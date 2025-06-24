<template>
  <div class="bg-black-secundary py-5">
    <!-- Loading indicator -->
    <div v-if="loading" class="text-center py-8">
      <p class="text-white-bg text-lg">Cargando evento...</p>
    </div>

    <!-- Error message -->
    <div v-if="error" class="text-center py-8">
      <p class="text-red-500 text-lg">{{ error }}</p>
      <button @click="loadEventData" class="mt-4 bg-purple-primary text-white px-4 py-2 rounded hover:bg-purple-600">
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
        <p class="text-lg text-white-bg"><span class="font-bold">Organizador:</span> {{ event.user.name }}</p>
        <p class="text-lg text-white-bg"><span class="font-bold">Tipo:</span> {{ event.eventType.name }}</p>
        <p class="text-lg text-white-bg"><span class="font-bold">Local:</span> {{ event.establishment.name }}</p>
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
          v-model="newCommentText"
          class="w-full p-3 border border-white-bg rounded"
          placeholder="Escribe tu comentario aquí..."
        ></textarea>
        <div class="flex">
          <button
            class="ml-auto my-3 p-2 bg-purple-primary border border-white-bg text-white font-bold"
            @click="publishComment"
          >
            Publicar
          </button>
        </div>
      </div>
    </div>

    <!--Comments section-->
    <div
      class="mt-5 mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 w-auto border border-white-bg rounded bg-dark-primary rounded-xl pb-2"
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
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { Star } from "lucide-vue-next";
import { useEvent } from "@/composables/useEvent";
import { formatDateTime } from "@/utils/dateFormat";

const route = useRoute();
const likedEvents = ref(false);
const newCommentText = ref("");

// Usar el composable para obtener el evento
const { event, loading, error, loadEvent, getEventTags } = useEvent();

// Función para cargar los datos del evento
const loadEventData = async () => {
  const eventId = parseInt(route.params.eventId as string);
  if (!isNaN(eventId)) {
    await loadEvent(eventId);
  } else {
    error.value = "ID de evento inválido";
  }
};

// Cargar evento al montar el componente
onMounted(() => {
  loadEventData();
});

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

const publishComment = () => {
  const trimmed = newCommentText.value.trim();
  if (!trimmed) return;

  const newComment = {
    id: String(Date.now()),
    username: "usuarioActual", // Puedes reemplazar esto con el nombre real del usuario logueado
    date: new Date().toLocaleDateString("es-ES"),
    text: trimmed,
  };

  comments.value.unshift(newComment);
  newCommentText.value = ""; // Limpiar textarea
};
</script>
