import { useState, useEffect, useCallback, useRef } from "react";
import axios from "axios";
import Sidebar from "../components/Sidebar";
import ChatArea from "../components/ChatArea";
import type { Conversation, Message } from "../types";
import { logoutUser } from "../services/authService";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import {
  connectWebSocket,
  sendMessage,
  disconnectWebSocket,
} from "../services/websocketService";

axios.defaults.baseURL = "http://localhost:8080";

function Chat() {
  const navigate = useNavigate();
  const [activeConversationId, setActiveConversationId] = useState<
    string | null
  >(null);
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [messages, setMessages] = useState<Message[]>([]);
  const [isLoadingConversations, setIsLoadingConversations] = useState(true);
  const [isLoadingMessages, setIsLoadingMessages] = useState(false);
  const [error, setError] = useState("");
  const [currentUser, setCurrentUser] = useState({
    id: localStorage.getItem("userId") || "",
    name: localStorage.getItem("userName") || "",
  });

  // Reference to track if WebSocket is connected
  const webSocketConnected = useRef(false);
  // Reference to track current active conversation for WebSocket handler
  const activeConversationRef = useRef<string | null>(null);

  // Update ref whenever activeConversationId changes
  useEffect(() => {
    activeConversationRef.current = activeConversationId;
  }, [activeConversationId]);

  useEffect(() => {
    if (!currentUser.id || webSocketConnected.current) return;

    const handleMessageReceived = (newMessage: Message) => {
      console.log("Message received in handler:", newMessage);

      // Check if the message is for the currently active conversation
      const isForActiveConversation =
        activeConversationRef.current === newMessage.conversationId;

      if (isForActiveConversation) {
        // Add message to current conversation's messages (avoid duplicates)
        setMessages((prevMessages) => {
          // Don't add duplicate messages - check if message already exists
          const messageExists = prevMessages.some(
            (msg) =>
              msg.id === newMessage.id ||
              (msg.content === newMessage.content &&
                msg.sender.id === newMessage.sender.id &&
                Math.abs(
                  new Date(msg.timestamp).getTime() -
                    new Date(newMessage.timestamp).getTime()
                ) < 1000)
          );

          if (!messageExists) {
            return [...prevMessages, newMessage];
          }
          return prevMessages;
        });
      }

      // Always update conversations list with new message details
      setConversations((prev) => {
        const updatedConversations = [...prev];
        const conversationIndex = updatedConversations.findIndex(
          (conv) => conv.id === newMessage.conversationId
        );
        if (conversationIndex !== -1) {
          // Update the conversation with new message data using the message's timestamp
          const updatedConversation = {
            ...updatedConversations[conversationIndex],
            lastMessage: newMessage.content,
            lastMessageTime: newMessage.timestamp, // Use message timestamp instead of current time
            // Only increment unread count if this conversation is not currently active
            unreadCount: isForActiveConversation
              ? 0
              : updatedConversations[conversationIndex].unreadCount + 1,
          };

          // Remove the conversation from the array
          updatedConversations.splice(conversationIndex, 1);

          // Add it back at the beginning (newest messages first)
          return [updatedConversation, ...updatedConversations];
        }

        return prev;
      });
    };

    connectWebSocket(currentUser.id, handleMessageReceived);
    webSocketConnected.current = true;

    return () => {
      console.log("Disconnecting WebSocket...");
      disconnectWebSocket();
      webSocketConnected.current = false;
    };
  }, [currentUser.id]); // Remove activeConversationId dependency

  // Function to set active conversation by ID
  const handleSetActiveConversation = useCallback(
    (id: string) => {
      // Only clear messages if switching to a different conversation
      if (activeConversationId !== id) {
        setMessages([]); // Clear messages when changing conversation
      }
      setActiveConversationId(id);
    },
    [activeConversationId]
  );

  // Fetch conversations on component mount
  useEffect(() => {
    const fetchConversations = async () => {
      // Check if current user id exists
      if (!currentUser.id) {
        navigate("/login");
        return;
      }

      setIsLoadingConversations(true);
      try {
        const { data } = await axios.get(
          `/conversations/user/${currentUser.id}`
        );
        setConversations(data.data || []);
        setError("");
      } catch (err: any) {
        console.error("Failed to fetch conversations", err);
        setError("Failed to load conversations");

        // If unauthorized, redirect to login
        if (err.response?.status === 401) {
          logoutUser();
          navigate("/login");
        }
      } finally {
        setIsLoadingConversations(false);
      }
    };

    fetchConversations();
  }, [currentUser.id, navigate]);

  // Fetch messages when active conversation changes
  useEffect(() => {
    if (!activeConversationId) return;

    const activeConversation = conversations.find(
      (c) => c.id === activeConversationId
    );
    if (!activeConversation) return;

    const fetchMessages = async () => {
      setIsLoadingMessages(true);
      try {
        // Get the other user's ID
        const receiverId =
          activeConversation.senderId === currentUser.id
            ? activeConversation.receiverId
            : activeConversation.senderId;

        const { data } = await axios.get(
          `/messages/${currentUser.id}/${receiverId}`
        );

        // Clear messages first, then set new ones for the active conversation
        setMessages(data.data || []);

        // Mark conversation as read
        await axios.post(`/conversations/${activeConversationId}/read`);

        // Update the local conversations list to reflect read status
        setConversations((prev) =>
          prev.map((conv) =>
            conv.id === activeConversationId
              ? { ...conv, unreadCount: 0 }
              : conv
          )
        );
      } catch (err) {
        console.error("Failed to fetch messages", err);
        toast.error("Failed to load messages");
      } finally {
        setIsLoadingMessages(false);
      }
    };

    fetchMessages();
  }, [activeConversationId, currentUser.id]);

  // Modify the handleSendMessage function
  const handleSendMessage = async (content: string) => {
    if (!activeConversationId || !content.trim()) return;

    const activeConversation = conversations.find(
      (c) => c.id === activeConversationId
    );
    if (!activeConversation) return;

    const receiverId =
      activeConversation.senderId === currentUser.id
        ? activeConversation.receiverId
        : activeConversation.senderId;

    try {
      // Format current time consistently for display
      const now = new Date();

      const message = {
        senderId: currentUser.id,
        receiverId,
        content,
        conversationId: activeConversationId,
        timestamp: now.toISOString(),
      };

      // Don't add temporary UI message - let WebSocket handle all message updates
      const socketSent = sendMessage(message);

      if (!socketSent) {
        console.log("WebSocket send failed, using API fallback");
        await axios.post("/messages", message);
      }

      // Update the conversation's last message time optimistically for better UX
      setConversations((prev) => {
        const updatedConversations = [...prev];
        const conversationIndex = updatedConversations.findIndex(
          (conv) => conv.id === activeConversationId
        );
        if (conversationIndex !== -1) {
          const updatedConversation = {
            ...updatedConversations[conversationIndex],
            lastMessage: content,
            lastMessageTime: now.toISOString(),
          };

          // Remove the conversation from the array
          updatedConversations.splice(conversationIndex, 1);

          // Add it back at the beginning (newest messages first)
          return [updatedConversation, ...updatedConversations];
        }
        return prev;
      });
    } catch (err) {
      console.error("Failed to send message", err);
      toast.error("Failed to send message");
    }
  };

  const handleInitiateConversation = async (targetUserId: string) => {
    if (!currentUser.id || !targetUserId) {
      toast.error(
        "Cannot inititiate conversation. User information is misssing."
      );
      return;
    }

    try {
      const token = localStorage.getItem("authToken");
      const response = await axios.post(
        "/conversations",
        {
          senderId: currentUser.id,
          receiverId: targetUserId,
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      const newConversation: Conversation = response.data.data;

      setConversations((prev) => {
        const existingIndex = prev.findIndex(
          (c) => c.id === newConversation.id
        );
        if (existingIndex !== -1) {
          // If conversation exists, move it to the top
          const updatedExisting = {
            ...prev[existingIndex],
            ...newConversation,
          };
          const rest = prev.filter((c) => c.id !== newConversation.id);
          return [updatedExisting, ...rest];
        }
        return [newConversation, ...prev];
      });
      setActiveConversationId(newConversation.id);
    } catch (err: any) {
      console.error("Failed to initiate conversation:", err);
      const errorMsg =
        err.response?.data?.message ||
        "Failed to start chat. Please try again.";
      toast.error(errorMsg);
    }
  };

  const handleLogout = () => {
    logoutUser();
    navigate("/login");
  };

  // Find the active conversation based on activeConversationId
  const activeConversation = conversations.find(
    (conv) => conv.id === activeConversationId
  );

  return (
    <div className="flex h-screen bg-[#121212]">
      <Sidebar
        activeConversation={activeConversation || null}
        conversations={conversations}
        isLoading={isLoadingConversations}
        error={error}
        setActiveConversation={handleSetActiveConversation}
        onLogout={handleLogout}
        currentUser={currentUser}
        onInitiateConversation={handleInitiateConversation}
      />

      <div className="flex-1 flex">
        {activeConversation ? (
          <ChatArea
            conversation={activeConversation}
            messages={messages}
            onSendMessage={handleSendMessage}
            currentUserId={currentUser.id}
          />
        ) : (
          <div className="flex-1 flex items-center justify-center text-gray-400">
            Select a conversation to start chatting
          </div>
        )}
      </div>
    </div>
  );
}

export default Chat;
