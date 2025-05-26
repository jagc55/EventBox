<template>
  <div class="bg-gray-300">
    <button
      @click="showModal = true"
      class="bg-blue-secundary text-white border border-dark-primary p-1 mt-3 ml-4 font-bold"
    >
      Añadir evento
    </button>

    <h1 class="text-2xl font-bold text-dark-primary px-4 py-2">
      Eventos en {{ establishment }}
    </h1>

    <!-- Events grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mx-5">
      <div
        v-for="event in events"
        :key="event.id"
        class="flex flex-col border border-dark-primary rounded overflow-hidden bg-blue-primary"
      >
        <div class="p-4 w-full space-y-2">
          <h2 class="font-bold text-2xl">{{ event.title }}</h2>
          <p class="font-bold text-lg">{{ event.date }} {{ event.time }}</p>

          <!-- Tags + botones en línea -->
          <div class="flex items-center justify-between gap-2">
            <!-- Tags truncados con puntos suspensivos -->
            <div
              class="flex-1 overflow-hidden whitespace-nowrap text-ellipsis"
              :title="event.tags.map((tag) => '#' + tag).join(' ')"
            >
              <span
                v-for="(tag, index) in event.tags"
                :key="index"
                class="inline-block font-bold bg-blue-secundary text-white px-3 py-1 border border-dark-primary text-sm mr-1"
              >
                #{{ tag }}
              </span>
            </div>

            <!-- Botones fuera del truncamiento -->
            <div class="flex-shrink-0 flex gap-2 ml-2">
              <button>
                <Pencil
                  class="border border-dark-primary size-9 bg-blue-secundary"
                />
              </button>
              <button>
                <Trash2
                  class="border border-dark-primary size-9 bg-red-primary"
                />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
    <div
      v-show="showModal"
      class="absolute inset-0 flex items-center justify-center bg-black/40 backdrop-blur"
      @click.self="showModal = false"
    >
      <div
        class="w-[80%] md:w-[50%] lg:w-[35%] border border-dark-primary bg-blue-primary p-4"
      >
        <div class="flex justify-end mb-2">
          <button
            @click="showModal = false"
            class="border border-dark-primary size-7 bg-red-primary text-white flex items-center justify-center rounded"
          >
            <X />
          </button>
        </div>

        <!-- Formulario de evento -->
        <form class="flex flex-col gap-2" @submit.prevent="submitForm">
          <input
            v-model="form.title"
            type="text"
            placeholder="Título"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.title" class="text-red-600 text-sm">
            {{ errors.title }}
          </p>

          <input
            v-model="form.date"
            type="date"
            :min="minDate"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.date" class="text-red-600 text-sm">
            {{ errors.date }}
          </p>

          <input
            v-model="form.hour"
            type="time"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.hour" class="text-red-600 text-sm">
            {{ errors.hour }}
          </p>

          <input
            v-model="form.tags"
            type="text"
            placeholder="Etiquetas (separadas por comas)"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.tags" class="text-red-600 text-sm">
            {{ errors.tags }}
          </p>

          <input
            v-model="form.minimumAge"
            type="number"
            placeholder="Edad mínima"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.minimumAge" class="text-red-600 text-sm">
            {{ errors.minimumAge }}
          </p>

          <input
            v-model="form.price"
            type="number"
            step="0.01"
            placeholder="Precio (€)"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.price" class="text-red-600 text-sm">
            {{ errors.price }}
          </p>

          <input
            v-model="form.image"
            type="text"
            placeholder="URL de imagen"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.image" class="text-red-600 text-sm">
            {{ errors.image }}
          </p>

          <textarea
            v-model="form.description"
            placeholder="Descripción"
            class="p-2 border border-dark-primary"
          ></textarea>
          <p v-if="errors.description" class="text-red-600 text-sm">
            {{ errors.description }}
          </p>

          <button
            type="submit"
            class="bg-blue-secundary text-white font-bold py-2 px-4 mt-2 border border-dark-primary self-end"
          >
            Guardar evento
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from "vue";
import { Pencil, Trash2, X } from "lucide-vue-next";

const showModal = ref(false);
const establishment = ref("Hotel Witiñon");

const events = ref([
  {
    id: "1",
    title: "Caza del pitulin",
    organizer: "Carlitos Superstar",
    establishment: "Casa Carlos",
    date: "2025-06-10",
    time: "18:30",
    tags: ["sex", "homo", "rico", "roberto", "pelon"],
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

const form = ref({
  title: "",
  date: "",
  hour: "",
  tags: "",
  image: "",
  minimumAge: null as number | null,
  price: null as number | null,
  description: "",
});

const errors = ref<Record<string, string>>({});

function isValidURL(url: string) {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
}

function submitForm() {
  errors.value = {};

  if (!form.value.title) errors.value.title = "El título es obligatorio";
  if (!form.value.date) errors.value.date = "La fecha es obligatoria";
  if (!form.value.hour) errors.value.hour = "La hora es obligatoria";
  if (!form.value.tags) errors.value.tags = "Debe añadir al menos una etiqueta";
  if (!form.value.image) {
    errors.value.image = "La imagen es obligatoria";
  } else if (!isValidURL(form.value.image)) {
    errors.value.image = "URL de imagen inválida";
  }
  if (form.value.minimumAge == null || form.value.minimumAge < 0)
    errors.value.minimumAge = "Edad mínima no válida";
  if (form.value.price == null || form.value.price < 0)
    errors.value.price = "Precio no válido";
  if (!form.value.description)
    errors.value.description = "La descripción es obligatoria";

  if (Object.keys(errors.value).length === 0) {
    events.value.push({
      id: String(events.value.length + 1),
      title: form.value.title,
      organizer: "Organizador X",
      establishment: establishment.value,
      date: form.value.date,
      time: form.value.hour,
      tags: form.value.tags.split(",").map((tag) => tag.trim()),
      image: form.value.image,
    });
    showModal.value = false;
  }
}

const minDate = ref("");

function getTomorrowDate() {
  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);
  const year = tomorrow.getFullYear();
  const month = String(tomorrow.getMonth() + 1).padStart(2, "0");
  const day = String(tomorrow.getDate()).padStart(2, "0");
  return `${year}-${month}-${day}`;
}

minDate.value = getTomorrowDate();

watch(showModal, (newVal) => {
  if (!newVal) {
    form.value = {
      title: "",
      date: "",
      hour: "",
      tags: "",
      image: "",
      minimumAge: null,
      price: null,
      description: "",
    };
    errors.value = {};
  }
});
</script>
