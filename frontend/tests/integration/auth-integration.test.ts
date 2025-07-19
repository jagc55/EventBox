import { describe, it, expect, beforeEach, vi } from "vitest";
import { useAuth } from "../../src/composables/useAuth";
import { useSession } from "../../src/composables/useSession";
import { useFavorites } from "../../src/composables/useFavorites";
import { ApiService } from "../../src/services/api";

// Mock básico de localStorage que funciona correctamente
const localStorageMock = (() => {
  let store: Record<string, string> = {};

  return {
    getItem: vi.fn((key: string) => store[key] || null),
    setItem: vi.fn((key: string, value: string) => {
      store[key] = value;
    }),
    removeItem: vi.fn((key: string) => {
      delete store[key];
    }),
    clear: vi.fn(() => {
      store = {};
    }),
  };
})();

global.localStorage = localStorageMock as any;

// Mock del ApiService
vi.mock("../../src/services/api", () => ({
  ApiService: {
    addToFavorites: vi.fn(),
    getFavoritesByUser: vi.fn(),
    isFavorite: vi.fn(),
    removeFromFavorites: vi.fn(),
  },
}));

/**
 * Pruebas de integración simplificadas del flujo de autenticación
 */
describe("Authentication Integration Flow", () => {
  beforeEach(() => {
    localStorageMock.clear();
    vi.clearAllMocks();
  });

  it("should handle session state correctly", () => {
    const { session, setSession, clearSession, isLoggedIn } = useSession();
    const { currentUser, isAdmin, isOrganizer } = useAuth();

    // Initially logged out
    expect(isLoggedIn.value).toBe(false);
    expect(currentUser.value).toBeNull();

    // Set session
    setSession({
      token: "test-token",
      userId: 1,
      roleId: 3, // Admin role
      email: "admin@test.com",
      name: "Admin User",
    });

    // Should be logged in
    expect(isLoggedIn.value).toBe(true);
    expect(currentUser.value?.id).toBe(1);
    expect(isAdmin.value).toBe(true);
    expect(isOrganizer.value).toBe(true);

    // Clear session
    clearSession();
    expect(isLoggedIn.value).toBe(false);
    expect(currentUser.value).toBeNull();
  });

  it("should handle localStorage persistence", () => {
    const { session, setSession, loadSessionFromStorage } = useSession();

    const sessionData = {
      token: "persist-token",
      userId: 2,
      roleId: 2,
      email: "user@test.com",
      name: "Test User",
    };

    setSession(sessionData);
    expect(localStorageMock.setItem).toHaveBeenCalledWith("session", JSON.stringify(session.value));

    // Simulate loading from storage
    loadSessionFromStorage();
    expect(session.value.token).toBe("persist-token");
  });

  it("should handle role permissions correctly", () => {
    const { isAdmin, isOrganizer } = useAuth();
    const { setSession } = useSession();

    // Test admin role
    setSession({
      token: "admin-token",
      userId: 1,
      roleId: 3,
      email: "admin@test.com",
      name: "Admin",
    });
    expect(isAdmin.value).toBe(true);
    expect(isOrganizer.value).toBe(true);

    // Test organizer role
    setSession({
      token: "organizer-token",
      userId: 2,
      roleId: 2,
      email: "organizer@test.com",
      name: "Organizer",
    });
    expect(isAdmin.value).toBe(false);
    expect(isOrganizer.value).toBe(true);

    // Test user role
    setSession({
      token: "user-token",
      userId: 3,
      roleId: 1,
      email: "user@test.com",
      name: "User",
    });
    expect(isAdmin.value).toBe(false);
    expect(isOrganizer.value).toBe(false);
  });

  it("should integrate with favorites correctly", async () => {
    const { setSession } = useSession();
    const { addToFavorites } = useFavorites();

    // Mock API response
    vi.mocked(ApiService.addToFavorites).mockResolvedValue({ message: "Added" });

    setSession({
      token: "user-token",
      userId: 123,
      roleId: 1,
      email: "user@test.com",
      name: "Test User",
    });

    const result = await addToFavorites(123, 1);
    expect(result).toBe(true);
    expect(ApiService.addToFavorites).toHaveBeenCalledWith(123, 1);
  });

  it("should handle corrupted localStorage gracefully", () => {
    const { session, loadSessionFromStorage, isLoggedIn } = useSession();

    // Set corrupted data
    localStorageMock.setItem("session", "invalid-json");

    loadSessionFromStorage();

    // Should fallback to empty session
    expect(isLoggedIn.value).toBe(false);
    expect(session.value.token).toBe("");
    expect(session.value.userId).toBeNull();
  });
});
