<template>
  <div class="bg-black-secundary">
    <!-- Contenedor central -->
    <div class="max-w-screen-xl mx-auto px-6 lg:px-8 pt-4">
      <div
        class="border border-white-bg rounded mb-5 p-4 bg-dark-primary flex flex-wrap gap-4 justify-center items-center shadow-xl"
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
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="event in events"
          :key="event.id"
          class="bg-dark-primary border border-white-bg rounded shadow-xl flex flex-col transition duration-300 hover:scale-105"
        >
          <img
            :src="event.image"
            alt="Imagen del evento"
            class="w-full h-48 object-cover border-b border-white-bg"
          />
          <div class="p-4 flex flex-col flex-grow space-y-2">
            <h2 class="text-2xl font-bold text-white-bg truncate">
              {{ event.title }}
            </h2>
            <p class="text-sm text-white-bg">
              Organizador: {{ event.organizer }}
            </p>
            <hr class="my-3 border border-white-bg" />
            <p class="text-white-bg">
              Establecimiento: {{ event.establishment }}
            </p>
            <p class="text-white-bg">
              Fecha: {{ event.date }} {{ event.time }}
            </p>
            <p class="text-white-bg">Tipo: {{ event.tipe }}</p>

            <hr class="my-3 border border-white-bg" />

            <!-- Tags con truncamiento -->
            <div
              class="overflow-hidden whitespace-nowrap text-ellipsis"
              :title="event.tags.map((tag) => '#' + tag).join(' ')"
            >
              <span
                v-for="(tag, index) in event.tags"
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
import { ref } from "vue";

const events = ref([
  {
    id: "1",
    title: "Caza del pitulin",
    tipe: "Deportivo",
    organizer: "Carlitos Superstar",
    establishment: "Casa Carlos",
    date: "2025-06-10",
    time: "18:30",
    tags: ["sex", "homo", "rico"],
    image: "/images/1.jpg",
  },
  {
    id: "2",
    title: "Concurso malos programadores",
    tipe: "Tecnológico",
    organizer: "Bob Anton",
    establishment: "Teatro Municipal",
    date: "2025-06-11",
    time: "13:30",
    tags: ["calvo", "malalogica", "puto"],
    image: "/images/2.jpg",
  },
  {
    id: "3",
    title: "Baile del tulikitaka",
    tipe: "Musical",
    organizer: "Anton Bailon",
    establishment: "Plaza de toros",
    date: "2025-06-11",
    time: "13:30",
    tags: ["tulikitaka", "baile"],
    image: "/images/3.jpg",
  },
  {
    id: "4",
    title: "Cariño perdoname solo quiero ver a los niños",
    tipe: "Otros",
    organizer: "Divorciado32",
    establishment: "Carcel Puerto 2",
    date: "2025-06-11",
    time: "13:30",
    tags: ["divorcio"],
    image: "/images/4.jpg",
  },
]);

const searchQuery = ref("");
const selectedDate = ref("");
const selectedProvince = ref("");

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
