<template>
  <div class="bg-black-secundary py-5 pt-[88px]" :class="$attrs.class">
    <!-- Loading indicator -->
    <div v-if="loading" class="text-center py-8">
      <p class="text-white-bg text-lg">Cargando evento...</p>
    </div>

    <!-- Error message -->
    <div v-if="error" class="text-center py-8">
      <p class="text-red-500 text-lg">{{ error }}</p>
      <button
        @click="loadEventData"
        class="bg-purple-primary hover:bg-purple-600 text-white px-3 py-1.5 mt-4 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg text-sm"
      >
        Reintentar
      </button>
    </div>

    <!-- Event details -->
    <div
      v-if="event && !loading && !error"
      class="mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 w-auto border border-white-bg rounded bg-dark-primary rounded-xl"
    >
      <img
        :src="event.imageUrl"
        alt="Imagen del evento"
        class="w-full h-[450px] object-cover border-b border-white-bg rounded-t-xl"
      />
      <div class="mt-3 px-5">
        <h1 class="text-2xl font-bold text-white-bg">
          {{ event.title }}
        </h1>
        <p class="text-xl font-semibold text-white-bg">{{ formatDateTime(event.date, event.time) }}</p>
        <hr class="my-3 border border-white-bg" />
        <p class="text-lg text-white-bg">
          <span class="font-bold">Organizador: </span>
          <button
            @click="openOrganizerModal(event.user)"
            class="text-white-bg hover:text-gray-300 underline cursor-pointer"
          >
            {{ event.user.name }}
          </button>
        </p>
        <p class="text-lg text-white-bg"><span class="font-bold">Tipo:</span> {{ event.eventType.name }}</p>
        <p class="text-lg text-white-bg">
          <span class="font-bold">Establecimiento: </span>
          <button
            @click="openEstablishmentModal(event.establishment)"
            class="text-white-bg hover:text-gray-300 underline cursor-pointer"
          >
            {{ event.establishment.name }}
          </button>
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold">Dirección:</span>
          {{ event.establishment.address }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold">Localidad:</span> {{ event.establishment.locality.name }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold">Provincia:</span> {{ event.establishment.locality.province.name }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold"> Edad mínima:</span>
          {{ event.minimumAge === 0 ? "Sin restricciones" : event.minimumAge + " año(s)" }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold"> Precio:</span>
          {{ event.price === 0 ? "Gratuito" : event.price + " €" }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold">Teléfono de contacto:</span> {{ event.establishment.phone }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold">Email de contacto:</span> {{ event.establishment.email }}
        </p>
        <hr class="my-3 border border-white-bg" />
        <p class="text-lg text-white-bg">{{ event.description }}</p>
        <hr class="my-3 border border-white-bg" />
        <div class="flex gap-3 mb-3">
          <span
            v-for="(tag, index) in getEventTags(event)"
            :key="index"
            class="font-bold bg-purple-primary rounded text-white px-3 py-1 border border-white-bg"
          >
            #{{ tag }}
          </span>
          <button
            class="ml-auto border border-white-bg p-2 bg-purple-primary rounded flex shrink-0 items-center size-9 hover:bg-purple-600 transition-all duration-200"
            @click="handleToggleFavorite"
            :disabled="!isLoggedIn || togglingFavorite"
            :title="
              isLoggedIn
                ? isEventFavorite
                  ? 'Quitar de favoritos'
                  : 'Añadir a favoritos'
                : 'Debes iniciar sesión para añadir favoritos'
            "
          >
            <Star :fill="isEventFavorite ? 'yellow' : 'none'" :color="isEventFavorite ? 'yellow' : 'white'" />
          </button>
        </div>
      </div>
    </div>

    <!--Comments form-->
    <div
      v-if="event && !loading && !error"
      class="mt-5 mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 w-auto border border-white-bg rounded bg-dark-primary rounded-xl"
    >
      <div class="mt-3 px-5">
        <label for="comentario" class="block text-white-bg font-bold mb-2"> Publicar un comentario: </label>
        <textarea
          id="comentario"
          rows="4"
          v-model="newCommentText"
          class="w-full p-3 border border-white-bg rounded"
          placeholder="Escribe tu comentario aquí..."
          :disabled="!isLoggedIn"
        ></textarea>
        <div class="flex">
          <p v-if="!isLoggedIn" class="text-white-bg text-sm mt-2">Debes iniciar sesión para comentar</p>
          <button
            v-if="isLoggedIn"
            class="bg-purple-primary hover:bg-purple-600 text-white px-3 py-1.5 my-3 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg text-sm ml-auto"
            @click="handlePublishComment"
            :disabled="!newCommentText.trim() || publishingComment"
          >
            {{ publishingComment ? "Publicando..." : "Publicar comentario" }}
          </button>
        </div>
      </div>
    </div>

    <!--Comments section-->
    <div
      v-if="event && !loading && !error"
      class="mt-5 mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 w-auto border border-white-bg rounded bg-dark-primary rounded-xl pb-2"
    >
      <p class="text-lg font-semibold px-5 mt-3 text-white-bg">Comentarios: {{ comments.length }}</p>

      <!-- Loading comments -->
      <div v-if="commentsLoading" class="text-center py-4">
        <p class="text-white-bg">Cargando comentarios...</p>
      </div>

      <!-- Comments error -->
      <div v-if="commentsError" class="text-center py-4">
        <p class="text-red-500">{{ commentsError }}</p>
      </div>

      <!-- No comments message -->
      <div v-if="!commentsLoading && !commentsError && comments.length === 0" class="text-center py-4">
        <p class="text-white-bg">No hay comentarios aún. ¡Sé el primero en comentar!</p>
      </div>

      <!-- Comments list -->
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="relative px-5 py-3 mx-5 my-3 bg-white border border-white-bg rounded"
      >
        <div class="flex items-center">
          <p class="text-lg font-semibold">@{{ comment.user.name }}</p>
          <p class="ml-auto text-lg font-semibold">{{ formatCommentDate(comment.date) }}</p>
        </div>
        <hr class="my-3 border border-white-bg" />
        <p class="text-lg">{{ comment.description }}</p>

        <!-- Delete button (only for comment author or admin) -->
        <button
          v-if="canDeleteComment(comment)"
          @click="handleDeleteComment(comment.id)"
          class="absolute bottom-2 right-2 p-1 text-red-500 hover:text-red-700 hover:bg-red-100 rounded"
          title="Eliminar comentario"
        >
          <Trash2 :size="16" />
        </button>
      </div>
    </div>
  </div>

  <!-- Organizer Modal -->
  <div
    v-if="showOrganizerModal"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    @click="closeOrganizerModal"
  >
    <div class="bg-dark-primary border border-white-bg rounded-xl p-6 max-w-md w-full mx-4" @click.stop>
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-xl font-bold text-white-bg">Información del Organizador</h3>
        <button @click="closeOrganizerModal" class="text-white-bg hover:text-gray-300 text-2xl">×</button>
      </div>

      <div v-if="selectedOrganizer" class="space-y-4">
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <p class="text-white-bg text-lg font-semibold">{{ selectedOrganizer.name }}</p>
            <p class="text-gray-300 text-sm" v-if="organizerEstablishments !== null">
              {{ organizerEstablishments }} establecimiento{{ organizerEstablishments !== 1 ? "s" : "" }}
            </p>
          </div>

          <div v-if="isLoggedIn && currentUser && selectedOrganizer.id !== currentUser.id" class="ml-4">
            <button
              @click="handleFollowToggle"
              :disabled="followLoading"
              :class="[
                'px-4 py-2 rounded font-medium transition-colors duration-200 whitespace-nowrap',
                isFollowingOrganizer
                  ? 'bg-gray-600 hover:bg-gray-700 text-white'
                  : 'bg-purple-primary hover:bg-purple-600 text-white',
              ]"
            >
              {{ followLoading ? "Cargando..." : isFollowingOrganizer ? "Dejar de seguir" : "Seguir" }}
            </button>
          </div>
        </div>

        <div v-if="followCounts" class="text-sm text-gray-300 flex gap-6">
          <p>Seguidores: {{ followCounts.followersCount }}</p>
          <p>Siguiendo: {{ followCounts.followingCount }}</p>
        </div>

        <div v-if="!isLoggedIn" class="text-sm text-gray-400">Inicia sesión para seguir organizadores</div>
      </div>
    </div>
  </div>

  <!-- Establishment Modal -->
  <div
    v-if="showEstablishmentModal"
    class="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50"
    @click="closeEstablishmentModal"
  >
    <div class="bg-dark-primary border border-white-bg rounded-xl p-6 max-w-md w-full mx-4" @click.stop>
      <div class="flex justify-between items-center mb-4">
        <h3 class="text-xl font-bold text-white-bg">Información del Establecimiento</h3>
        <button @click="closeEstablishmentModal" class="text-white-bg hover:text-gray-300 text-2xl">×</button>
      </div>

      <div v-if="selectedEstablishment" class="space-y-4">
        <div class="flex items-start justify-between">
          <div class="flex-1">
            <p class="text-white-bg text-lg font-semibold">{{ selectedEstablishment.name }}</p>
            <p class="text-gray-300 text-sm">{{ selectedEstablishment.address }}</p>
            <p class="text-gray-300 text-sm">
              {{ selectedEstablishment.locality?.name }}, {{ selectedEstablishment.locality?.province?.name }}
            </p>
            <p class="text-gray-300 text-sm" v-if="establishmentEvents !== null">
              {{ establishmentEvents }} eventos organizados
            </p>
          </div>

          <div v-if="isLoggedIn && currentUser && selectedEstablishment.user?.id !== currentUser.id" class="ml-4">
            <button
              @click="handleEstablishmentFollowToggle"
              :disabled="establishmentFollowLoading"
              :class="[
                'px-4 py-2 rounded font-medium transition-colors duration-200 whitespace-nowrap',
                isFollowingEstablishment
                  ? 'bg-gray-600 hover:bg-gray-700 text-white'
                  : 'bg-purple-primary hover:bg-purple-600 text-white',
              ]"
            >
              {{ establishmentFollowLoading ? "Cargando..." : isFollowingEstablishment ? "Dejar de seguir" : "Seguir" }}
            </button>
          </div>
        </div>

        <div v-if="establishmentFollowCounts" class="text-sm text-gray-300">
          <p>Seguidores: {{ establishmentFollowCounts.followersCount }}</p>
        </div>

        <div class="text-sm text-gray-300">
          <p><span class="font-medium">Organizador:</span> {{ selectedEstablishment.user?.name }}</p>
          <p><span class="font-medium">Teléfono:</span> {{ selectedEstablishment.phone }}</p>
          <p><span class="font-medium">Email:</span> {{ selectedEstablishment.email }}</p>
        </div>

        <div v-if="!isLoggedIn" class="text-sm text-gray-400">Inicia sesión para seguir establecimientos</div>
        <div v-else-if="currentUser && selectedEstablishment.user?.id === currentUser.id" class="text-sm text-gray-400">
          No puedes seguir tu propio establecimiento
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
defineOptions({
  inheritAttrs: false,
});

import { ref, onMounted, computed, watch } from "vue";
import { useRoute } from "vue-router";
import { Star, Trash2 } from "lucide-vue-next";
import { useEvent } from "@/composables/useEvent";
import { useComments } from "@/composables/useComments";
import { useAuth } from "@/composables/useAuth";
import { useSession } from "@/composables/useSession";
import { useFavorites } from "@/composables/useFavorites";
import { formatDateTime } from "@/utils/dateFormat";
import { ApiService } from "@/services/api";

const route = useRoute();
const newCommentText = ref("");
const publishingComment = ref(false);
const isEventFavorite = ref(false);
const togglingFavorite = ref(false);

// Modal state
const showOrganizerModal = ref(false);
const selectedOrganizer = ref<any>(null);
const followCounts = ref<any>(null);
const isFollowingOrganizer = ref(false);
const followLoading = ref(false);
const organizerEstablishments = ref<number | null>(null);

// Establishment modal state
const showEstablishmentModal = ref(false);
const selectedEstablishment = ref<any>(null);
const establishmentFollowCounts = ref<any>(null);
const isFollowingEstablishment = ref(false);
const establishmentFollowLoading = ref(false);
const establishmentEvents = ref<number | null>(null);

// Usar el composable para obtener el evento
const { event, loading, error, loadEvent, getEventTags } = useEvent();

// Usar el composable de autenticación
const { isLoggedIn, canDeleteComment, currentUser } = useAuth();

// Cargar sesión del localStorage
const { loadSessionFromStorage } = useSession();

// Obtener el ID del evento de la ruta
const eventId = computed(() => parseInt(route.params.eventId as string));

// Usar el composable de comentarios
const {
  comments,
  loading: commentsLoading,
  error: commentsError,
  loadComments,
  createComment,
  deleteComment,
  formatCommentDate,
} = useComments(eventId.value);

// Usar el composable de favoritos
const { isFavorite, toggleFavorite } = useFavorites();

// Función para cargar los datos del evento
const loadEventData = async () => {
  if (!isNaN(eventId.value)) {
    await loadEvent(eventId.value);
  } else {
    error.value = "ID de evento inválido";
  }
};

// Cargar evento y comentarios al montar el componente
onMounted(async () => {
  // Cargar sesión del localStorage
  loadSessionFromStorage();

  await loadEventData();
  if (!error.value) {
    await loadComments();
    // Verificar estado de favoritos si el usuario está logueado
    await checkFavoriteStatus();
  }
});

// Recargar comentarios si cambia el evento
watch(eventId, async (newEventId) => {
  if (!isNaN(newEventId)) {
    await loadComments();
    await checkFavoriteStatus();
  }
});

// Verificar favoritos cuando cambie el usuario
watch(currentUser, async (newUser) => {
  if (newUser && !isNaN(eventId.value)) {
    await checkFavoriteStatus();
  } else {
    isEventFavorite.value = false;
  }
});

// Función para publicar un comentario
const handlePublishComment = async () => {
  if (!newCommentText.value.trim() || !currentUser.value || !currentUser.value.id) return;

  publishingComment.value = true;

  try {
    const success = await createComment(newCommentText.value, currentUser.value.id);
    if (success) {
      newCommentText.value = ""; // Limpiar textarea
    }
  } finally {
    publishingComment.value = false;
  }
};

// Función para eliminar un comentario
const handleDeleteComment = async (commentId: number) => {
  if (confirm("¿Estás seguro de que quieres eliminar este comentario?")) {
    await deleteComment(commentId);
  }
};

// Función para verificar si el evento está en favoritos
const checkFavoriteStatus = async () => {
  if (currentUser.value && currentUser.value.id && !isNaN(eventId.value)) {
    try {
      isEventFavorite.value = await isFavorite(currentUser.value.id, eventId.value);
    } catch (error) {
      console.error("Error checking favorite status:", error);
      isEventFavorite.value = false;
    }
  }
};

// Función para toggle favorito
const handleToggleFavorite = async () => {
  if (!currentUser.value || !currentUser.value.id) return;

  togglingFavorite.value = true;

  try {
    const result = await toggleFavorite(currentUser.value.id, eventId.value);
    if (result !== null) {
      isEventFavorite.value = result;
    }
  } catch (error) {
    console.error("Error toggling favorite:", error);
  } finally {
    togglingFavorite.value = false;
  }
};

// Función para abrir el modal del organizador
const openOrganizerModal = async (organizer: any) => {
  selectedOrganizer.value = organizer;
  showOrganizerModal.value = true;

  // Cargar datos del organizador
  try {
    followCounts.value = await ApiService.getFollowCounts(organizer.id);

    // Cargar número de establecimientos del organizador
    const establishmentsResponse = await fetch(`http://localhost:8080/api/establishments/user/${organizer.id}`, {
      headers: {
        Authorization: `Bearer ${currentUser.value?.id ? localStorage.getItem("session") : ""}`,
      },
    });

    if (establishmentsResponse.ok) {
      const establishments = await establishmentsResponse.json();
      organizerEstablishments.value = establishments.length;
    } else {
      organizerEstablishments.value = 0;
    }

    // Verificar si seguimos al organizador
    if (isLoggedIn.value && currentUser.value && currentUser.value.id) {
      const result = await ApiService.isFollowing(currentUser.value.id, organizer.id);
      isFollowingOrganizer.value = result.isFollowing;
    }
  } catch (error) {
    console.error("Error loading organizer data:", error);
    organizerEstablishments.value = 0;
  }
};

// Función para cerrar el modal
const closeOrganizerModal = () => {
  showOrganizerModal.value = false;
  selectedOrganizer.value = null;
  followCounts.value = null;
  isFollowingOrganizer.value = false;
  organizerEstablishments.value = null;
};

// Función para seguir/dejar de seguir
const handleFollowToggle = async () => {
  if (!currentUser.value || !currentUser.value.id || !selectedOrganizer.value) return;

  followLoading.value = true;

  try {
    if (isFollowingOrganizer.value) {
      await ApiService.unfollowUser(currentUser.value.id, selectedOrganizer.value.id);
      isFollowingOrganizer.value = false;
    } else {
      await ApiService.followUser(currentUser.value.id, selectedOrganizer.value.id);
      isFollowingOrganizer.value = true;
    }

    // Actualizar contadores
    followCounts.value = await ApiService.getFollowCounts(selectedOrganizer.value.id);
  } catch (error) {
    console.error("Error toggling follow:", error);
  } finally {
    followLoading.value = false;
  }
};

// Función para abrir el modal del establecimiento
const openEstablishmentModal = async (establishment: any) => {
  selectedEstablishment.value = establishment;
  showEstablishmentModal.value = true;

  // Cargar datos del establecimiento
  try {
    // Cargar contadores de seguidores del establecimiento
    try {
      const followCountsResponse = await fetch(
        `http://localhost:8080/api/establishment-follows/establishment/${establishment.id}/count`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("session") || ""}`,
          },
        }
      );

      if (followCountsResponse.ok) {
        establishmentFollowCounts.value = await followCountsResponse.json();
      }
    } catch (error) {
      console.warn("Error loading establishment followers:", error);
    }

    // Cargar número de eventos del establecimiento
    establishmentEvents.value = 0; // Default value
    try {
      console.log(`Fetching events for establishment ID: ${establishment.id}`);
      // Intentar primero con el endpoint general de eventos y filtrar
      const eventsResponse = await fetch(`http://localhost:8080/api/events`, {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("session") || ""}`,
        },
      });

      console.log(`Events API response status: ${eventsResponse.status}`);

      if (eventsResponse.ok) {
        const allEvents = await eventsResponse.json();
        console.log(`All events received:`, allEvents);

        // Filtrar eventos que pertenecen a este establecimiento
        const establishmentEventsFiltered = Array.isArray(allEvents)
          ? allEvents.filter((event) => event.establishment?.id === establishment.id)
          : [];

        establishmentEvents.value = establishmentEventsFiltered.length;
        console.log(`Filtered events for establishment ${establishment.id}:`, establishmentEventsFiltered);
        console.log(`Set establishmentEvents to: ${establishmentEvents.value}`);
      } else {
        console.log(`Events API returned ${eventsResponse.status}, setting to 0`);
      }
    } catch (error) {
      console.warn("Network error loading events:", error);
    }

    // Verificar si seguimos al establecimiento
    if (isLoggedIn.value && currentUser.value && currentUser.value.id) {
      const result = await ApiService.isFollowingEstablishment(currentUser.value.id, establishment.id);
      isFollowingEstablishment.value = result.isFollowing;
    }
  } catch (error) {
    console.error("Error loading establishment data:", error);
    establishmentEvents.value = 0;
  }
};

