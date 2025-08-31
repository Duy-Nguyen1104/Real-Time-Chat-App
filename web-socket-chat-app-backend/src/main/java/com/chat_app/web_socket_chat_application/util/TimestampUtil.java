package com.chat_app.web_socket_chat_application.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Utility class for handling timestamp formatting consistently across the application.
 * This centralizes timestamp formatting logic to avoid redundancy.
 */
public class TimestampUtil {
    
    private static final String ISO_TIMESTAMP_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat(ISO_TIMESTAMP_FORMAT);
    
    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
    
    /**
     * Get current timestamp in ISO format (UTC)
     * @return Current timestamp as ISO string
     */
    public static String getCurrentTimestamp() {
        return ISO_FORMAT.format(new Date());
    }
    
    /**
     * Format a Date object to ISO timestamp string
     * @param date The date to format
     * @return Formatted timestamp string
     */
    public static String formatToTimestamp(Date date) {
        if (date == null) {
            return getCurrentTimestamp();
        }
        return ISO_FORMAT.format(date);
    }
    
    /**
     * Check if a timestamp string is null or empty and provide current timestamp if needed
     * @param timestamp The timestamp to check
     * @return The original timestamp if valid, otherwise current timestamp
     */
    public static String ensureTimestamp(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return getCurrentTimestamp();
        }
        return timestamp;
    }
    
    /**
     * Parse timestamp string to Date object
     * @param timestamp The timestamp string to parse
     * @return Date object
     * @throws java.text.ParseException if timestamp format is invalid
     */
    public static Date parseTimestamp(String timestamp) throws java.text.ParseException {
        return ISO_FORMAT.parse(timestamp);
    }
    
    /**
     * Validate if a timestamp string is in correct ISO format
     * @param timestamp The timestamp string to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidTimestamp(String timestamp) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            return false;
        }
        try {
            ISO_FORMAT.parse(timestamp);
            return true;
        } catch (java.text.ParseException e) {
            return false;
        }
    }
}
