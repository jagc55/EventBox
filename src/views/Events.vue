<template>
  <div class="bg-gray-300">
    <h1 class="text-2xl font-bold text-gray-800">Eventos</h1>
    <div class="me-4 p-4 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
      <div
        v-for="event in events"
        :key="event.id"
        class="bg-blue-primary border border-dark-primary rounded shadow-xl"
      >
        <img
          :src="event.image"
          alt="Imagen del event"
          class="w-full h-48 object-cover"
        />
        <div class="p-4">
          <h2 class="text-2xl font-bold text-dark-primary">
            {{ event.title }}
          </h2>
          <p class="text-sm text-dark-primary">
            Organizador: {{ event.organizer }}
          </p>
          <hr class="my-2 border border-dark-primary" />
          <p class="text-gray-700">
            Establecimiento: {{ event.establishment }}
          </p>
          <p class="text-gray-700">Fecha: {{ event.date }} {{ event.time }}</p>
          <hr class="my-2 border border-dark-primary" />

          <div class="flex gap-3">
            <span
              v-for="(tag, index) in event.tags"
              :key="index"
              class="mt-2 font-bold bg-blue-secundary text-white px-3 py-1 border border-dark-primary"
            >
              #{{ tag }}
            </span>

            <button
              class="ml-auto border border-dark-primary p-2 bg-blue-secundary"
              @click="toggleLike(event.id)"
            >
              <Star :fill="likedEvents.has(event.id) ? 'yellow' : 'none'" />
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Star } from "lucide-vue-next";

const likedEvents = ref(new Set());

function toggleLike(id: string) {
  if (likedEvents.value.has(id)) {
    likedEvents.value.delete(id);
  } else {
    likedEvents.value.add(id);
  }
}
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
