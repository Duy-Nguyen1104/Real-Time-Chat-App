import { useState, useEffect, useRef } from "react";
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
  XCircle,
} from "lucide-react";
import type { Conversation, UserResponseDTO } from "../types";
import Avatar from "./Avatar";
import Spinner from "./Spinner";
import axios from "axios";
import { toast } from "react-toastify";

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
  onInitiateConversation: (targetUserId: string) => void;
};

const formatTimestampToLocalTime = (
  isoTimestamp: string | null | undefined
): string => {
  if (!isoTimestamp) {
    return "";
  }
  try {
    const date = new Date(isoTimestamp);
    // Check if date is valid, as new Date(null) or new Date("") can result in Invalid Date
    if (isNaN(date.getTime())) {
      return ""; // Or some error/placeholder string
    }
    return date.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    console.error("Failed to format timestamp:", isoTimestamp, error);
    return "Invalid Time"; // Fallback for other errors
  }
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
  onInitiateConversation,
}: SidebarProps) {
  const [filter, setFilter] = useState("all");
  const [search, setSearch] = useState("");
  const [apiSearchResults, setApiSearchResults] = useState<UserResponseDTO[]>(
    []
  );
  const [localSearchResults, setLocalSearchResults] = useState<Conversation[]>(
    []
  );
  const [isSearching, setIsSearching] = useState(false);
  const [showSearchResults, setShowSearchResults] = useState(false);
  const [showCategoryDropdown, setShowCategoryDropdown] = useState(false);
  const [selectedCategory, setSelectedCategory] = useState("all");
  const [showSettingsDropdown, setShowSettingsDropdown] = useState(false);
  const searchContainerRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        searchContainerRef.current &&
        !searchContainerRef.current.contains(event.target as Node)
      ) {
        setShowSearchResults(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, []);

  const handleSearchChange = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const query = e.target.value;
    setSearch(query);

    if (query.trim() === "") {
      // If the search input is empty, reset the search results
      setApiSearchResults([]);
      setLocalSearchResults([]);
      setShowSearchResults(false);
      return;
    }

    setShowSearchResults(true);
    setIsSearching(true);

    // Heuristic: if query is all digits and a certain length
    const isPhoneNumber = /^\d{5,}$/.test(query);

    if (isPhoneNumber) {
      setLocalSearchResults([]); // Clear local results when searching by phone
      try {
        const token = localStorage.getItem("authToken");
        const response = await axios.get(
          `/api/users/search?query=${encodeURIComponent(query)}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setApiSearchResults(response.data.data || []);
      } catch (error: any) {
        console.error("Failed to search users by phone:", error);
        toast.error(
          error.response?.data?.message || "Failed to search users by phone."
        );
        setApiSearchResults([]);
      }
    } else {
      // Search by name (locally, in existing conversations)
      setApiSearchResults([]); // Clear API results when searching by name
      const filteredConversations = conversations.filter((conv) =>
        conv.displayName.toLowerCase().includes(query.toLowerCase())
      );
      setLocalSearchResults(filteredConversations);
    }
    setIsSearching(false);
  };

  const handleApiResultClick = async (user: UserResponseDTO) => {
    setShowSearchResults(false);
    setSearch("");
    setApiSearchResults([]);
    setLocalSearchResults([]);

    const existingConv = conversations.find(
      (c) =>
        (c.senderId === currentUser.id && c.receiverId === user.id) ||
        (c.senderId === user.id && c.receiverId === currentUser.id)
    );

    if (existingConv) {
      setActiveConversation(existingConv.id);
    } else {
      await onInitiateConversation(user.id);
    }
  };

  const handleLocalResultClick = (conversation: Conversation) => {
    setShowSearchResults(false);
    setSearch("");
    setApiSearchResults([]);
    setLocalSearchResults([]);
    setActiveConversation(conversation.id);
  };

  const clearSearch = () => {
    setSearch("");
    setApiSearchResults([]);
    setLocalSearchResults([]);
    setShowSearchResults(false);
  };

  //Filter conversation
  const displayedConversations = conversations.filter((conversation) => {
    const matchesFilter =
      filter === "all" || (filter === "unread" && conversation.unreadCount > 0);

    const matchesCategory =
      selectedCategory === "all"
        ? true
        : conversation.category === selectedCategory;

    return matchesFilter && matchesCategory;
  });

  const highlightUnread = (conversation: Conversation) => {
    return (
      conversation.unreadCount > 0 //&&
      // conversation.lastMessageSenderId !== currentUser.id
    );
  };

  const categories = ["all", "work", "family", "friends"];

  return (
    <div className="w-80 flex flex-col border-r border-[#2a2a2a] bg-[#1a1a1a]">
      {/* Profile and search */}
      <div className="p-2 flex items-center border-b border-[#2a2a2a]">
        <div className="flex-shrink-0">
          <Avatar name={currentUser.name} size="md" />
        </div>
        <div className="flex-1 ml-2 relative" ref={searchContainerRef}>
          <div className="relative">
            <Search className="absolute left-2 top-1/2 transform -translate-y-1/2 text-gray-400 h-4 w-4" />
            <input
              type="text"
              placeholder="Search"
              className="w-full bg-[#2a2a2a] rounded-full py-1.5 pl-8 pr-2 text-sm focus:outline-none"
              value={search}
              onChange={handleSearchChange}
              onFocus={() => setShowSearchResults(true)}
            />
            {search && (
              <XCircle
                className="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-500 hover:text-gray-300 cursor-pointer h-4 w-4"
                onClick={clearSearch}
              />
            )}
          </div>
          {showSearchResults && (
            <div className="absolute top-full mt-1 w-full bg-[#252525] rounded-md shadow-lg z-20 max-h-60 overflow-y-auto border border-[#3a3a3a]">
              {isSearching && (
                <div className="p-3 text-sm text-gray-400 text-center">
                  Searching...
                </div>
              )}
              {!isSearching &&
                apiSearchResults.map((user) => (
                  <div
                    key={user.id}
                    className="flex items-center p-2.5 cursor-pointer hover:bg-[#3a3a3a] transition-colors duration-150"
                    onClick={() => handleApiResultClick(user)}
                  >
                    <Avatar
                      name={user.name}
                      size="sm"
                      bgColor={
                        user.status === "online"
                          ? "bg-green-500"
                          : "bg-gray-500"
                      }
                    />
                    <div className="ml-2.5">
                      <p className="text-sm text-white font-medium">
                        {user.name}
                      </p>
                      <p className="text-xs text-gray-400">
                        {user.phoneNumber}
                      </p>
                    </div>
                  </div>
                ))}
              {/* Local Search Results (Conversations) */}
              {!isSearching &&
                localSearchResults.map((conv) => (
                  <div
                    key={conv.id}
                    className="flex items-center p-2.5 cursor-pointer hover:bg-[#3a3a3a] transition-colors duration-150"
                    onClick={() => handleLocalResultClick(conv)}
                  >
                    <Avatar
                      name={conv.displayName}
                      size="sm"
                      bgColor={conv.avatarColor}
                    />
                    <div className="ml-2.5">
                      <p className="text-sm text-white font-medium">
                        {conv.displayName}
                      </p>
                    </div>
                  </div>
                ))}
            </div>
          )}
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
      </div>

      {/* Conversation list */}
      <div className="flex-1 overflow-y-auto">
        {isLoading ? (
          <Spinner />
        ) : displayedConversations.length > 0 ? (
          displayedConversations.map((conversation) => (
            <div
              key={conversation.id}
              className={`flex items-center p-2 cursor-pointer ${
                activeConversation?.id === conversation.id
                  ? "bg-[#2a2a2a]"
                  : "hover:bg-[#232323]"
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
                    {formatTimestampToLocalTime(conversation.lastMessageTime)}
                  </span>
                </div>
                <div className="flex items-center">
                  <p
                    className={`text-sm truncate ${
                      highlightUnread(conversation)
                        ? "text-white font-medium"
                        : "text-gray-400"
                    }`}
                  >
                    {conversation.lastMessage}
                  </p>
                  {highlightUnread(conversation) && (
                    <span className="ml-auto pl-2 flex-shrink-0">
                      <div className="w-2 h-2 bg-blue-500 rounded-full" />
                    </span>
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
