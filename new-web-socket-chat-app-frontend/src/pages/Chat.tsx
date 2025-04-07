import { useState, useEffect } from "react";
import axios from "axios";
import Sidebar from "../components/Sidebar";
import type { Conversation } from "../types";
import { logoutUser } from "../services/authService";
import { useNavigate } from "react-router-dom";

axios.defaults.baseURL = "http://localhost:8080";

function Chat() {
  const navigate = useNavigate();
  const [activeConversation, setActiveConversation] =
    useState<Conversation | null>(null);
  const [conversations, setConversations] = useState<Conversation[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [currentUser, setCurrentUser] = useState({
    id: localStorage.getItem("userId") || "",
    name: localStorage.getItem("userName") || "",
  });

  // Function to set active conversation by ID
  const handleSetActiveConversation = (id: string) => {
    const conversation = conversations.find((conv) => conv.id === id);
    setActiveConversation(conversation || null);
  };

  // Fetch conversations on component mount
  useEffect(() => {
    const fetchConversations = async () => {
      //Check if current user id exists
      if (!currentUser.id) {
        navigate("/login");
        return;
      }

      setIsLoading(true);
      try {
        const { data } = await axios.get(
          `/conversations/user/${currentUser.id}`
        );
        setConversations(data.data || []);
        setError("");
      } catch (err) {
        console.error("Failed to fetch conversations", err);
        setError("Failed to load conversations");
      } finally {
        setIsLoading(false);
      }
    };

    fetchConversations();
  }, []);

  const handleLogout = () => {
    logoutUser();
    navigate("/login");
  };

  return (
    <div className="flex h-screen">
      <Sidebar
        activeConversation={activeConversation}
        conversations={conversations}
        isLoading={isLoading}
        error={error}
        setActiveConversation={handleSetActiveConversation}
        onLogout={handleLogout}
        currentUser={currentUser}
      />

      <div className="flex-1 bg-[#121212] flex items-center justify-center">
        {activeConversation ? (
          <div>Active conversation content will go here</div>
        ) : (
          <div className="text-gray-400">
            Select a conversation to start chatting
          </div>
        )}
      </div>
    </div>
  );
}

export default Chat;
