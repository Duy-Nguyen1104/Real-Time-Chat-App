import { ChangeEvent, FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import Spinner from "../components/Spinner";
import { Link } from "react-router-dom";
import axios from "axios";
import { loginUser, setAuthToken } from "../services/authService";
import { validateLoginForm, ValidationError } from "../utils/validation";
import { handleFormSubmission } from "../utils/apiResponseHandler";

axios.defaults.baseURL = "http://localhost:8080";

interface FormData {
  phoneNumber: string;
  password: string;
}

function Login() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState<FormData>({
    phoneNumber: "",
    password: "",
  });

  const [errors, setErrors] = useState<ValidationError>({});

  const [isLoading, setIsLoading] = useState(false);

  const { phoneNumber, password } = formData;

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const validateForm = () => {
    const result = validateLoginForm(phoneNumber, password);
    setErrors(result.errors);
    return result.isValid;
  };

  const handleSubmit = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    await handleFormSubmission(formData, loginUser, {
      setLoading: setIsLoading,
      setErrors,
      onSuccess: () => {
        navigate("/home");
      },
      successMessage: "Login successful",
      errorMessage: "Login failed. Please check your credentials.",
    });
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-[#121212] py-12 px-4 sm:px-6 lg:px-8">
      {isLoading ? (
        <Spinner />
      ) : (
        <div className="max-w-md w-full space-y-8">
          <div>
            <img src="" alt="" />
            <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
              Login to the Chat App
            </h2>
          </div>
          {/* Form field */}
          <form className="mt-8 space-y-6" onSubmit={handleSubmit}>
            <input type="hidden" name="remember" defaultValue="true" />
            <div className="rounded-md shadow-sm -space-y-px">
              <div>
                <label htmlFor="phone-number" className="sr-only">
                  Phone Number
                </label>
                <input
                  id="phone-number"
                  name="phoneNumber"
                  type="tel"
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border 
                  border-gray-300 placeholder-gray-500 text-white rounded-t-md focus:outline-none 
                  focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                  placeholder="Phone Number"
                  value={phoneNumber}
                  onChange={handleChange}
                />
              </div>
              <div className="mb-4">
                <label htmlFor="password" className="sr-only">
                  Password
                </label>
                <input
                  id="password"
                  name="password"
                  type="password"
                  autoComplete="current-password"
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border border-gray-300 placeholder-gray-500 text-white rounded-b-md focus:outline-none focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                  placeholder="Password"
                  value={password}
                  onChange={handleChange}
                />

                {/* Password requirement indicator */}
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

            <div className="flex items-center justify-between">
              <div className="flex items-center">
                <input
                  id="remember-me"
                  name="remember-me"
                  type="checkbox"
                  className="h-4 w-4 text-blue-600 focus:ring-blue-500 border-gray-300 rounded"
                />
                <label
                  htmlFor="remember-me"
                  className="ml-2 block text-sm text-gray-400"
                >
                  Remember me
                </label>
              </div>

              <div className="text-sm">
                <Link
                  to="/forgot-password"
                  className="font-medium text-blue-600 hover:text-blue-500"
                >
                  Forgot Password?
                </Link>
              </div>
            </div>

            <div>
              <button
                type="submit"
                className="group relative w-full flex justify-center py-2 px-4 border border-transparent text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Login
              </button>
            </div>

            <div className="text-center text-sm text-gray-300 mt-3">
              Don't have an account?{" "}
              <Link
                to="/signup"
                className="font-bold text-blue-500 hover:text-blue-400"
              >
                Signup
              </Link>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}

export default Login;
