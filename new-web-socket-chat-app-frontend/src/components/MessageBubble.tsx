import type { Message } from "../types";
import { Check, CheckCheck } from "lucide-react";

interface MessageBubbleProps {
  message: Message;
  isCurrentUser: boolean;
}

export default function MessageBubble({
  message,
  isCurrentUser,
}: MessageBubbleProps) {
  const formatTime = (timestamp: string) => {
    try {
      const date = new Date(timestamp);
      return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      });
    } catch (error) {
      return "Invalid time";
    }
  };

  return (
    <div
      className={`flex ${isCurrentUser ? "justify-end" : "justify-start"} mb-4`}
    >
      <div
        className={`max-w-[70%] rounded-lg px-4 py-2 ${
          isCurrentUser
            ? "bg-blue-600 text-white rounded-br-none"
            : "bg-[#2a2a2a] text-white rounded-bl-none"
        }`}
      >
        {message.content}
        <div
          className={`text-xs mt-1 flex items-center ${
            isCurrentUser ? "justify-end" : ""
          }`}
        >
          <span
            className={`${isCurrentUser ? "text-gray-300" : "text-gray-400"}`}
          >
            {formatTime(message.timestamp)}
          </span>
          {isCurrentUser && (
            <span className="ml-1">
              {message.read ? (
                <CheckCheck size={14} className="text-blue-300" />
              ) : (
                <Check size={14} className="text-gray-300" />
              )}
            </span>
          )}
        </div>
      </div>
    </div>
  );
}
