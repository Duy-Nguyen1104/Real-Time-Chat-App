/**
 * Utility functions for timestamp formatting
 * Centralizes timestamp handling to reduce redundancy across components
 */

/**
 * Format ISO timestamp to local time string (HH:MM format)
 */
export const formatTimestampToLocalTime = (isoTimestamp: string): string => {
  try {
    if (!isoTimestamp) {
      return "";
    }

    const date = new Date(isoTimestamp);

    // Check if date is valid
    if (isNaN(date.getTime())) {
      return "Invalid Time";
    }

    return date.toLocaleTimeString([], {
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    console.error("Failed to format timestamp:", isoTimestamp, error);
    return "Invalid Time";
  }
};

/**
 * Format timestamp for message display (handles relative time)
 */
export const formatMessageTime = (timestamp: string): string => {
  try {
    if (!timestamp) {
      return "";
    }

    const date = new Date(timestamp);

    if (isNaN(date.getTime())) {
      return "Invalid Time";
    }

    const now = new Date();
    const diffInHours = (now.getTime() - date.getTime()) / (1000 * 60 * 60);

    // If less than 24 hours ago, show time
    if (diffInHours < 24) {
      return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      });
    }

    // If less than 7 days ago, show day and time
    if (diffInHours < 168) {
      // 7 * 24 hours
      return date.toLocaleDateString([], {
        weekday: "short",
        hour: "2-digit",
        minute: "2-digit",
      });
    }

    // Otherwise show date and time
    return date.toLocaleDateString([], {
      month: "short",
      day: "numeric",
      hour: "2-digit",
      minute: "2-digit",
    });
  } catch (error) {
    console.error("Error formatting message time:", timestamp, error);
    return "Invalid Time";
  }
};

/**
 * Format date for conversation/message separators (DD/MM/YYYY format)
 */
export const formatDateForSeparator = (isoTimestamp: string): string => {
  try {
    if (!isoTimestamp) {
      return "Invalid Date";
    }

    const date = new Date(isoTimestamp);

    if (isNaN(date.getTime())) {
      return "Invalid Date";
    }

    const day = String(date.getDate()).padStart(2, "0");
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const year = date.getFullYear();

    return `${day}/${month}/${year}`;
  } catch (error) {
    console.error("Error formatting date for separator:", isoTimestamp, error);
    return "Invalid Date";
  }
};

/**
 * Get current timestamp in ISO format
 */
export const getCurrentTimestamp = (): string => {
  return new Date().toISOString();
};

/**
 * Check if a timestamp is from today
 */
export const isToday = (timestamp: string): boolean => {
  try {
    const date = new Date(timestamp);
    const today = new Date();

    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    );
  } catch (error) {
    console.error("Error checking if timestamp is today:", timestamp, error);
    return false;
  }
};

/**
 * Check if a timestamp is from yesterday
 */
export const isYesterday = (timestamp: string): boolean => {
  try {
    const date = new Date(timestamp);
    const yesterday = new Date();
    yesterday.setDate(yesterday.getDate() - 1);

    return (
      date.getDate() === yesterday.getDate() &&
      date.getMonth() === yesterday.getMonth() &&
      date.getFullYear() === yesterday.getFullYear()
    );
  } catch (error) {
    console.error(
      "Error checking if timestamp is yesterday:",
      timestamp,
      error
    );
    return false;
  }
};

/**
 * Get relative time description (Today, Yesterday, or date)
 */
export const getRelativeTime = (timestamp: string): string => {
  if (isToday(timestamp)) {
    return "Today";
  }

  if (isYesterday(timestamp)) {
    return "Yesterday";
  }

  return formatDateForSeparator(timestamp);
};

/**
 * Format timestamp for conversation list (last message time)
 */
export const formatConversationTime = (timestamp: string): string => {
  try {
    if (!timestamp) {
      return "";
    }

    const date = new Date(timestamp);

    if (isNaN(date.getTime())) {
      return "";
    }

    // Always show date format (e.g., "August 10") instead of time
    return date.toLocaleDateString([], {
      month: "long",
      day: "numeric",
    });
  } catch (error) {
    console.error("Error formatting conversation time:", timestamp, error);
    return "";
  }
};

/**
 * Validate if a timestamp string is valid
 */
export const isValidTimestamp = (timestamp: string): boolean => {
  if (!timestamp) return false;

  try {
    const date = new Date(timestamp);
    return !isNaN(date.getTime());
  } catch (error) {
    return false;
  }
};

/**
 * Convert timestamp to different timezone
 */
export const convertToTimezone = (
  timestamp: string,
  timezone: string
): string => {
  try {
    const date = new Date(timestamp);
    return date.toLocaleString("en-US", { timeZone: timezone });
  } catch (error) {
    console.error("Error converting timezone:", timestamp, timezone, error);
    return timestamp;
  }
};
