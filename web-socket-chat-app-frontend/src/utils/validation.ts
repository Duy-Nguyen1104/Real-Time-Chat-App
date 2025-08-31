/**
 * Utility functions for form validation
 * Centralizes validation logic to reduce redundancy across components
 */

export interface ValidationError {
  [key: string]: string;
}

export interface ValidationResult {
  isValid: boolean;
  errors: ValidationError;
}

/**
 * Validate password strength and requirements
 */
export const validatePassword = (password: string): string | null => {
  if (!password) {
    return "Password is required";
  }
  if (password.length < 6) {
    return "Password must be at least 6 characters";
  }
  if (password.length > 100) {
    return "Password must not exceed 100 characters";
  }
  return null;
};

/**
 * Validate phone number format
 */
export const validatePhoneNumber = (phoneNumber: string): string | null => {
  if (!phoneNumber) {
    return "Phone number is required";
  }

  // Remove spaces and special characters for validation
  const cleanPhone = phoneNumber.replace(/[\s()-]/g, "");

  // Basic phone number validation (10-15 digits)
  const phoneRegex = /^[+]?[0-9]{10,15}$/;
  if (!phoneRegex.test(cleanPhone)) {
    return "Invalid phone number format";
  }

  return null;
};

/**
 * Validate user name
 */
export const validateName = (name: string): string | null => {
  if (!name || !name.trim()) {
    return "Name is required";
  }

  const trimmedName = name.trim();
  if (trimmedName.length < 1) {
    return "Name cannot be empty";
  }
  if (trimmedName.length > 50) {
    return "Name must not exceed 50 characters";
  }

  return null;
};

/**
 * Validate password confirmation
 */
export const validatePasswordMatch = (
  password: string,
  confirmPassword: string
): string | null => {
  if (!confirmPassword) {
    return "Please confirm your password";
  }
  if (password !== confirmPassword) {
    return "Passwords do not match";
  }
  return null;
};

/**
 * Validate login form
 */
export const validateLoginForm = (
  phoneNumber: string,
  password: string
): ValidationResult => {
  const errors: ValidationError = {};

  const phoneError = validatePhoneNumber(phoneNumber);
  if (phoneError) errors.phoneNumber = phoneError;

  const passwordError = validatePassword(password);
  if (passwordError) errors.password = passwordError;

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
};

/**
 * Validate signup form
 */
export const validateSignupForm = (
  name: string,
  phoneNumber: string,
  password: string,
  confirmPassword: string
): ValidationResult => {
  const errors: ValidationError = {};

  const nameError = validateName(name);
  if (nameError) errors.name = nameError;

  const phoneError = validatePhoneNumber(phoneNumber);
  if (phoneError) errors.phoneNumber = phoneError;

  const passwordError = validatePassword(password);
  if (passwordError) errors.password = passwordError;

  const passwordMatchError = validatePasswordMatch(password, confirmPassword);
  if (passwordMatchError) errors.confirmPassword = passwordMatchError;

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
};

/**
 * Validate forgot password form
 */
export const validateForgotPasswordForm = (
  phoneNumber: string,
  newPassword: string
): ValidationResult => {
  const errors: ValidationError = {};

  const phoneError = validatePhoneNumber(phoneNumber);
  if (phoneError) errors.phoneNumber = phoneError;

  const passwordError = validatePassword(newPassword);
  if (passwordError) errors.newPassword = passwordError;

  return {
    isValid: Object.keys(errors).length === 0,
    errors,
  };
};

/**
 * Check if input is empty or contains only whitespace
 */
export const isEmpty = (value: string): boolean => {
  return !value || !value.trim();
};

/**
 * Sanitize input by trimming whitespace
 */
export const sanitize = (value: string): string => {
  return value ? value.trim() : "";
};

/**
 * Get password strength indicator
 */
export const getPasswordStrength = (
  password: string
): {
  strength: "weak" | "medium" | "strong";
  message: string;
  isValid: boolean;
} => {
  if (!password) {
    return {
      strength: "weak",
      message: "Password is required",
      isValid: false,
    };
  }

  if (password.length < 6) {
    return {
      strength: "weak",
      message: "Password must be at least 6 characters",
      isValid: false,
    };
  }

  if (password.length >= 6 && password.length < 10) {
    return {
      strength: "medium",
      message: "Password meets minimum requirements",
      isValid: true,
    };
  }

  return { strength: "strong", message: "Strong password", isValid: true };
};

/**
 * Format validation errors for display
 */
export const formatValidationErrors = (errors: ValidationError): string => {
  const errorMessages = Object.values(errors);
  if (errorMessages.length === 0) return "";
  if (errorMessages.length === 1) return errorMessages[0];
  return errorMessages.join(". ");
};
