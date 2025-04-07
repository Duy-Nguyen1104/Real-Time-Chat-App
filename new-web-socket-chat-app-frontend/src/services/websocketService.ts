import { Client } from "@stomp/stompjs";

let stompClient: Client | null = null;

export const connectWebSocket = (
  userId: string,
  onMessageReceived: (message: any) => void
) => {
  const client = new Client({
    brokerURL: "ws://localhost:8080/chat",
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  });

  client.onConnect = () => {
    //Subscribe to the queue
    client.subscribe(`/user/${userId}/queue/messages`, (message) => {
      const receivedMessage = JSON.parse(message.body);
      onMessageReceived(receivedMessage);
    });

    // Subscribe to user status updates
    client.subscribe("/user/topic", (message) => {
      console.log("User status update:", JSON.parse(message.body));
    });
  };
  client.activate(); // Connect to the server
  stompClient = client;
  return client;
};

export const sendMessage = (message: any) => {
  if (stompClient && stompClient.connected) {
    stompClient.publish({
      destination: "/app/chat",
      body: JSON.stringify(message),
    });
    return true;
  }
  return false;
};

export const disconnectWebSocket = () => {
  if (stompClient) {
    stompClient.deactivate();
    stompClient = null;
  }
};
