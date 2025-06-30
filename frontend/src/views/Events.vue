<template>
  <div class="bg-black-secundary pt-[68px]">
    <!-- Contenedor central -->
    <div class="max-w-7xl mx-auto px-6 lg:px-8 pt-4">
      <!-- Loading indicator -->
      <div v-if="loading" class="text-center py-8">
        <p class="text-white-bg text-lg">Cargando eventos...</p>
      </div>

      <!-- Error message -->
      <div v-if="error" class="text-center py-8">
        <p class="text-red-500 text-lg">{{ error }}</p>
        <button
          @click="setActiveTab(activeTab)"
          class="mt-4 bg-purple-primary text-white px-4 py-2 rounded hover:bg-purple-600"
        >
          Reintentar
        </button>
      </div>

      <!-- Buscador con el mismo ancho que el contenedor de eventos -->
      <div
        v-if="!loading && !error"
        class="border border-white-bg rounded-xl mb-5 p-4 bg-dark-primary flex flex-wrap gap-4 justify-center items-center shadow-xl"
      >
        <!-- Input de búsqueda -->
        <input
          type="text"
          placeholder="Buscador por título o palabras clave"
          class="border p-2 w-64"
          v-model="searchQuery"
        />

        <!-- Selector de fechas -->
        <input type="date" class="border p-2" v-model="selectedDate" />

        <!-- Selector de provincia -->
        <select class="border p-2" v-model="selectedProvince">
          <option disabled value="">Selector Provincia</option>
          <option v-for="prov in provinces" :key="prov" :value="prov">
            {{ prov }}
          </option>
        </select>
      </div>

      <!-- Contenedor con pestañas y eventos -->
      <div
        v-if="!loading && !error"
        class="border border-white-bg rounded-xl bg-dark-primary shadow-xl overflow-hidden mb-8"
      >
        <!-- Pestañas -->
        <div class="flex border-b border-white-bg">
          <button
            :class="[
              'flex-1 px-1 xs:px-2 sm:px-4 py-3 text-xs sm:text-sm text-white-bg border-r border-white-bg hover:bg-purple-600 transition-colors',
              activeTab === 'upcoming' ? 'bg-purple-primary' : 'hover:bg-purple-primary',
            ]"
            @click="setActiveTab('upcoming')"
          >
            Próximos eventos
          </button>
          <button
            :class="[
              'flex-1 px-1 xs:px-2 sm:px-4 py-3 text-xs sm:text-sm text-white-bg border-r border-white-bg hover:bg-purple-600 transition-colors',
              activeTab === 'finished' ? 'bg-purple-primary' : 'hover:bg-purple-primary',
            ]"
            @click="setActiveTab('finished')"
          >
            Eventos finalizados
          </button>
          <button
            :class="[
              'flex-1 px-1 xs:px-2 sm:px-4 py-3 text-xs sm:text-sm text-white-bg border-r border-white-bg hover:bg-purple-600 transition-colors',
              activeTab === 'favorites' ? 'bg-purple-primary' : 'hover:bg-purple-primary',
            ]"
            @click="setActiveTab('favorites')"
          >
            Eventos favoritos
          </button>
          <button
            :class="[
              'flex-1 px-1 xs:px-2 sm:px-4 py-3 text-xs sm:text-sm text-white-bg hover:bg-purple-600 transition-colors',
              activeTab === 'following' ? 'bg-purple-primary' : 'hover:bg-purple-primary',
            ]"
            @click="setActiveTab('following')"
          >
            Siguiendo
          </button>
        </div>

        <!-- Contenido de eventos -->
        <div class="p-6">
          <!-- Grid de eventos -->
          <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
            <!-- Mensaje cuando no hay eventos -->
            <div v-if="filteredEvents.length === 0" class="col-span-full text-center py-8">
              <p class="text-white-bg text-lg">No se encontraron eventos</p>
            </div>
            <div
              v-for="event in filteredEvents"
              :key="event.id"
              class="bg-dark-primary border border-white-bg rounded shadow-xl flex flex-col transition duration-300 hover:scale-105 cursor-pointer"
              @click="navigateToEvent(event.id)"
            >
              <img
                :src="event.imageUrl"
                alt="Imagen del evento"
                class="w-full h-48 object-cover border-b border-white-bg rounded"
              />
              <div class="p-4 flex flex-col flex-grow space-y-2">
                <h2 class="text-2xl font-bold text-white-bg truncate">
                  {{ event.title }}
                </h2>
                <p class="text-sm text-white-bg">
                  Organizador:
                  <button
                    @click.stop="openOrganizerModal(event.user)"
                    class="text-white-bg hover:text-gray-300 underline cursor-pointer"
                  >
                    {{ event.user.name }}
                  </button>
                </p>
                <hr class="my-3 border border-white-bg" />
                <p class="text-white-bg">
                  Establecimiento:
                  <button
                    @click.stop="openEstablishmentModal(event.establishment)"
                    class="text-white-bg hover:text-gray-300 underline cursor-pointer"
                  >
                    {{ event.establishment.name }}
                  </button>
                </p>
                <p class="text-white-bg">Fecha: {{ formatDate(event.date) }} {{ formatTime(event.time) }}</p>
                <p class="text-white-bg">Tipo: {{ event.eventType.name }}</p>
                <p class="text-white-bg">Precio: {{ event.price === 0 ? "Gratuito" : event.price + "€" }}</p>

                <hr class="my-3 border border-white-bg" />

                <!-- Tags con truncamiento -->
                <div
                  class="overflow-hidden whitespace-nowrap text-ellipsis"
                  :title="getEventTags(event).map((tag: string) => '#' + tag).join(' ')"
                >
                  <span
                    v-for="(tag, index) in getEventTags(event)"
                    :key="index"
                    class="inline-block font-bold bg-purple-primary text-white px-3 py-1 border border-white-bg text-sm mr-1 rounded"
                  >
                    #{{ tag }}
                  </span>
                </div>
              </div>
            </div>
          </div>
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
                {{
                  establishmentFollowLoading ? "Cargando..." : isFollowingEstablishment ? "Dejar de seguir" : "Seguir"
                }}
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
          <div
            v-else-if="currentUser && selectedEstablishment.user?.id === currentUser.id"
            class="text-sm text-gray-400"
          >
            No puedes seguir tu propio establecimiento
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useEvents } from "@/composables/useEvents";
import { formatDate, formatTime } from "@/utils/dateFormat";
import { useAuth } from "@/composables/useAuth";
import { ApiService } from "@/services/api";

