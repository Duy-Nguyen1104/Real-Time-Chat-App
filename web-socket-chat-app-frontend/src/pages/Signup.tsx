import { ChangeEvent, FormEvent, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import Spinner from "../components/Spinner";
import { Link } from "react-router-dom";
import { registerUser } from "../services/authService";

function Signup() {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    name: "",
    phoneNumber: "",
    password: "",
    confirmPassword: "",
  });

  const [errors, setErrors] = useState<{
    name?: string;
    phoneNumber?: string;
    password?: string;
    confirmPassword?: string;
    general?: string;
  }>({});

  const { name, phoneNumber, password, confirmPassword } = formData;
  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const [isLoading, setIsLoading] = useState(false);

  const validateForm = () => {
    const newErrors: {
      name?: string;
      phoneNumber?: string;
      password?: string;
      confirmPassword?: string;
    } = {};

    // Validate name
    if (!name.trim()) {
      newErrors.name = "Name is required";
    }

    // Validate phone number
    if (!phoneNumber.trim()) {
      newErrors.phoneNumber = "Phone number is required";
    }

    // Validate password
    if (!password) {
      newErrors.password = "Password is required";
    } else if (password.length < 6) {
      newErrors.password = "Password must be at least 6 characters";
    }
    if (password !== confirmPassword) {
      newErrors.confirmPassword = "Passwords do not match";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const handleSignup = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!validateForm()) {
      toast.error("Please fill in all required fields");
      return;
    }

    setIsLoading(true);

    try {
      const signupData = {
        name,
        phoneNumber,
        password,
      };

      await registerUser(signupData);
      toast.success("Signup successful! Please login to continue");
      navigate("/login");
    } catch (error: any) {
      const errorMessage =
        error.response.data.message ||
        "Signup failed. Please check your signup details.";
      setErrors({
        general: errorMessage,
      });
      toast.error(errorMessage);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    // container div
    <div className="min-h-screen flex items-center justify-center bg-[#121212] py-12 px-4 sm:px-6 lg:px-8">
      {isLoading ? (
        <Spinner />
      ) : (
        <div className="max-w-md w-full space-y-8">
          <div>
            <img src="" alt="" />
            <h2 className="mt-6 text-center text-3xl font-extrabold text-white">
              Signup to the Chat App
            </h2>
          </div>

          {/* Form field */}
          <form action="" className="mt-8 space-y-6" onSubmit={handleSignup}>
            <div className="rounded-md shadow-sm -space-y-px">
              <div>
                <label htmlFor="name" className="sr-only">
                  Name
                </label>
                <input
                  type="text"
                  id="name"
                  name="name"
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border 
                  border-gray-300 placeholder-gray-500 text-white rounded-t-md focus:outline-none 
                  focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                  placeholder="Name"
                  value={name}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="phone-number" className="sr-only">
                  Phone Number
                </label>
                <input
                  type="tel"
                  id="phone-number"
                  name="phoneNumber"
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border 
                  border-gray-300 placeholder-gray-500 text-white rounded-t-md focus:outline-none 
                  focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                  placeholder="Phone Number"
                  value={phoneNumber}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="password" className="sr-only">
                  Password
                </label>
                <input
                  type="password"
                  id="password"
                  name="password"
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border 
                  border-gray-300 placeholder-gray-500 text-white rounded-t-md focus:outline-none 
                  focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                  placeholder="Password"
                  value={password}
                  onChange={handleChange}
                />
              </div>

              <div>
                <label htmlFor="confirm-password" className="sr-only">
                  Confirm Password
                </label>
                <input
                  type="password"
                  id="confirm-password"
                  name="confirmPassword"
                  required
                  className="appearance-none rounded-none relative block w-full px-3 py-2 border 
                  border-gray-300 placeholder-gray-500 text-white rounded-t-md focus:outline-none 
                  focus:ring-blue-500 focus:border-blue-500 focus:z-10 sm:text-sm"
                  placeholder="Confirm Password"
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

            {/* Signup button */}
            <div>
              <button
                type="submit"
                className="group relative w-full flex justify-center py-2 px-4 border border-transparent
                text-sm font-medium rounded-md text-white bg-blue-600 hover:bg-blue-700 focus:outline-none
                focus:ring-2 focus:ring-offset-2 focus:ring-blue-500"
              >
                Sign Up
              </button>
            </div>

            {/* Login link */}
            <div className="text-center text-sm text-gray-300 mt-3">
              Already have an account?{" "}
              <Link to="/login" className="text-blue-400">
                Login
              </Link>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}

export default Signup;
