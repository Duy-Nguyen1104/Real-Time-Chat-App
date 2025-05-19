// filepath: c:\Code\Final-Chat-App\web-socket-chat-app-frontend\src\pages\ForgotPasswordPage.tsx
import { ChangeEvent, FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import Spinner from "../components/Spinner";
import { resetPassword } from "../services/authService";
import { Link } from "react-router-dom";

function ForgotPasswordPage() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    phoneNumber: "",
    password: "",
    confirmPassword: "",
  });
  const [isLoading, setIsLoading] = useState(false);
  const [errors, setErrors] = useState<{
    phoneNumber?: string;
    password?: string;
    confirmPassword?: string;
    general?: string;
  }>({});

  const { phoneNumber, password, confirmPassword } = formData;

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
    setErrors({ ...errors, [e.target.name]: undefined, general: undefined });
  };

  const validateForm = () => {
    const newErrors: {
      phoneNumber?: string;
      password?: string;
      confirmPassword?: string;
    } = {};
    if (!phoneNumber) newErrors.phoneNumber = "Phone number is required.";
    if (!password) {
      newErrors.password = "New password is required.";
    } else if (password.length < 6) {
      newErrors.password = "Password must be at least 6 characters.";
    }
    if (password !== confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match.";
    }
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setErrors({});

    if (!validateForm()) {
      toast.error("Please correct the errors in the form.");
      return;
    }

    setIsLoading(true);
    try {
      await resetPassword(phoneNumber, password);
      toast.success(
        "Password reset successfully. Please login with your new password."
      );
      navigate("/login");
    } catch (err: any) {
      const errorMessage =
        err.response?.data?.message ||
        "Failed to reset password. Please try again.";
      setErrors({ general: errorMessage });
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#121212] py-12 px-4 sm:px-6 lg:px-8">
      <div className="max-w-md w-full space-y-8">
        <div>
          <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
            Reset Your Password
          </h2>
        </div>
        <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
          {errors.general && (
            <p className="text-red-500 text-sm text-center py-2">
              {errors.general}
            </p>
          )}
          <div className="rounded-md shadow-sm -space-y-px">
            <div>
              <label htmlFor="phone-number-forgot" className="sr-only">
                Phone Number
              </label>
              <input
                id="phone-number-forgot"
                name="phoneNumber"
                type="tel"
                required
                className={`appearance-none rounded-none relative block w-full px-3 py-2 border ${
                  errors.phoneNumber ? "border-red-500" : "border-gray-300"
                } placeholder-gray-500 text-white rounded-t-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm`}
                placeholder="Phone Number"
                value={phoneNumber}
                onChange={handleChange}
              />
              {errors.phoneNumber && (
                <p className="text-red-500 text-xs mt-1">
                  {errors.phoneNumber}
                </p>
              )}
            </div>
            <div>
              <label htmlFor="password-forgot" className="sr-only">
                New Password
              </label>
              <input
                id="password-forgot"
                name="password"
                type="password"
                required
                className={`appearance-none rounded-none relative block w-full px-3 py-2 border ${
                  errors.password ? "border-red-500" : "border-gray-300"
                } placeholder-gray-500 text-white focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm`}
                placeholder="New Password"
                value={password}
                onChange={handleChange}
              />
            </div>
            <div>
              <label htmlFor="confirmPassword-forgot" className="sr-only">
                Confirm New Password
              </label>
              <input
                id="confirmPassword-forgot"
                name="confirmPassword"
                type="password"
                required
                className={`appearance-none rounded-none relative block w-full px-3 py-2 border ${
                  errors.confirmPassword ? "border-red-500" : "border-gray-300"
                } placeholder-gray-500 text-white rounded-b-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm`}
                placeholder="Confirm New Password"
                value={confirmPassword}
                onChange={handleChange}
              />
              <p
                className={`text-xs mt-1 ${
                  password === confirmPassword && password !== ""
                    ? "text-green-400"
                    : "text-gray-500"
                }`}
              >
                {password === confirmPassword && password !== ""
                  ? "✓ Passwords match"
                  : "Passwords do not match"}
              </p>

              <p
                className={`text-xs mt-1 ${
                  password.length >= 6 ? "text-green-400" : "text-gray-500"
                }`}
              >
                {password.length >= 6
                  ? "✓ Password meets minimum length"
                  : "Password must be at least 6 characters"}
              </p>
            </div>
          </div>
          <div>
            <button
              type="submit"
              disabled={isLoading}
              className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500 disabled:opacity-50"
            >
              {isLoading ? <Spinner /> : "Reset Password"}
            </button>
          </div>
          <div className="text-center text-sm text-gray-300 mt-3">
            Remember your password?{" "}
            <Link
              to="/login"
              className="font-bold text-blue-500 hover:text-blue-400"
            >
              Login
            </Link>
          </div>
        </form>
      </div>
    </div>
  );
}

export default ForgotPasswordPage;
