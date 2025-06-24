import { ref } from "vue";
import { ApiService, type Event } from "@/services/api";

export function useEvent() {
  const event = ref<Event | null>(null);
  const loading = ref(false);
  const error = ref<string | null>(null);

  // Función para cargar un evento específico por ID
  const loadEvent = async (eventId: number) => {
    loading.value = true;
    error.value = null;
    event.value = null;

    try {
      event.value = await ApiService.getEventById(eventId);
    } catch (err) {
      error.value = err instanceof Error ? err.message : "Error al cargar el evento";
      console.error("Error loading event:", err);
    } finally {
      loading.value = false;
    }
  };

  // Función para procesar los tags (convertir string a array)
  const getEventTags = (eventData: Event): string[] => {
    if (!eventData.tags) return [];
    return eventData.tags
      .split(",")
      .map((tag) => tag.trim())
      .filter((tag) => tag.length > 0);
  };

  return {
    event,
    loading,
    error,
    loadEvent,
    getEventTags,
  };
}
