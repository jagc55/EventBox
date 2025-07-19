import { describe, it, expect, beforeEach, vi } from "vitest";
import { useSession } from "../../src/composables/useSession";
import { useAuth } from "../../src/composables/useAuth";
import { useEvents } from "../../src/composables/useEvents";
import { useFavorites } from "../../src/composables/useFavorites";
import { useComments } from "../../src/composables/useComments";
import { ApiService } from "../../src/services/api";

/**
 * Pruebas de integración simplificadas para los composables principales
 * Valida la integración básica entre composables y estado de sesión
 */
describe("Composables Integration Tests", () => {
  beforeEach(() => {
    localStorage.clear();
    vi.clearAllMocks();
  });

  it("should integrate session with auth composable correctly", () => {
    const { setSession, clearSession, isLoggedIn } = useSession();
    const { currentUser, isAdmin, isOrganizer } = useAuth();

    expect(isLoggedIn.value).toBe(false);
    expect(currentUser.value).toBeNull();
    expect(isAdmin.value).toBe(false);
    expect(isOrganizer.value).toBe(false);

    setSession({
      token: "admin-token",
      userId: 1,
      roleId: 3,
      email: "admin@test.com",
      name: "Admin User",
    });

    expect(isLoggedIn.value).toBe(true);
    expect(currentUser.value?.id).toBe(1);
    expect(currentUser.value?.roleId).toBe(3);
    expect(isAdmin.value).toBe(true);
    expect(isOrganizer.value).toBe(true);

    clearSession();

    expect(isLoggedIn.value).toBe(false);
    expect(currentUser.value).toBeNull();
    expect(isAdmin.value).toBe(false);
    expect(isOrganizer.value).toBe(false);
  });

  it("should handle events loading with session state", async () => {
    // Mock API con fecha futura
    const futureDate = new Date();
    futureDate.setDate(futureDate.getDate() + 30); // 30 días en el futuro

    const mockEvents = [
      {
        id: 1,
        title: "Test Event",
        description: "Test Description",
        date: futureDate.toISOString().split("T")[0], // Fecha futura en formato YYYY-MM-DD
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
        user: { id: 1, name: "Organizer", email: "organizer@test.com" },
      },
    ];

    vi.spyOn(ApiService, "getAllEvents").mockResolvedValue(mockEvents);

    // Given - User session
    const { setSession } = useSession();
    setSession({
      token: "user-token",
      userId: 1,
      roleId: 1,
      email: "user@test.com",
      name: "Test User",
    });

    // When - Use events composable
    const { events, loading, loadUpcomingEvents } = useEvents();

    expect(events.value).toEqual([]);
    expect(loading.value).toBe(false);

    await loadUpcomingEvents();

    // Then - Events should be loaded
    expect(ApiService.getAllEvents).toHaveBeenCalled();
    expect(events.value).toEqual(mockEvents);
    expect(loading.value).toBe(false);
  });

  it("should integrate favorites with user session correctly", async () => {
    vi.spyOn(ApiService, "getFavoritesByUser").mockResolvedValue([]);
    vi.spyOn(ApiService, "addToFavorites").mockResolvedValue({ message: "Added to favorites" });
    vi.spyOn(ApiService, "removeFromFavorites").mockResolvedValue({ message: "Removed from favorites" });
    vi.spyOn(ApiService, "isFavorite").mockResolvedValue(false);

    const { setSession } = useSession();
    setSession({
      token: "user-token",
      userId: 2,
      roleId: 1,
      email: "user@test.com",
      name: "Test User",
    });

    const { favorites, loading, addToFavorites, removeFromFavorites, isFavorite } = useFavorites(2);

    expect(favorites.value).toEqual([]);

    const addResult = await addToFavorites(2, 1);
    expect(addResult).toBe(true);
    expect(ApiService.addToFavorites).toHaveBeenCalledWith(2, 1);

    const removeResult = await removeFromFavorites(2, 1);
    expect(removeResult).toBe(true);
    expect(ApiService.removeFromFavorites).toHaveBeenCalledWith(2, 1);

    const isFav = await isFavorite(2, 1);
    expect(ApiService.isFavorite).toHaveBeenCalledWith(2, 1);
  });

  it("should integrate comments with event ID correctly", async () => {
    // Mock APIs
    const mockComments = [
      {
        id: 1,
        description: "Great event!",
        date: "2024-12-18T10:00:00",
        user: { id: 3, name: "Comment User", email: "commenter@test.com" },
        event: {
          id: 1,
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
          user: { id: 1, name: "Organizer", email: "organizer@test.com" },
        },
      },
    ];

    vi.spyOn(ApiService, "getCommentsByEvent").mockResolvedValue(mockComments);
    vi.spyOn(ApiService, "createComment").mockResolvedValue(mockComments[0]);
    vi.spyOn(ApiService, "deleteComment").mockResolvedValue();

    // Given - User session
    const { setSession } = useSession();
    setSession({
      token: "user-token",
      userId: 3,
      roleId: 1,
      email: "commenter@test.com",
      name: "Comment User",
    });

    // When - Use comments for specific event
    const { comments, loading, loadComments, createComment, deleteComment } = useComments(1);

    expect(comments.value).toEqual([]);

    // Load comments
    await loadComments();
    expect(ApiService.getCommentsByEvent).toHaveBeenCalledWith(1);
    expect(comments.value).toEqual(mockComments);

    // Create comment
    const createResult = await createComment("New comment", 3);
    expect(createResult).toBe(true);
    expect(ApiService.createComment).toHaveBeenCalledWith({
      description: "New comment",
      userId: 3,
      eventId: 1,
    });

    // Delete comment
    const deleteResult = await deleteComment(1);
    expect(deleteResult).toBe(true);
    expect(ApiService.deleteComment).toHaveBeenCalledWith(1);
  });

  it("should handle role-based permissions across composables", () => {
    const { setSession } = useSession();
    const { canDeleteComment, isAdmin, isOrganizer } = useAuth();

    const roles = [
      { roleId: 1, isAdmin: false, isOrganizer: false },
      { roleId: 2, isAdmin: false, isOrganizer: true },
      { roleId: 3, isAdmin: true, isOrganizer: true },
    ];

    roles.forEach((role) => {
      setSession({
        token: `role-${role.roleId}-token`,
        userId: role.roleId,
        roleId: role.roleId,
        email: `user${role.roleId}@test.com`,
        name: `User ${role.roleId}`,
      });

      expect(isAdmin.value).toBe(role.isAdmin);
      expect(isOrganizer.value).toBe(role.isOrganizer);

      // Test comment deletion permission
      const mockComment = {
        id: 1,
        description: "Test comment",
        date: "2024-12-18T10:00:00",
        user: { id: role.roleId, name: "Comment Author", email: "author@test.com" },
        event: {
          id: 1,
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
          user: { id: 1, name: "Organizer", email: "organizer@test.com" },
        },
      };

      // User should be able to delete their own comment, admin can delete any
      const canDelete = canDeleteComment.value(mockComment);
      expect(canDelete).toBe(true); // Same user or admin
    });
  });

  it("should maintain state consistency across composable operations", async () => {
    // Mock all APIs
    vi.spyOn(ApiService, "getAllEvents").mockResolvedValue([]);
    vi.spyOn(ApiService, "getFavoritesByUser").mockResolvedValue([]);
    vi.spyOn(ApiService, "getCommentsByEvent").mockResolvedValue([]);

    // Given - User session
    const { setSession, isLoggedIn } = useSession();
    const { currentUser } = useAuth();

    setSession({
      token: "integration-token",
      userId: 1,
      roleId: 2,
      email: "integration@test.com",
      name: "Integration User",
    });

    // When - Use multiple composables
    const { events, loadUpcomingEvents } = useEvents();
    const { favorites } = useFavorites(1);
    const { comments } = useComments(1);

    // Then - All composables should have access to session state
    expect(isLoggedIn.value).toBe(true);
    expect(currentUser.value?.id).toBe(1);

    // All composables should be able to operate
    await loadUpcomingEvents();
    expect(ApiService.getAllEvents).toHaveBeenCalled();

    // State should remain consistent
    expect(isLoggedIn.value).toBe(true);
    expect(currentUser.value?.id).toBe(1);
  });

  it("should handle logged out state gracefully", async () => {
    vi.spyOn(ApiService, "getAllEvents").mockResolvedValue([]);

    const { clearSession, isLoggedIn } = useSession();
    const { currentUser } = useAuth();

    clearSession();

    const { loadUpcomingEvents } = useEvents();
    const { favorites } = useFavorites();

    expect(isLoggedIn.value).toBe(false);
    expect(currentUser.value).toBeNull();

    await loadUpcomingEvents();
    expect(ApiService.getAllEvents).toHaveBeenCalled();

    expect(favorites.value).toEqual([]);
  });
});