// Función para cerrar el modal del establecimiento
const closeEstablishmentModal = () => {
  showEstablishmentModal.value = false;
  selectedEstablishment.value = null;
  establishmentFollowCounts.value = null;
  isFollowingEstablishment.value = false;
  establishmentEvents.value = null;
};

// Función para seguir/dejar de seguir establecimiento
const handleEstablishmentFollowToggle = async () => {
  if (!currentUser.value || !currentUser.value.id || !selectedEstablishment.value) return;

  establishmentFollowLoading.value = true;

  try {
    if (isFollowingEstablishment.value) {
      await ApiService.unfollowEstablishment(currentUser.value.id, selectedEstablishment.value.id);
      isFollowingEstablishment.value = false;
    } else {
      await ApiService.followEstablishment(currentUser.value.id, selectedEstablishment.value.id);
      isFollowingEstablishment.value = true;
    }

    // Actualizar contadores
    const followCountsResponse = await fetch(
      `http://localhost:8080/api/establishment-follows/establishment/${selectedEstablishment.value.id}/count`,
      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("session") || ""}`,
        },
      }
    );

    if (followCountsResponse.ok) {
      establishmentFollowCounts.value = await followCountsResponse.json();
    }
  } catch (error) {
    console.error("Error toggling establishment follow:", error);
  } finally {
    establishmentFollowLoading.value = false;
  }
};
</script>
