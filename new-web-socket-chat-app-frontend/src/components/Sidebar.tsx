import { useState, useEffect } from "react";
import {
  Search,
  Users,
  MessageSquare,
  Phone,
  Settings,
  Cloud,
  User,
  ChevronDown,
  LogOut,
} from "lucide-react";
import type { Conversation } from "../types";
import Avatar from "./Avatar";
import Spinner from "./Spinner";

type SidebarProps = {
  activeConversation: Conversation | null;
  conversations: Conversation[];
  isLoading: boolean;
  error: string;
  setActiveConversation: (id: string) => void;
  onLogout: () => void;
  currentUser: {
    id: string;
    name: string;
  };
};

// Remove the conversation fetching logic from Sidebar.tsx
function Sidebar({
  activeConversation,
  conversations,
  isLoading,
  error,
  setActiveConversation,
  onLogout,
  currentUser,
}: SidebarProps) {
  const [filter, setFilter] = useState("all");
  const [search, setSearch] = useState("");
  const [showCategoryDropdown, setShowCategoryDropdown] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [showSettingsDropdown, setShowSettingsDropdown] = useState(false);
  const [animatedConversations, setAnimatedConversations] = useState<
    string | null
  >(null);

  useEffect(() => {
    if (conversations.length > 0) {
      setAnimatedConversations(conversations[0].id);
      const timer = setTimeout(() => setAnimatedConversations(null), 500);
      return () => clearTimeout(timer);
    }
  }, [conversations]);

  //Filter conversation
  const filteredConversations = conversations.filter((conversation) => {
    const matchesSearch = conversation.displayName
      .toLowerCase()
      .includes(search.toLowerCase());

    const matchesFilter =
      filter === "all" || (filter === "unread" && conversation.unreadCount > 0);

    const matchesCategory =
      selectedCategory === "all" || conversation.category === selectedCategory;

    return matchesSearch && matchesFilter && matchesCategory;
  });

  const categories = ["all", "work", "family", "friends"];

  return (
    <div className="w-80 flex flex-col border-r border-[#2a2a2a] bg-[#1a1a1a]">
      {/* Profile and search */}
      <div className="p-2 flex items-center border-b border-[#2a2a2a]">
        <div className="flex-shrink-0">
          <Avatar name={currentUser.name} size="md" />
        </div>
        <div className="flex-1 ml-2">
          <div className="relative">
            <Search className="absolute left-2 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <input
              type="text"
              placeholder="Tìm kiếm"
              className="w-full bg-[#2a2a2a] rounded-full py-1.5 pl-8 pr-2 text-sm focus:outline-none"
              value={search}
              onChange={(e) => setSearch(e.target.value)}
            />
          </div>
        </div>
        <div className="flex space-x-1 ml-1">
          <button className="p-1 rounded-full hover:bg-[#2a2a2a]">
            <Users size={20} className="text-gray-400" />
          </button>
          <button className="p-1 rounded-full hover:bg-[#2a2a2a]">
            <MessageSquare size={20} className="text-gray-400" />
          </button>
        </div>
      </div>

      {/* Filters */}
      <div className="flex px-2 py-1 border-b border-[#2a2a2a]">
        <button
          className={`flex-1 py-1.5 text-sm font-medium ${
            filter === "all" ? "text-white" : "text-gray-400"
          }`}
          onClick={() => setFilter("all")}
        >
          All
        </button>
        <button
          className={`flex-1 py-1.5 text-sm font-medium ${
            filter === "unread" ? "text-white" : "text-gray-400"
          }`}
          onClick={() => setFilter("unread")}
        >
          Unread
        </button>
        <div className="relative">
          <button
            className="py-1.5 px-2 text-sm font-medium text-gray-400 flex items-center"
            onClick={() => setShowCategoryDropdown(!showCategoryDropdown)}
          >
            Categorize
            <ChevronDown size={16} className="ml-1" />
          </button>

          {showCategoryDropdown && (
            <div className="absolute right-0 mt-1 w-40 bg-[#2a2a2a] rounded-md shadow-lg z-10">
              {categories.map((category) => (
                <button
                  key={category}
                  className={`block w-full text-left px-4 py-2 text-sm ${
                    selectedCategory === category
                      ? "bg-[#3a3a3a] text-white"
                      : "text-gray-300 hover:bg-[#3a3a3a]"
                  }`}
                  onClick={() => {
                    setSelectedCategory(category);
                    setShowCategoryDropdown(false);
                  }}
                >
                  {category}
                </button>
              ))}
            </div>
          )}
        </div>
        <button className="py-1.5 px-2 text-gray-400">
          <svg
            xmlns="http://www.w3.org/2000/svg"
            width="20"
            height="20"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            strokeWidth="2"
            strokeLinecap="round"
            strokeLinejoin="round"
          >
            <circle cx="12" cy="12" r="1" />
            <circle cx="19" cy="12" r="1" />
            <circle cx="5" cy="12" r="1" />
          </svg>
        </button>
      </div>

      {/* Conversation list */}
      <div className="flex-1 overflow-y-auto">
        {isLoading ? (
          <Spinner />
        ) : filteredConversations.length > 0 ? (
          filteredConversations.map((conversation) => (
            <div
              key={conversation.id}
              className={`flex items-center p-2 cursor-pointer ${
                activeConversation?.id === conversation.id
                  ? "bg-[#2a2a2a]"
                  : "hover:bg-[#232323]"
              } ${
                animatedConversations === conversation.id
                  ? "animate-slide-down"
                  : ""
              } transition-all duration-300 ease-in-out`}
              onClick={() => setActiveConversation(conversation.id)}
            >
              <div className="relative">
                <Avatar
                  name={conversation.displayName}
                  bgColor={conversation.avatarColor}
                  size="lg"
                />
                {conversation.online && (
                  <div className="absolute bottom-0 right-0 w-3 h-3 bg-green-500 rounded-full border-2 border-[#1a1a1a]"></div>
                )}
              </div>
              <div className="ml-3 flex-1 min-w-0">
                <div className="flex justify-between">
                  <span className="font-medium truncate">
                    {conversation.displayName}
                  </span>
                  <span className="text-xs text-gray-400">
                    {conversation.lastMessageTime}
                  </span>
                </div>
                <div className="flex items-center">
                  <p className="text-sm text-gray-400 truncate">
                    {conversation.lastMessage}
                  </p>
                  {conversation.unreadCount > 0 && (
                    <div className="ml-2 flex-shrink-0 w-5 h-5 bg-red-500 rounded-full flex items-center justify-center">
                      <span className="text-xs">
                        {conversation.unreadCount}
                      </span>
                    </div>
                  )}
                </div>
              </div>
            </div>
          ))
        ) : (
          <div className="p-4 text-center text-gray-400">
            Can not find any conversation
          </div>
        )}
      </div>

      {/* Bottom navigation */}
      <div className="flex justify-around py-2 border-t border-[#2a2a2a] bg-[#1a1a1a]">
        <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
          <MessageSquare size={20} className="text-gray-400" />
        </button>
        <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
          <Phone size={20} className="text-gray-400" />
        </button>
        <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
          <User size={20} className="text-gray-400" />
        </button>
        <button className="p-2 rounded-full hover:bg-[#2a2a2a]">
          <Cloud size={20} className="text-gray-400" />
        </button>
        <div className="relative">
          <button
            className="p-2 rounded-full hover:bg-[#2a2a2a]"
            onClick={() => setShowSettingsDropdown(!showSettingsDropdown)}
          >
            <Settings size={20} className="text-gray-400" />
          </button>
          {showSettingsDropdown && (
            <div className="absolute right-0 bottom-full mb-2 w-48 bg-[#2a2a2a] rounded-md shadow-lg z-10">
              <button
                className="block w-full text-left px-4 py-2 text-sm text-gray-300 hover:bg-[#3a3a3a]"
                onClick={onLogout}
              >
                <LogOut size={16} className="inline mr-2" />
                Log out
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default Sidebar;
