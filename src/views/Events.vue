<template>
  <div class="bg-gray-300">
    <h1 class="text-2xl font-bold text-dark-primary px-4 pt-4">Eventos</h1>

    <!-- Contenedor central -->
    <div class="max-w-screen-xl mx-auto px-6 lg:px-8">
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div
          v-for="event in events"
          :key="event.id"
          class="bg-blue-primary border border-dark-primary rounded shadow-xl flex flex-col transition duration-300 hover:scale-105"
        >
          <img
            :src="event.image"
            alt="Imagen del evento"
            class="w-full h-48 object-cover border-b border-dark-primary"
          />
          <div class="p-4 flex flex-col flex-grow">
            <h2 class="text-2xl font-bold text-dark-primary truncate">
              {{ event.title }}
            </h2>
            <p class="text-sm text-dark-primary">
              Organizador: {{ event.organizer }}
            </p>
            <hr class="my-3 border border-dark-primary" />
            <p class="text-gray-700">
              Establecimiento: {{ event.establishment }}
            </p>
            <p class="text-gray-700">
              Fecha: {{ event.date }} {{ event.time }}
            </p>
            <hr class="my-3 border border-dark-primary" />

            <div class="flex items-center gap-2 sm:gap-3 mt-auto flex-wrap">
              <span
                v-for="(tag, index) in event.tags"
                :key="index"
                class="font-bold bg-blue-secundary text-white px-3 py-1 border border-dark-primary text-sm"
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
    organizer: "Divorciado32",
    establishment: "Carcel Puerto 2",
    date: "2025-06-11",
    time: "13:30",
    tags: ["divorcio"],
    image: "/images/4.jpg",
  },
]);
</script>