const router = useRouter();

// Usar el composable de eventos
const {
  filteredEvents,
  loading,
  error,
  searchQuery,
  selectedDate,
  selectedProvince,
  activeTab,
  loadUpcomingEvents,
  setActiveTab,
  getEventTags,
} = useEvents();

// Usar el composable de autenticación
const { isLoggedIn, currentUser } = useAuth();

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

// Función para navegar al detalle del evento
const navigateToEvent = (eventId: number) => {
  router.push({ name: "event", params: { eventId: eventId.toString() } });
};

// Cargar eventos al montar el componente
onMounted(() => {
  setActiveTab("upcoming"); // Esto cargará los próximos eventos
});

const provinces = [
  "Álava",
  "Albacete",
  "Alicante",
  "Almería",
  "Asturias",
  "Ávila",
  "Badajoz",
  "Barcelona",
  "Burgos",
  "Cáceres",
  "Cádiz",
  "Cantabria",
  "Castellón",
  "Ciudad Real",
  "Córdoba",
  "Cuenca",
  "Gerona",
  "Granada",
  "Guadalajara",
  "Guipúzcoa",
  "Huelva",
  "Huesca",
  "Islas Baleares",
  "Jaén",
  "La Coruña",
  "La Rioja",
  "Las Palmas",
  "León",
  "Lérida",
  "Lugo",
  "Madrid",
  "Málaga",
  "Murcia",
  "Navarra",
  "Orense",
  "Palencia",
  "Pontevedra",
  "Salamanca",
  "Santa Cruz de Tenerife",
  "Segovia",
  "Sevilla",
  "Soria",
  "Tarragona",
  "Teruel",
  "Toledo",
  "Valencia",
  "Valladolid",
  "Vizcaya",
  "Zamora",
  "Zaragoza",
];
</script>
