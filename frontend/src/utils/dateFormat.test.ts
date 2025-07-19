import { describe, it, expect, vi } from "vitest";
import { formatDate, formatTime, formatDateTime } from "@/utils/dateFormat";

describe("dateFormat utilities", () => {
  describe("formatDate", () => {
    it("should format ISO date to Spanish format", () => {
      // Given
      const isoDate = "2024-12-25";

      // When
      const result = formatDate(isoDate);

      // Then
      expect(result).toBe("25/12/2024");
    });

    it("should handle single digit days and months", () => {
      // Given
      const isoDate = "2024-01-05";

      // When
      const result = formatDate(isoDate);

      // Then
      expect(result).toBe("05/01/2024");
    });

    it("should return empty string for empty input", () => {
      // When
      const result = formatDate("");

      // Then
      expect(result).toBe("");
    });

    it("should return empty string for null input", () => {
      // When
      const result = formatDate(null as any);

      // Then
      expect(result).toBe("");
    });

    it("should return empty string for undefined input", () => {
      // When
      const result = formatDate(undefined as any);

      // Then
      expect(result).toBe("");
    });

    it("should handle invalid date gracefully", () => {
      // Given
      const invalidDate = "invalid-date";

      // When
      const result = formatDate(invalidDate);

      // Then
      // The implementation converts invalid dates to "Invalid Date" string
      expect(result).toBe("Invalid Date");
    });

    it("should handle leap year dates", () => {
      // Given
      const leapYearDate = "2024-02-29";

      // When
      const result = formatDate(leapYearDate);

      // Then
      expect(result).toBe("29/02/2024");
    });
  });

  describe("formatTime", () => {
    it("should format time from HH:MM:SS to HH:MM", () => {
      const time = "14:30:45";

      const result = formatTime(time);

      expect(result).toBe("14:30");
    });

    it("should format time from HH:MM format", () => {
      const time = "09:15";

      const result = formatTime(time);

      expect(result).toBe("09:15");
    });

    it("should handle single digit hours and minutes", () => {
      // Given
      const time = "08:05:30";

      // When
      const result = formatTime(time);

      // Then
      expect(result).toBe("08:05");
    });

    it("should return empty string for empty input", () => {
      // When
      const result = formatTime("");

      // Then
      expect(result).toBe("");
    });

    it("should return empty string for null input", () => {
      // When
      const result = formatTime(null as any);

      // Then
      expect(result).toBe("");
    });

    it("should return empty string for undefined input", () => {
      // When
      const result = formatTime(undefined as any);

      // Then
      expect(result).toBe("");
    });

    it("should handle midnight time", () => {
      // Given
      const time = "00:00:00";

      // When
      const result = formatTime(time);

      // Then
      expect(result).toBe("00:00");
    });

    it("should handle late night time", () => {
      // Given
      const time = "23:59:59";

      // When
      const result = formatTime(time);

      // Then
      expect(result).toBe("23:59");
    });

    it("should handle invalid time gracefully", () => {
      // Given
      const invalidTime = "invalid-time";

      // When
      const result = formatTime(invalidTime);

      // Then
      // The implementation just returns the original string for invalid times
      expect(result).toBe(invalidTime);
    });

    it("should handle incomplete time format", () => {
      // Given
      const incompleteTime = "14";

      // When
      const result = formatTime(incompleteTime);

      // Then
      expect(result).toBe(incompleteTime);
    });
  });

  describe("formatDateTime", () => {
    it("should format both date and time together", () => {
      // Given
      const date = "2024-12-25";
      const time = "20:30:00";

      // When
      const result = formatDateTime(date, time);

      // Then
      expect(result).toBe("25/12/2024 a las 20:30");
    });

    it("should return only formatted date when time is empty", () => {
      // Given
      const date = "2024-12-25";
      const time = "";

      // When
      const result = formatDateTime(date, time);

      // Then
      expect(result).toBe("25/12/2024");
    });

    it("should return only formatted time when date is empty", () => {
      // Given
      const date = "";
      const time = "20:30:00";

      // When
      const result = formatDateTime(date, time);

      // Then
      expect(result).toBe("20:30");
    });

    it("should return empty string when both date and time are empty", () => {
      // Given
      const date = "";
      const time = "";

      // When
      const result = formatDateTime(date, time);

      // Then
      expect(result).toBe("");
    });

    it("should handle null/undefined inputs gracefully", () => {
      // When
      const result1 = formatDateTime(null as any, undefined as any);
      const result2 = formatDateTime("2024-12-25", null as any);
      const result3 = formatDateTime(undefined as any, "20:30:00");

      // Then
      expect(result1).toBe("");
      expect(result2).toBe("25/12/2024");
      expect(result3).toBe("20:30");
    });

    it("should handle invalid inputs gracefully", () => {
      // When
      const result = formatDateTime("invalid-date", "invalid-time");

      // Then
      // The implementation shows "Invalid Date" for invalid dates
      expect(result).toBe("Invalid Date a las invalid-time");
    });

    it("should format New Year Eve event correctly", () => {
      // Given
      const date = "2024-12-31";
      const time = "23:59:00";

      // When
      const result = formatDateTime(date, time);

      // Then
      expect(result).toBe("31/12/2024 a las 23:59");
    });
  });
});
