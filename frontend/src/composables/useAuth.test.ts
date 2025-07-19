import { describe, it, expect, beforeEach, vi } from "vitest";
import { useAuth } from "@/composables/useAuth";
import { useSession } from "@/composables/useSession";
import type { Comment, Event } from "@/services/api";

// Mock useSession
vi.mock("@/composables/useSession", () => ({
  useSession: vi.fn(),
}));

describe("useAuth", () => {
  const mockSession = {
    value: {
      token: "test-token",
      userId: 1,
      roleId: 1,
      email: "test@example.com",
      name: "Test User",
    },
  };

  const mockIsLoggedIn = { value: true };

  const createMockEvent = (id: number = 1): Event => ({
    id,
    title: "Test Event",
    description: "Test Description",
    date: "2024-12-25",
    time: "20:00",
    price: 25.0,
    minimumAge: 18,
    imageUrl: "https://example.com/image.jpg",
    tags: "music,concert",
    eventType: { id: 1, name: "Concert" },
    establishment: {
      id: 1,
      name: "Test Venue",
      address: "Test Address",
      email: "venue@test.com",
      phone: "+1234567890",
      locality: {
        id: 1,
        name: "Test City",
        province: { id: 1, name: "Test Province" },
      },
    },
    user: { id: 1, name: "Test User", email: "test@example.com" },
  });

  const createMockComment = (userId: number = 1, eventId: number = 1): Comment => ({
    id: 1,
    description: "Test comment",
    date: new Date().toISOString(),
    user: {
      id: userId,
      name: userId === 1 ? "Test User" : "Other User",
      email: userId === 1 ? "test@example.com" : "other@example.com",
    },
    event: createMockEvent(eventId),
  });

  beforeEach(() => {
    vi.clearAllMocks();
    (useSession as any).mockReturnValue({
      session: mockSession,
      isLoggedIn: mockIsLoggedIn,
    });
  });

  describe("canDeleteComment", () => {
    it("should return false when user is not logged in", () => {
      (useSession as any).mockReturnValue({
        session: mockSession,
        isLoggedIn: { value: false },
      });

      const comment = createMockComment(2);

      const { canDeleteComment } = useAuth();

      expect(canDeleteComment.value(comment)).toBe(false);
    });

    it("should return true when user is the comment author", () => {
      const comment = createMockComment(1);

      const { canDeleteComment } = useAuth();

      expect(canDeleteComment.value(comment)).toBe(true);
    });

    it("should return true when user is admin (roleId = 3)", () => {
      const adminSession = {
        ...mockSession,
        value: { ...mockSession.value, roleId: 3 },
      };

      (useSession as any).mockReturnValue({
        session: adminSession,
        isLoggedIn: mockIsLoggedIn,
      });

      const comment = createMockComment(2);

      const { canDeleteComment } = useAuth();

      expect(canDeleteComment.value(comment)).toBe(true);
    });

    it("should return false when user is neither author nor admin", () => {
      // Given
      const comment = createMockComment(2); // Different user

      // When
      const { canDeleteComment } = useAuth();

      // Then
      expect(canDeleteComment.value(comment)).toBe(false);
    });
  });

  describe("isAdmin", () => {
    it("should return true when user is logged in and has admin role (roleId = 3)", () => {
      // Given
      const adminSession = {
        ...mockSession,
        value: { ...mockSession.value, roleId: 3 },
      };

      (useSession as any).mockReturnValue({
        session: adminSession,
        isLoggedIn: mockIsLoggedIn,
      });

      // When
      const { isAdmin } = useAuth();

      // Then
      expect(isAdmin.value).toBe(true);
    });

    it("should return false when user is logged in but not admin", () => {
      // When
      const { isAdmin } = useAuth();

      // Then
      expect(isAdmin.value).toBe(false);
    });

    it("should return false when user is not logged in", () => {
      // Given
      (useSession as any).mockReturnValue({
        session: mockSession,
        isLoggedIn: { value: false },
      });

      // When
      const { isAdmin } = useAuth();

      // Then
      expect(isAdmin.value).toBe(false);
    });
  });

  describe("isOrganizer", () => {
    it("should return true when user is organizer (roleId = 2)", () => {
      // Given
      const organizerSession = {
        ...mockSession,
        value: { ...mockSession.value, roleId: 2 },
      };

      (useSession as any).mockReturnValue({
        session: organizerSession,
        isLoggedIn: mockIsLoggedIn,
      });

      // When
      const { isOrganizer } = useAuth();

      // Then
      expect(isOrganizer.value).toBe(true);
    });

    it("should return true when user is admin (roleId = 3)", () => {
      // Given
      const adminSession = {
        ...mockSession,
        value: { ...mockSession.value, roleId: 3 },
      };

      (useSession as any).mockReturnValue({
        session: adminSession,
        isLoggedIn: mockIsLoggedIn,
      });

      // When
      const { isOrganizer } = useAuth();

      // Then
      expect(isOrganizer.value).toBe(true);
    });

    it("should return false when user is regular user (roleId = 1)", () => {
      // When
      const { isOrganizer } = useAuth();

      // Then
      expect(isOrganizer.value).toBe(false);
    });

    it("should return false when user is not logged in", () => {
      // Given
      (useSession as any).mockReturnValue({
        session: mockSession,
        isLoggedIn: { value: false },
      });

      // When
      const { isOrganizer } = useAuth();

      // Then
      expect(isOrganizer.value).toBe(false);
    });
  });

  describe("currentUser", () => {
    it("should return user data when logged in", () => {
      // When
      const { currentUser } = useAuth();

      // Then
      expect(currentUser.value).toEqual({
        id: 1,
        name: "Test User",
        email: "test@example.com",
        roleId: 1,
      });
    });

    it("should return null when not logged in", () => {
      // Given
      (useSession as any).mockReturnValue({
        session: mockSession,
        isLoggedIn: { value: false },
      });

      // When
      const { currentUser } = useAuth();

      // Then
      expect(currentUser.value).toBeNull();
    });
  });
});
