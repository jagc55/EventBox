import { ref, computed } from "vue";
import { ApiService, type Event, type Favorite } from "@/services/api";
import { useAuth } from "./useAuth";

export function useEvents() {
  const { currentUser, isLoggedIn } = useAuth();

  const events = ref<Event[]>([]);
  const favoriteEvents = ref<Event[]>([]);
  const followingEvents = ref<Event[]>([]);
  const followingEstablishmentsEvents = ref<Event[]>([]);
  const finishedEvents = ref<Event[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const activeTab = ref("upcoming"); // "upcoming", "finished", "favorites", "following", "followingEstablishments"

  // Filtros
  const searchQuery = ref("");
  const selectedDate = ref("");
  const selectedProvince = ref("");
  // Función para cargar todos los eventos (ahora solo próximos)
  const loadUpcomingEvents = async () => {
    loading.value = true;
    error.value = null;

    try {
      const allEvents = await ApiService.getAllEvents();
      const now = new Date();

      // Filtrar solo eventos futuros
      events.value = allEvents
        .filter((event) => {
          const eventDateTime = new Date(`${event.date}T${event.time}`);
          return eventDateTime > now;
        })
        .sort((a, b) => {
          const dateTimeA = new Date(`${a.date}T${a.time}`);
          const dateTimeB = new Date(`${b.date}T${b.time}`);
          return dateTimeA.getTime() - dateTimeB.getTime();
        });
    } catch (err) {
      error.value = "Error al cargar los eventos próximos. Por favor, inténtalo de nuevo.";
      console.error("Error loading upcoming events:", err);
    } finally {
      loading.value = false;
    }
  };

  // Función para cargar eventos finalizados
  const loadFinishedEvents = async () => {
    loading.value = true;
    error.value = null;

    try {
      const allEvents = await ApiService.getAllEvents();
      const now = new Date();

      // Filtrar solo eventos pasados y ordenar en orden descendente
      finishedEvents.value = allEvents
        .filter((event) => {
          const eventDateTime = new Date(`${event.date}T${event.time}`);
          return eventDateTime <= now;
        })
        .sort((a, b) => {
          const dateTimeA = new Date(`${a.date}T${a.time}`);
          const dateTimeB = new Date(`${b.date}T${b.time}`);
          return dateTimeB.getTime() - dateTimeA.getTime(); // Orden descendente
        });
    } catch (err) {
      error.value = "Error al cargar los eventos finalizados. Por favor, inténtalo de nuevo.";
      console.error("Error loading finished events:", err);
    } finally {
      loading.value = false;
    }
  };

  // Función para cargar eventos favoritos
  const loadFavoriteEvents = async () => {
    if (!isLoggedIn.value || !currentUser.value?.id) return;

    loading.value = true;
    error.value = null;

    try {
      const favorites: Favorite[] = await ApiService.getFavoritesByUser(currentUser.value.id);
      favoriteEvents.value = favorites
        .map((fav) => fav.event)
        .sort((a, b) => {
          const dateTimeA = new Date(`${a.date}T${a.time}`);
          const dateTimeB = new Date(`${b.date}T${b.time}`);
          return dateTimeA.getTime() - dateTimeB.getTime();
        });
    } catch (err) {
      error.value = "Error al cargar los eventos favoritos.";
      console.error("Error loading favorite events:", err);
    } finally {
      loading.value = false;
    }
  };

  // Función para cargar eventos de usuarios seguidos
  const loadFollowingEvents = async () => {
    if (!isLoggedIn.value || !currentUser.value?.id) return;

    loading.value = true;
    error.value = null;

    try {
      followingEvents.value = await ApiService.getEventsByFollowedUsers(currentUser.value.id);
    } catch (err) {
      error.value = "Error al cargar los eventos de usuarios seguidos.";
      console.error("Error loading following events:", err);
    } finally {
      loading.value = false;
    }
  };

  // Función para cargar eventos de establecimientos seguidos
  const loadFollowingEstablishmentsEvents = async () => {
    if (!isLoggedIn.value || !currentUser.value?.id) return;

    loading.value = true;
    error.value = null;

    try {
      followingEstablishmentsEvents.value = await ApiService.getEventsByFollowedEstablishments(currentUser.value.id);
    } catch (err) {
      error.value = "Error al cargar los eventos de establecimientos seguidos.";
      console.error("Error loading following establishments events:", err);
    } finally {
      loading.value = false;
    }
  };

  // Función para cambiar de pestaña
  const setActiveTab = (tab: string) => {
    activeTab.value = tab;

    switch (tab) {
      case "upcoming":
        loadUpcomingEvents();
        break;
      case "finished":
        loadFinishedEvents();
        break;
      case "favorites":
        loadFavoriteEvents();
        break;
      case "following":
        loadFollowingEvents();
        break;
      case "followingEstablishments":
        loadFollowingEstablishmentsEvents();
        break;
      default:
        loadUpcomingEvents();
    }
  };

  // Función para procesar los tags (convertir string a array)
  const getEventTags = (event: Event): string[] => {
    if (!event.tags) return [];
    return event.tags
      .split(",")
      .map((tag) => tag.trim())
      .filter((tag) => tag.length > 0);
  };
  // Eventos filtrados y ordenados por fecha
  const filteredEvents = computed(() => {
    let sourceEvents: Event[] = [];

    switch (activeTab.value) {
      case "upcoming":
        sourceEvents = events.value;
        break;
      case "finished":
        sourceEvents = finishedEvents.value;
        break;
      case "favorites":
        sourceEvents = favoriteEvents.value;
        break;
      case "following":
        sourceEvents = followingEvents.value;
        break;
      case "followingEstablishments":
        sourceEvents = followingEstablishmentsEvents.value;
        break;
      default:
        sourceEvents = events.value;
    }

    return sourceEvents
      .filter((event) => {
        // Filtro por búsqueda (título o tags)
        if (searchQuery.value) {
          const query = searchQuery.value.toLowerCase();
          const titleMatch = event.title.toLowerCase().includes(query);
          const tagsMatch = getEventTags(event).some((tag) => tag.toLowerCase().includes(query));
          if (!titleMatch && !tagsMatch) return false;
        }

        // Filtro por fecha
        if (selectedDate.value) {
          if (event.date !== selectedDate.value) return false;
        }

        // Filtro por provincia
        if (selectedProvince.value) {
          if (event.establishment.locality.province.name !== selectedProvince.value) return false;
        }

        return true;
      })
      .sort((a, b) => {
        const dateTimeA = new Date(`${a.date}T${a.time}`);
        const dateTimeB = new Date(`${b.date}T${b.time}`);

        // Orden según la pestaña activa
        if (activeTab.value === "finished") {
          // Para eventos finalizados: orden descendente (más recientes primero)
          return dateTimeB.getTime() - dateTimeA.getTime();
        } else {
          // Para el resto: orden ascendente (más próximos primero)
          return dateTimeA.getTime() - dateTimeB.getTime();
        }
      });
  });

  return {
    events,
    favoriteEvents,
    followingEvents,
    followingEstablishmentsEvents,
    finishedEvents,
    filteredEvents,
    loading,
    error,
    searchQuery,
    selectedDate,
    selectedProvince,
    activeTab,
    loadUpcomingEvents,
    loadFinishedEvents,
    loadFavoriteEvents,
    loadFollowingEvents,
    loadFollowingEstablishmentsEvents,
    setActiveTab,
    getEventTags,
  };
}
