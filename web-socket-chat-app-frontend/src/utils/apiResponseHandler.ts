import { toast } from "react-toastify";

/**
 * Standard API response structure
 */
export interface ApiResponse<T = any> {
  message: string;
  code: number;
  data?: T;
}

/**
 * Error response structure from backend
 */
export interface ApiError {
  message: string;
  code: number;
  data?: any;
}

/**
 * Utility class for handling API responses consistently
 */
export class ApiResponseHandler {
  /**
   * Handle successful API response
   */
  static handleSuccess<T>(
    response: ApiResponse<T>,
    successMessage?: string,
    showToast: boolean = true
  ): T | undefined {
    if (showToast) {
      toast.success(
        successMessage || response.message || "Operation successful"
      );
    }
    return response.data;
  }

  /**
   * Handle API error response
   */
  static handleError(
    error: any,
    defaultMessage: string = "An error occurred",
    showToast: boolean = true
  ): string {
    let errorMessage = defaultMessage;

    if (error?.response?.data?.message) {
      errorMessage = error.response.data.message;
    } else if (error?.message) {
      errorMessage = error.message;
    } else if (typeof error === "string") {
      errorMessage = error;
    }

    if (showToast) {
      toast.error(errorMessage);
    }

    return errorMessage;
  }

  /**
   * Handle network or connection errors
   */
  static handleNetworkError(error: any, showToast: boolean = true): string {
    const networkMessage =
      "Network error. Please check your connection and try again.";

    if (showToast) {
      toast.error(networkMessage);
    }

    return networkMessage;
  }

  /**
   * Handle authentication errors (401/403)
   */
  static handleAuthError(
    error: any,
    onRedirect?: () => void,
    showToast: boolean = true
  ): string {
    const authMessage = "Authentication failed. Please login again.";

    if (showToast) {
      toast.error(authMessage);
    }

    // Redirect to login if callback provided
    if (onRedirect) {
      setTimeout(onRedirect, 1500);
    }

    return authMessage;
  }

  /**
   * Generic error handler that routes to appropriate handler based on error type
   */
  static handleApiError(
    error: any,
    options: {
      defaultMessage?: string;
      showToast?: boolean;
      onAuthError?: () => void;
    } = {}
  ): string {
    const {
      defaultMessage = "An error occurred",
      showToast = true,
      onAuthError,
    } = options;

    // Check for network errors
    if (!error.response) {
      return this.handleNetworkError(error, showToast);
    }

    // Check for authentication errors
    const status = error.response?.status;
    if (status === 401 || status === 403) {
      return this.handleAuthError(error, onAuthError, showToast);
    }

    // Handle other API errors
    return this.handleError(error, defaultMessage, showToast);
  }
}

/**
 * Async wrapper for API calls with consistent error handling
 */
export const withApiErrorHandling = async <T>(
  apiCall: () => Promise<T>,
  options: {
    successMessage?: string;
    errorMessage?: string;
    showSuccessToast?: boolean;
    showErrorToast?: boolean;
    onAuthError?: () => void;
  } = {}
): Promise<{ data?: T; error?: string; success: boolean }> => {
  const {
    successMessage,
    errorMessage = "Operation failed",
    showSuccessToast = false,
    showErrorToast = true,
    onAuthError,
  } = options;

  try {
    const result = await apiCall();

    if (showSuccessToast && successMessage) {
      toast.success(successMessage);
    }

    return { data: result, success: true };
  } catch (error) {
    const errorMsg = ApiResponseHandler.handleApiError(error, {
      defaultMessage: errorMessage,
      showToast: showErrorToast,
      onAuthError,
    });

    return { error: errorMsg, success: false };
  }
};

/**
 * Utility for handling form submission with loading states
 */
export const handleFormSubmission = async <T>(
  formData: any,
  apiCall: (data: any) => Promise<T>,
  options: {
    setLoading: (loading: boolean) => void;
    setErrors: (errors: any) => void;
    onSuccess?: (data: T) => void;
    onError?: (error: string) => void;
    successMessage?: string;
    errorMessage?: string;
  }
): Promise<void> => {
  const {
    setLoading,
    setErrors,
    onSuccess,
    onError,
    successMessage,
    errorMessage = "Operation failed",
  } = options;

  setLoading(true);
  setErrors({});

  try {
    const result = await apiCall(formData);

    if (successMessage) {
      toast.success(successMessage);
    }

    if (onSuccess) {
      onSuccess(result);
    }
  } catch (error) {
    const errorMsg = ApiResponseHandler.handleError(error, errorMessage);

    // Set form-specific errors if available
    if (error && typeof error === "object" && "response" in error) {
      const axiosError = error as any;
      if (axiosError.response?.data?.errors) {
        setErrors(axiosError.response.data.errors);
      } else {
        setErrors({ general: errorMsg });
      }
    } else {
      setErrors({ general: errorMsg });
    }

    if (onError) {
      onError(errorMsg);
    }
  } finally {
    setLoading(false);
  }
};

/**
 * Check if error is a validation error (400 status with field errors)
 */
export const isValidationError = (error: any): boolean => {
  return error?.response?.status === 400 && error?.response?.data?.errors;
};

/**
 * Extract field errors from validation error response
 */
export const extractFieldErrors = (error: any): Record<string, string> => {
  if (isValidationError(error)) {
    return error.response.data.errors;
  }
  return {};
};
