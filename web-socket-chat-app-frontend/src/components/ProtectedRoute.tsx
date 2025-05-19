import { Navigate, Outlet, redirect, replace } from "react-router-dom";
import { isAuthenticated } from "../services/authService";

interface ProtectedRouteProps {
  redirectPath?: string;
}

const ProtectedRoute = ({ redirectPath = "/login" }: ProtectedRouteProps) => {
  if (!isAuthenticated()) {
    return <Navigate to={redirectPath} />;
  }

  return <Outlet />;
};

export default ProtectedRoute;
