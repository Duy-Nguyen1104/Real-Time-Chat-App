export interface Conversation {
  id: string;
  displayName: string;
  lastMessage: string;
  lastMessageTime: string | null;
  senderId: string;
  receiverId: string;
  unreadCount: number;
  online: boolean;
  avatarColor?: string;
  category: string;
  chatId: string;
}

export interface Message {
  id: string;
  conversationId: string;
  sender: {
    id: string;
    name: string;
  };
  content: string;
  timestamp: string;
  read: boolean;
}

export interface User {
  id: string;
  name: string;
  phoneNumber: string;
  status: "online" | "offline";
}

export interface UserResponseDTO {
  id: string;
  name: string;
  phoneNumber: string;
  status: "online" | "offline";
}
