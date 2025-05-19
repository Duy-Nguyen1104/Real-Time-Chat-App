import axios from "axios";

const API_URL = "http://localhost:8080";

axios.defaults.baseURL = API_URL;

export const setAuthToken = (token: string | null) => {
  if (token) {
    axios.defaults.headers.common["Authorization"] = `Bearer ${token}`;
    localStorage.setItem("authToken", token);
  } else {
    delete axios.defaults.headers.common["Authorization"];
    localStorage.removeItem("authToken");
  }
};

export const loginUser = async (userData: {
  phoneNumber: String;
  password: String;
}) => {
  const response = await axios.post("/auth/login", userData);
  const { token, id, name } = response.data.data;

  localStorage.setItem("authToken", token);
  localStorage.setItem("userId", id);
  localStorage.setItem("userName", name);

  setAuthToken(token);
};

export const registerUser = async (userData: {
  phoneNumber: string;
  password: string;
  name: string;
}) => {
  const response = await axios.post("/auth/register", userData);
  return response.data;
};

export const resetPassword = async (
  phoneNumber: string,
  newPassword: string
) => {
  const response = await axios.post("/auth/reset-password", {
    phoneNumber,
    newPassword,
  });
  return response.data;
};

export const logoutUser = () => {
  localStorage.removeItem("authToken");
  localStorage.removeItem("userId");
  localStorage.removeItem("userName");
  setAuthToken(null);
};

export const isAuthenticated = () => {
  const token = localStorage.getItem("authToken");
  return !!token;
};

export const initializeAuth = () => {
  const token = localStorage.getItem("authToken");
  if (token) {
    setAuthToken(token);
  }
};
