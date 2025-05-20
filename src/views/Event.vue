<template>
  <div class="bg-gray-300 min-h-screen py-5">
    <div
      class="mx-52 w-auto border border-dark-primary rounded bg-blue-primary"
    >
      <img
        :src="events[0].image"
        alt="Imagen del evento"
        class="w-full h-[450px] object-cover border-b border-dark-primary"
      />
      <div class="mt-3 px-5">
        <h1 class="text-2xl font-bold text-dark-primary">
          {{ events[0].title }}
        </h1>
        <p>{{ events[0].date }} a las {{ events[0].hour }}</p>
        <hr class="my-3 border border-dark-primary" />
        <p>Organizador: {{ events[0].organizer }}</p>
        <p>Local: {{ establishments[0].name }}</p>
        <p>Dirección: {{ establishments[0].adress }}</p>
        <p>
          Ciudad: {{ establishments[0].city }}, {{ establishments[0].province }}
        </p>
        <p>
          Precio:
          {{ events[0].price === "0" ? "Gratuito" : events[0].price + " €" }}
        </p>
        <hr class="my-3 border border-dark-primary" />
        <p>{{ events[0].description }}</p>
        <hr class="my-3 border border-dark-primary" />
        <div class="flex gap-3 mb-3">
          <span
            v-for="(tag, index) in events[0].tags"
            :key="index"
            class="font-bold bg-blue-secundary text-white px-3 py-1 border border-dark-primary"
          >
            #{{ tag }}
          </span>
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
    title: "Caza del witi amarillo",
    organizer: "Witi Witiñon",
    establishment: "Hotel Witiñon",
    date: "10/06/2025",
    hour: "18:30",
    tags: ["Witi", "Gekko", "Amarillo"],
    image: "/images/5.jpg",
    price: "0",
    description:
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
      "Donec et ultrices eros. " +
      "Pellentesque sapien nibh, malesuada nec fermentum in, rutrum sed nunc. " +
      "Suspendisse vitae felis venenatis, venenatis lorem et, sollicitudin elit. " +
      "Etiam facilisis, ligula eu euismod sollicitudin, quam erat pulvinar nisi, id facilisis mi nunc a neque. " +
      "Vivamus interdum arcu quis euismod consectetur. " +
      "Aliquam ligula leo, pulvinar et malesuada ac, consequat vitae neque. " +
      "Suspendisse potenti. " +
      "Nullam non porttitor tortor. " +
      "Vestibulum vestibulum quam eget turpis iaculis, nec porta est elementum." +
      "Lorem ipsum dolor sit amet, consectetur adipiscing elit. " +
      "Donec et ultrices eros. " +
      "Pellentesque sapien nibh, malesuada nec fermentum in, rutrum sed nunc. " +
      "Suspendisse vitae felis venenatis, venenatis lorem et, sollicitudin elit. " +
      "Etiam facilisis, ligula eu euismod sollicitudin, quam erat pulvinar nisi, id facilisis mi nunc a neque. " +
      "Vivamus interdum arcu quis euismod consectetur. " +
      "Aliquam ligula leo, pulvinar et malesuada ac, consequat vitae neque. " +
      "Suspendisse potenti. " +
      "Nullam non porttitor tortor. " +
      "Vestibulum vestibulum quam eget turpis iaculis, nec porta est elementum.",
  },
]);

const establishments = ref([
  {
    id: "1",
    name: "Hotel Witiñon",
    adress: "Calle Segovia nº 3",
    city: "San Fernando",
    province: "Cádiz",
  },
]);
</script>
