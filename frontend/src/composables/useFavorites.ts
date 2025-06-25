import { ref, computed } from "vue";
import { ApiService, type Favorite } from "@/services/api";

export function useFavorites(userId?: number) {
  const favorites = ref<Favorite[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Obtener todos los favoritos de un usuario
  const loadFavorites = async (userIdParam?: number) => {
    const targetUserId = userIdParam || userId;
    if (!targetUserId) {
      error.value = "ID de usuario requerido";
      return;
    }

    loading.value = true;
    error.value = null;

    try {
      favorites.value = await ApiService.getFavoritesByUser(targetUserId);
    } catch (err) {
      console.error("Error loading favorites:", err);
      error.value = "Error al cargar los favoritos";
    } finally {
      loading.value = false;
    }
  };

  // Verificar si un evento está en favoritos
  const isFavorite = async (userIdParam: number, eventId: number): Promise<boolean> => {
    try {
      return await ApiService.isFavorite(userIdParam, eventId);
    } catch (err) {
      console.error("Error checking favorite status:", err);
      return false;
    }
  };

  // Añadir evento a favoritos
  const addToFavorites = async (userIdParam: number, eventId: number): Promise<boolean> => {
    try {
      await ApiService.addToFavorites(userIdParam, eventId);
      // Recargar favoritos si tenemos un userId
      if (userId) {
        await loadFavorites();
      }
      return true;
    } catch (err) {
      console.error("Error adding to favorites:", err);
      if (err instanceof Error) {
        error.value = err.message.includes("400") ? "El evento ya está en favoritos" : "Error al añadir a favoritos";
      } else {
        error.value = "Error al añadir a favoritos";
      }
      return false;
    }
  };

  // Eliminar evento de favoritos
  const removeFromFavorites = async (userIdParam: number, eventId: number): Promise<boolean> => {
    try {
      await ApiService.removeFromFavorites(userIdParam, eventId);
      // Recargar favoritos si tenemos un userId
      if (userId) {
        await loadFavorites();
      }
      return true;
    } catch (err) {
      console.error("Error removing from favorites:", err);
      if (err instanceof Error) {
        error.value = err.message.includes("400") ? "El evento no está en favoritos" : "Error al eliminar de favoritos";
      } else {
        error.value = "Error al eliminar de favoritos";
      }
      return false;
    }
  };

  // Alternar estado de favorito
  const toggleFavorite = async (userIdParam: number, eventId: number): Promise<boolean | null> => {
    const currentlyFavorite = await isFavorite(userIdParam, eventId);

    if (currentlyFavorite) {
      const success = await removeFromFavorites(userIdParam, eventId);
      return success ? false : null;
    } else {
      const success = await addToFavorites(userIdParam, eventId);
      return success ? true : null;
    }
  };

  // Computed para obtener los IDs de eventos favoritos
  const favoriteEventIds = computed(() => favorites.value.map((favorite) => favorite.event.id));

  return {
    favorites,
    favoriteEventIds,
    loading,
    error,
    loadFavorites,
    isFavorite,
    addToFavorites,
    removeFromFavorites,
    toggleFavorite,
  };
}
