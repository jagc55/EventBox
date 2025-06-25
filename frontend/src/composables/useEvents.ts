import { ref, computed } from "vue";
import { ApiService, type Event } from "@/services/api";

export function useEvents() {
  const events = ref<Event[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Filtros
  const searchQuery = ref("");
  const selectedDate = ref("");
  const selectedProvince = ref("");
  // Función para cargar todos los eventos
  const loadEvents = async () => {
    loading.value = true;
    error.value = null;

    try {
      // Opción 1: Usar endpoint ordenado del backend (más eficiente)
      // events.value = await ApiService.getAllEventsOrdered();

      // Opción 2: Obtener todos y ordenar en frontend (implementación actual)
      events.value = await ApiService.getAllEvents();
    } catch (err) {
      error.value = "Error al cargar los eventos. Por favor, inténtalo de nuevo.";
      console.error("Error loading events:", err);
    } finally {
      loading.value = false;
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
  // Eventos filtrados y ordenados por fecha (más recientes primero)
  const filteredEvents = computed(() => {
    return events.value
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
        // Ordenar por fecha y hora (más próximos primero - orden ascendente)
        const dateTimeA = new Date(`${a.date}T${a.time}`);
        const dateTimeB = new Date(`${b.date}T${b.time}`);

        // Orden ascendente (más próximos primero)
        return dateTimeA.getTime() - dateTimeB.getTime();
      });
  });

  return {
    events,
    filteredEvents,
    loading,
    error,
    searchQuery,
    selectedDate,
    selectedProvince,
    loadEvents,
    getEventTags,
  };
}
