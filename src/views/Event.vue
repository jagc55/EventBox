<template>
  <div class="bg-black-secundary py-5">
    <div
      class="mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 w-auto border border-white-bg rounded bg-dark-primary rounded-xl"
    >
      <img
        :src="events[0].image"
        alt="Imagen del evento"
        class="w-full h-[450px] object-cover border-b border-white-bg rounded-t-xl"
      />
      <div class="mt-3 px-5">
        <h1 class="text-2xl font-bold text-white-bg">
          {{ events[0].title }}
        </h1>
        <p class="text-xl font-semibold text-white-bg">{{ events[0].date }} a las {{ events[0].hour }}</p>
        <hr class="my-3 border border-white-bg" />
        <p class="text-lg text-white-bg"><span class="font-bold">Organizador:</span> {{ events[0].organizer }}</p>
        <p class="text-lg text-white-bg"><span class="font-bold">Tipo:</span> {{ events[0].tipe }}</p>
        <p class="text-lg text-white-bg"><span class="font-bold">Local:</span> {{ establishments[0].name }}</p>
        <p class="text-lg text-white-bg">
          <span class="font-bold">Dirección:</span>
          {{ establishments[0].adress }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold"> Ciudad: </span>{{ establishments[0].city }},
          {{ establishments[0].province }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold"> Edad mínima:</span>
          {{ events[0].minimumAge === "0" ? "Sin restricciones" : events[0].minimumAge + " año(s)" }}
        </p>
        <p class="text-lg text-white-bg">
          <span class="font-bold"> Precio:</span>
          {{ events[0].price === "0" ? "Gratuito" : events[0].price + " €" }}
        </p>
        <hr class="my-3 border border-white-bg" />
        <p class="text-lg text-white-bg">{{ events[0].description }}</p>
        <hr class="my-3 border border-white-bg" />
        <div class="flex gap-3 mb-3">
          <span
            v-for="(tag, index) in events[0].tags"
            :key="index"
            class="font-bold bg-purple-primary rounded text-white px-3 py-1 border border-white-bg"
          >
            #{{ tag }}
          </span>
          <button
            class="ml-auto border border-white-bg p-2 bg-purple-primary rounded flex shrink-0 items-center size-9"
            @click="likedEvents = !likedEvents"
          >
            <Star :fill="likedEvents ? 'yellow' : 'none'" />
          </button>
        </div>
      </div>
    </div>

    <!--Coments-->
    <div
      class="mt-5 mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 w-auto border border-white-bg rounded bg-dark-primary rounded-xl"
    >
      <div class="mt-3 px-5">
        <label for="comentario" class="block text-white-bg font-bold mb-2"> Publicar un comentario: </label>
        <textarea
          id="comentario"
          rows="4"
          class="w-full p-3 border border-white-bg rounded"
          placeholder="Escribe tu comentario aquí..."
        ></textarea>
        <div class="flex">
          <button class="ml-auto my-3 p-2 bg-purple-primary border border-white-bg text-white font-bold">
            Publicar
          </button>
        </div>
      </div>
    </div>

    <!--Comments section-->
    <div
      class="mt-5 mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 w-auto border border-white-bg rounded bg-dark-primary rounded-xl"
    >
      <p class="text-lg font-semibold px-5 mt-3 text-white-bg">Comentarios: {{ comments.length }}</p>

      <div
        v-for="comment in comments"
        :key="comment.id"
        class="px-5 py-3 mx-5 my-3 bg-white border border-white-bg rounded"
      >
        <div class="flex items-center">
          <p class="text-lg font-semibold">@{{ comment.username }}</p>
          <p class="ml-auto text-lg font-semibold">{{ comment.date }}</p>
        </div>
        <hr class="my-3 border border-white-bg" />
        <p class="text-lg">{{ comment.text }}</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { Star } from "lucide-vue-next";
const likedEvents = ref(false);

const events = ref([
  {
    id: "1",
    title: "Caza del witi amarillo",
    tipe: "Otros",
    organizer: "Witi Witiñon",
    establishment: "Hotel Witiñon",
    date: "10/06/2025",
    hour: "18:30",
    tags: ["Witi", "Gekko", "Amarillo"],
    image: "/images/5.jpg",
    minimumAge: "3",
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

const comments = ref([
  {
    id: "2",
    username: "jagc55",
    date: "05/10/2024",
    text: "Voy a escribir el mismo comentario porque los witis me gustan mucho, me hacen muy feliz y me ayudan a llevar hacia delante mi día a día.",
  },

  {
    id: "1",
    username: "jagc55",
    date: "05/10/2024",
    text: "Los witis me gustan mucho, me hacen muy feliz y me ayudan a llevar hacia delante mi día a día.",
  },
]);
</script>
