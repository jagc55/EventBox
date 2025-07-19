import { describe, it, expect, beforeEach, vi } from "vitest";
import { useSession } from "@/composables/useSession";

// Mock localStorage
const localStorageMock = {
  getItem: vi.fn(),
  setItem: vi.fn(),
  removeItem: vi.fn(),
  clear: vi.fn(),
};

Object.defineProperty(window, "localStorage", {
  value: localStorageMock,
});

describe("useSession", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    localStorageMock.getItem.mockReturnValue(null);
  });

  describe("initial state", () => {
    it("should have empty session by default", () => {
      // When
      const { session, isLoggedIn } = useSession();

      // Then
      expect(session.value).toEqual({
        token: "",
        userId: null,
        roleId: null,
        email: "",
        name: "",
      });
      expect(isLoggedIn.value).toBe(false);
    });
  });

  describe("setSession", () => {
    it("should set session data and save to localStorage", () => {
      // Given
      const sessionData = {
        token: "test-token",
        userId: 1,
        roleId: 2,
        email: "test@example.com",
        name: "Test User",
      };

      // When
      const { setSession, session, isLoggedIn } = useSession();
      setSession(sessionData);

      // Then
      expect(session.value).toEqual(sessionData);
      expect(isLoggedIn.value).toBe(true);
      expect(localStorageMock.setItem).toHaveBeenCalledWith("session", JSON.stringify(sessionData));
    });
  });

  describe("clearSession", () => {
    it("should clear session data and remove from localStorage", () => {
      const { setSession, clearSession, session, isLoggedIn } = useSession();
      setSession({
        token: "test-token",
        userId: 1,
        roleId: 2,
        email: "test@example.com",
        name: "Test User",
      });

      clearSession();

      expect(session.value).toEqual({
        token: "",
        userId: null,
        roleId: null,
        email: "",
        name: "",
      });
      expect(isLoggedIn.value).toBe(false);
      expect(localStorageMock.removeItem).toHaveBeenCalledWith("session");
    });
  });

  describe("loadSessionFromStorage", () => {
    it("should load session from localStorage when valid data exists", () => {
      // Given
      const storedSession = {
        token: "stored-token",
        userId: 1,
        roleId: 2,
        email: "stored@example.com",
        name: "Stored User",
      };
      localStorageMock.getItem.mockReturnValue(JSON.stringify(storedSession));

      // When
      const { loadSessionFromStorage, session } = useSession();
      loadSessionFromStorage();

      // Then
      expect(session.value).toEqual(storedSession);
      expect(localStorageMock.getItem).toHaveBeenCalledWith("session");
    });

    it("should clear session when localStorage contains invalid JSON", () => {
      // Given
      localStorageMock.getItem.mockReturnValue("invalid-json");

      // When
      const { loadSessionFromStorage, session } = useSession();
      loadSessionFromStorage();

      // Then
      expect(session.value).toEqual({
        token: "",
        userId: null,
        roleId: null,
        email: "",
        name: "",
      });
      expect(localStorageMock.removeItem).toHaveBeenCalledWith("session");
    });

    it("should do nothing when localStorage is empty", () => {
      // Given
      localStorageMock.getItem.mockReturnValue(null);

      // When
      const { loadSessionFromStorage, session } = useSession();
      const initialSession = { ...session.value };
      loadSessionFromStorage();

      // Then
      expect(session.value).toEqual(initialSession);
    });
  });

  describe("isLoggedIn computed", () => {
    it("should return true when token exists", () => {
      // Given
      const { setSession, isLoggedIn } = useSession();

      // When
      setSession({
        token: "test-token",
        userId: 1,
        roleId: 2,
        email: "test@example.com",
        name: "Test User",
      });

      // Then
      expect(isLoggedIn.value).toBe(true);
    });

    it("should return false when token is empty", () => {
      // Given
      const { setSession, isLoggedIn } = useSession();

      // When
      setSession({
        token: "",
        userId: 1,
        roleId: 2,
        email: "test@example.com",
        name: "Test User",
      });

      // Then
      expect(isLoggedIn.value).toBe(false);
    });

    it("should return true when token is whitespace only (current implementation)", () => {
      // Given
      const { setSession, isLoggedIn } = useSession();

      // When
      setSession({
        token: "   ",
        userId: 1,
        roleId: 2,
        email: "test@example.com",
        name: "Test User",
      });

      // Then
      // Note: Current implementation treats whitespace-only tokens as truthy
      expect(isLoggedIn.value).toBe(true);
    });
  });

  describe("persistence", () => {
    it("should maintain session state across multiple calls", () => {
      // Given
      const sessionData = {
        token: "persistent-token",
        userId: 1,
        roleId: 2,
        email: "persistent@example.com",
        name: "Persistent User",
      };

      // When
      const { setSession } = useSession();
      setSession(sessionData);

      // Get a new instance (simulating app reload)
      const { session: newSession, isLoggedIn: newIsLoggedIn } = useSession();

      // Then
      expect(newSession.value).toEqual(sessionData);
      expect(newIsLoggedIn.value).toBe(true);
    });
  });
});
