import { useEffect, useRef, useState } from "react";
import {
  Phone,
  Video,
  Search,
  Paperclip,
  ImageIcon,
  Smile,
  Send,
  MoreHorizontal,
} from "lucide-react";
import type { Conversation, Message } from "../types";
import Avatar from "./Avatar";
import MessageBubble from "./MessageBubble";
import { formatDateForSeparator } from "../utils/timestamp";

interface ChatAreaProps {
  conversation: Conversation;
  messages: Message[];
  onSendMessage: (content: string) => void;
  currentUserId: string;
}

export default function ChatArea({
  conversation,
  messages,
  onSendMessage,
  currentUserId,
}: ChatAreaProps) {
  const [messageInput, setMessageInput] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();

    if (messageInput.trim()) {
      onSendMessage(messageInput);
      setMessageInput("");
    }
  };

  return (
    <div className="flex-1 flex flex-col bg-[#121212] h-full">
      {/* Chat header */}
      <div className="flex items-center justify-between p-3 border-b border-[#2a2a2a]">
        <div className="flex items-center">
          <div className="relative">
            <Avatar
              name={conversation.displayName}
              bgColor={conversation.avatarColor}
              size="md"
            />
            {conversation.online && (
              <div className="absolute bottom-0 right-0 w-2.5 h-2.5 bg-green-500 rounded-full border-2 border-[#121212]"></div>
            )}
          </div>
          <div className="ml-3">
            <h2 className="font-medium">{conversation.displayName}</h2>
            {conversation.online ? (
              <p className="text-xs text-gray-400">Online</p>
            ) : (
              <p className="text-xs text-gray-400">Offline</p>
            )}
          </div>
        </div>
        <div className="flex space-x-2">
          <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
            <Phone size={20} className="text-gray-400" />
          </button>
          <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
            <Video size={20} className="text-gray-400" />
          </button>
          <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
            <Search size={20} className="text-gray-400" />
          </button>
          <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
            <MoreHorizontal size={20} className="text-gray-400" />
          </button>
        </div>
      </div>

      {/* Messages area */}
      <div className="flex-1 overflow-y-auto p-4">
        <div className="space-y-4">
          {messages.length > 0 ? (
            (() => {
              let lastDisplayedDate: string | null = null;
              return messages.map((message, index) => {
                const messageDate = formatDateForSeparator(message.timestamp);
                let dateSeparator = null;

                if (messageDate !== lastDisplayedDate) {
                  dateSeparator = (
                    <div
                      key={`date-${messageDate}-${index}`}
                      className="text-center text-xs text-gray-500 my-4"
                    >
                      {messageDate}
                    </div>
                  );
                  lastDisplayedDate = messageDate;
                }

                return (
                  <>
                    {dateSeparator}
                    <MessageBubble
                      key={message.id}
                      message={message}
                      isCurrentUser={message.sender.id === currentUserId}
                    />
                  </>
                );
              });
            })()
          ) : (
            <div className="text-center text-gray-400 my-8">
              No messages yet. Start a conversation!
            </div>
          )}
          <div ref={messagesEndRef} />
        </div>
      </div>

      {/* Message input */}
      <div className="p-3 border-t border-[#2a2a2a]">
        <form onSubmit={handleSendMessage} className="flex items-center">
          <div className="flex space-x-2 mr-2">
            <button
              type="button"
              className="p-2 rounded-full hover:bg-[#2a2a2a]"
            >
              <Paperclip size={20} className="text-gray-400" />
            </button>
            <button
              type="button"
              className="p-2 rounded-full hover:bg-[#2a2a2a]"
              onClick={() => fileInputRef.current?.click()}
            >
              <ImageIcon size={20} className="text-gray-400" />
            </button>
            <input
              type="file"
              ref={fileInputRef}
              className="hidden"
              accept="image/*"
            />
          </div>
          <div className="flex-1 bg-[#2a2a2a] rounded-full flex items-center">
            <input
              type="text"
              value={messageInput}
              onChange={(e) => setMessageInput(e.target.value)}
              placeholder={`Message ${conversation.displayName}...`}
              className="flex-1 bg-transparent py-2 px-4 focus:outline-none text-white"
            />
            <button type="button" className="p-2 mr-1">
              <Smile size={20} className="text-gray-400" />
            </button>
          </div>
          <button
            type="submit"
            className="ml-2 p-2 rounded-full bg-blue-600 hover:bg-blue-700 disabled:opacity-50"
            disabled={!messageInput.trim()}
          >
            <Send size={20} className="text-white" />
          </button>
        </form>
      </div>
    </div>
  );
}
