import { Client, IMessage } from "@stomp/stompjs";
import { toast } from "react-toastify";
import { Message } from "../types";
import SockJS from "sockjs-client";

let stompClient: Client | null = null;
let reconnectAttempts = 0;
const MAX_RECONNECT_ATTEMPTS = 5;

export const connectWebSocket = (
  userId: string,
  onMessageReceived: (message: Message) => void
) => {
  if (stompClient) {
    console.log("WebSocket client already exists");
    return stompClient;
  }

  const client = new Client({
    webSocketFactory: () => new SockJS("http://localhost:8080/chat"),
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
    onConnect: () => {
      console.log("Connected to WebSocket");
      reconnectAttempts = 0;

      // Subscribe to personal message queue
      client.subscribe(
        `/user/${userId}/queue/messages`,
        (message: IMessage) => {
          try {
            const receivedMessage = JSON.parse(message.body);
            console.log("Received message via WebSocket:", receivedMessage);
            onMessageReceived(receivedMessage);
          } catch (error) {
            console.error("Error processing WebSocket message:", error);
          }
        }
      );
    },
    onStompError: (frame) => {
      console.error("STOMP error:", frame);
      toast.error("Connection error. Trying to reconnect...");
    },
    onWebSocketClose: () => {
      console.log("WebSocket connection closed");
    },
  });

  client.activate(); // Connect to the server
  stompClient = client;
  return client;
};

export const sendMessage = (message: any): boolean => {
  if (stompClient && stompClient.connected) {
    try {
      stompClient.publish({
        destination: "/app/chat",
        body: JSON.stringify(message),
      });
      console.log("Message sent via WebSocket:", message);
      return true;
    } catch (error) {
      console.error("Error sending message via WebSocket:", error);
      return false;
    }
  }
  console.warn("WebSocket not connected. Message not sent.");
  return false;
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    try {
      stompClient.deactivate();
      console.log("WebSocket disconnected");
    } catch (error) {
      console.error("Error disconnecting WebSocket:", error);
    }
    stompClient = null;
  }
};
