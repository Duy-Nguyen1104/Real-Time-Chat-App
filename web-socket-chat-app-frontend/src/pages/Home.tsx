import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function Home() {
  const navigate = useNavigate();

  useEffect(() => {
    const checkAuth = () => {
      const token = localStorage.getItem("authToken");
      if (token) {
        navigate("/home");
      } else {
        navigate("/login");
      }
    };

    checkAuth();
  }, [navigate]);

  return null; // This page will redirect, so we don't need to render anything
}
