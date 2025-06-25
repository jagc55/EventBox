<template>
  <div class="bg-black-secundary pt-[68px]">
    <!-- Contenedor central -->
    <div class="max-w-screen-xl mx-auto px-6 lg:px-8 pt-4">
      <!-- Loading indicator -->
      <div v-if="loading" class="text-center py-8">
        <p class="text-white-bg text-lg">Cargando eventos...</p>
      </div>

      <!-- Error message -->
      <div v-if="error" class="text-center py-8">
        <p class="text-red-500 text-lg">{{ error }}</p>
        <button @click="loadEvents" class="mt-4 bg-purple-primary text-white px-4 py-2 rounded hover:bg-purple-600">
          Reintentar
        </button>
      </div>

      <!-- Filtros -->
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

      <!-- Grid de eventos -->
      <div v-if="!loading && !error" class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
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
            <p class="text-sm text-white-bg">Organizador: {{ event.user.name }}</p>
            <hr class="my-3 border border-white-bg" />
            <p class="text-white-bg">Establecimiento: {{ event.establishment.name }}</p>
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
</template>

<script setup lang="ts">
import { onMounted } from "vue";
import { useRouter } from "vue-router";
import { useEvents } from "@/composables/useEvents";
import { formatDate, formatTime } from "@/utils/dateFormat";

const router = useRouter();

// Usar el composable de eventos
const { filteredEvents, loading, error, searchQuery, selectedDate, selectedProvince, loadEvents, getEventTags } =
  useEvents();

// Función para navegar al detalle del evento
const navigateToEvent = (eventId: number) => {
  router.push({ name: "event", params: { eventId: eventId.toString() } });
};

// Cargar eventos al montar el componente
onMounted(() => {
  loadEvents();
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
