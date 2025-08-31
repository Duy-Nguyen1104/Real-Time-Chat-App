import { BrowserRouter, Routes, Route } from "react-router-dom";
import { ToastContainer } from "react-toastify";
import Login from "./pages/Login";
import Chat from "./pages/Chat";
import Home from "./pages/Home";
import Signup from "./pages/Signup";
import ForgotPasswordPage from "./pages/ForgotPasswordPage";
import { initializeAuth } from "./services/authService";
import ProtectedRoute from "./components/ProtectedRoute";

initializeAuth();

function App() {
  return (
    <>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<Signup />} />
          <Route path="/forgot-password" element={<ForgotPasswordPage />} />

          <Route element={<ProtectedRoute />}>
            <Route path="/home" element={<Chat />} />
          </Route>
        </Routes>
      </BrowserRouter>
      <ToastContainer />
    </>
  );
}

export default App;
