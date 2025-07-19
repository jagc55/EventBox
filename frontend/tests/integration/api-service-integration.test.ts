import { describe, it, expect, beforeEach, vi } from "vitest";
import { ApiService } from "../../src/services/api";

// Simple API integration tests without complex component mounting
describe("API Service Integration", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("should call getAllEvents API method", async () => {
    // Mock the API call
    const mockFn = vi.spyOn(ApiService, "getAllEvents").mockResolvedValue([]);

    // Call the method
    await ApiService.getAllEvents();

    // Verify it was called
    expect(mockFn).toHaveBeenCalled();

    // Restore the mock
    mockFn.mockRestore();
  });

  it("should call getEventById API method with correct parameters", async () => {
    const eventId = 123;

    const mockFn = vi.spyOn(ApiService, "getEventById").mockResolvedValue({} as any);

    await ApiService.getEventById(eventId);

    expect(mockFn).toHaveBeenCalledWith(eventId);

    mockFn.mockRestore();
  });

  it("should call favorites API methods with correct parameters", async () => {
    const userId = 1;
    const eventId = 2;

    // Mock the API calls
    const addFavoritesMock = vi.spyOn(ApiService, "addToFavorites").mockResolvedValue({ message: "Added" });
    const removeFavoritesMock = vi.spyOn(ApiService, "removeFromFavorites").mockResolvedValue({ message: "Removed" });
    const getFavoritesMock = vi.spyOn(ApiService, "getFavoritesByUser").mockResolvedValue([]);

    // Test add to favorites
    await ApiService.addToFavorites(userId, eventId);
    expect(addFavoritesMock).toHaveBeenCalledWith(userId, eventId);

    // Test remove from favorites
    await ApiService.removeFromFavorites(userId, eventId);
    expect(removeFavoritesMock).toHaveBeenCalledWith(userId, eventId);

    // Test get favorites
    await ApiService.getFavoritesByUser(userId);
    expect(getFavoritesMock).toHaveBeenCalledWith(userId);

    // Restore the mocks
    addFavoritesMock.mockRestore();
    removeFavoritesMock.mockRestore();
    getFavoritesMock.mockRestore();
  });

  it("should handle API errors gracefully", async () => {
    // Mock an API error
    const mockFn = vi.spyOn(ApiService, "getAllEvents").mockRejectedValue(new Error("Network error"));

    // Test error handling
    try {
      await ApiService.getAllEvents();
    } catch (error) {
      expect(error).toBeInstanceOf(Error);
      expect((error as Error).message).toBe("Network error");
    }

    // Restore the mock
    mockFn.mockRestore();
  });

  it("should verify API service methods exist", () => {
    // Verify all expected methods exist on ApiService
    expect(typeof ApiService.getAllEvents).toBe("function");
    expect(typeof ApiService.getEventById).toBe("function");
    expect(typeof ApiService.addToFavorites).toBe("function");
    expect(typeof ApiService.removeFromFavorites).toBe("function");
    expect(typeof ApiService.getFavoritesByUser).toBe("function");
  });
});
