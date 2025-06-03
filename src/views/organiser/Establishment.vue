<template>
  <div class="bg-black-secundary">
    <button
      @click="openModal()"
      class="bg-purple-primary text-white p-1 mt-3 ml-4 font-bold"
    >
      Añadir evento
    </button>

    <!-- Formulario de edición de establecimiento -->
    <div
      class="bg-dark-primary border border-white-bg p-4 m-4 rounded shadow-lg w-[95%] max-w-3xl mx-auto"
    >
      <h2 class="text-xl font-bold text-white-bg mb-4">
        Información del establecimiento
      </h2>
      <form
        @submit.prevent="submitEstablishmentForm"
        class="flex flex-col gap-2"
      >
        <input
          v-model="establishmentForm.name"
          type="text"
          placeholder="Nombre"
          class="p-2 border border-dark-primary w-full"
        />
        <p v-if="establishmentErrors.name" class="text-red-600 text-sm">
          {{ establishmentErrors.name }}
        </p>

        <input
          v-model="establishmentForm.location"
          type="text"
          placeholder="Dirección"
          class="p-2 border border-dark-primary w-full"
        />
        <p v-if="establishmentErrors.location" class="text-red-600 text-sm">
          {{ establishmentErrors.location }}
        </p>

        <input
          v-model="establishmentForm.city"
          type="text"
          placeholder="Ciudad"
          class="p-2 border border-dark-primary w-full"
        />
        <p v-if="establishmentErrors.city" class="text-red-600 text-sm">
          {{ establishmentErrors.city }}
        </p>

        <select
          v-model="establishmentForm.province"
          class="p-2 border border-dark-primary w-full rounded"
        >
          <option disabled value="">Selecciona una provincia</option>
          <option v-for="prov in provinces" :key="prov" :value="prov">
            {{ prov }}
          </option>
        </select>
        <p v-if="establishmentErrors.province" class="text-red-600 text-sm">
          {{ establishmentErrors.province }}
        </p>

        <input
          v-model="establishmentForm.tfn"
          type="text"
          placeholder="Teléfono"
          class="p-2 border border-dark-primary w-full"
        />
        <p v-if="establishmentErrors.tfn" class="text-red-600 text-sm">
          {{ establishmentErrors.tfn }}
        </p>

        <input
          v-model="establishmentForm.email"
          type="email"
          placeholder="Email"
          class="p-2 border border-dark-primary w-full"
        />
        <p v-if="establishmentErrors.email" class="text-red-600 text-sm">
          {{ establishmentErrors.email }}
        </p>

        <div class="flex justify-end gap-4 mt-2">
          <button
            @click.prevent="deleteEstablishment"
            class="bg-red-primary text-white px-4 py-2 rounded font-bold"
          >
            Eliminar
          </button>

          <button
            v-if="establishmentModified"
            type="submit"
            class="bg-blue-secundary text-white px-4 py-2 rounded font-bold"
          >
            Guardar cambios
          </button>
        </div>
      </form>
    </div>

    <h1 class="text-2xl font-bold text-white-bg px-4 py-2">
      Eventos en {{ establishment }}
    </h1>

    <!-- Events grid -->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mx-5 mb-5">
      <div
        v-for="event in events"
        :key="event.id"
        class="flex flex-col border border-white-bg rounded overflow-hidden bg-dark-primary shadow-xl"
      >
        <div class="p-4 w-full space-y-2">
          <h2 class="font-bold text-2xl text-white-bg">{{ event.title }}</h2>
          <p class="font-bold text-lg text-white-bg">
            {{ event.date }} {{ event.time }}
          </p>

          <div class="flex items-center justify-between gap-2">
            <div
              class="flex-1 overflow-hidden whitespace-nowrap text-ellipsis"
              :title="event.tags.map((tag) => '#' + tag).join(' ')"
            >
              <span
                v-for="(tag, index) in event.tags"
                :key="index"
                class="inline-block font-bold bg-purple-primary text-white px-3 py-1 border border-dark-primary text-sm mr-1"
              >
                #{{ tag }}
              </span>
            </div>
            <div class="flex-shrink-0 flex gap-2 ml-2">
              <button @click="openModal(event)">
                <Pencil class="size-9 bg-blue-secundary" />
              </button>
              <button>
                <Trash2 class="size-9 bg-red-primary" />
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Create/Modify Event Modal-->
    <div
      v-show="showModal"
      class="absolute inset-0 flex items-center justify-center bg-black/20 backdrop-blur"
      @click.self="showModal = false"
    >
      <div
        class="w-[80%] md:w-[50%] lg:w-[35%] border border-white-bg bg-dark-primary p-4"
      >
        <div class="flex justify-end mb-2">
          <button
            @click="showModal = false"
            class="border border-dark-primary size-7 bg-red-primary text-white flex items-center justify-center rounded"
          >
            <X />
          </button>
        </div>

        <form class="flex flex-col gap-2" @submit.prevent="handleSubmit">
          <input
            v-model="form.title"
            type="text"
            placeholder="Título"
            class="p-2 border border-dark-primary"
          />
          <p v-if="errors.title" class="text-red-600 text-sm">
            {{ errors.title }}
          </p>

          <select
            v-model="form.category"
            class="w-full border border-dark-primary p-2 rounded"
          >
            <option disabled value="">Selecciona una categoría</option>
            <option
              v-for="category in eventCategories"
              :key="category"
              :value="category"
            >
              {{ category }}
            </option>
          </select>
          <p v-if="errors.category" class="text-red-600 text-sm">
            {{ errors.category }}
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
            class="bg-purple-primary text-white font-bold py-2 px-4 mt-2 self-end"
          >
            {{ editingEventId ? "Modificar evento" : "Añadir evento" }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed } from "vue";
import { Pencil, Trash2, X } from "lucide-vue-next";

const showModal = ref(false);
const editingEventId = ref<string | null>(null);
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
  category: "",
});

const errors = ref<Record<string, string>>({});

// --- Event Validator ---
function isValidURL(url: string) {
  try {
    new URL(url);
    return true;
  } catch {
    return false;
  }
}

function validateEventForm(values: typeof form.value) {
  const validationErrors: Record<string, string> = {};

  if (!values.title) validationErrors.title = "El título es obligatorio";
  if (!values.category)
    validationErrors.category = "La categoría es obligatoria";
  if (!values.date) validationErrors.date = "La fecha es obligatoria";
  if (!values.hour) validationErrors.hour = "La hora es obligatoria";
  if (!values.tags) validationErrors.tags = "Debe añadir al menos una etiqueta";

  if (!values.image) {
    validationErrors.image = "La imagen es obligatoria";
  } else if (!isValidURL(values.image)) {
    validationErrors.image = "URL de imagen inválida";
  }

  if (values.minimumAge == null || values.minimumAge < 0)
    validationErrors.minimumAge = "Edad mínima no válida";

  if (values.price == null || values.price < 0)
    validationErrors.price = "Precio no válido";

  if (!values.description)
    validationErrors.description = "La descripción es obligatoria";

  return validationErrors;
}

// --- Envío Evento ---
function handleSubmit() {
  errors.value = validateEventForm(form.value);
  if (Object.keys(errors.value).length > 0) return;

  const newEvent = {
    id: editingEventId.value ?? String(events.value.length + 1),
    title: form.value.title,
    organizer: "Organizador X",
    establishment: establishment.value,
    date: form.value.date,
    time: form.value.hour,
    tags: form.value.tags.split(",").map((tag) => tag.trim()),
    image: form.value.image,
  };

  if (editingEventId.value) {
    const idx = events.value.findIndex((e) => e.id === editingEventId.value);
    if (idx !== -1) events.value[idx] = newEvent;
  } else {
    events.value.push(newEvent);
  }

  showModal.value = false;
}

// --- Abrir/Cerrar Modal Evento ---
function openModal(eventToEdit: any = null) {
  if (eventToEdit) {
    editingEventId.value = eventToEdit.id;
    form.value = {
      title: eventToEdit.title,
      date: eventToEdit.date,
      hour: eventToEdit.time,
      tags: eventToEdit.tags.join(", "),
      image: eventToEdit.image,
      minimumAge: 18,
      price: 10,
      description: "Descripción simulada",
      category: "Entretenimiento",
    };
  } else {
    resetForm();
    editingEventId.value = null;
  }
  showModal.value = true;
}

function resetForm() {
  form.value = {
    title: "",
    date: "",
    hour: "",
    tags: "",
    image: "",
    minimumAge: null,
    price: null,
    description: "",
    category: "",
  };
  errors.value = {};
}

watch(showModal, (visible) => {
  if (!visible) {
    resetForm();
  }
});

const establishmentOriginal = ref({
  name: "Hotel Witiñon",
  location: "Calle Falsa 123",
  city: "Witicity",
  province: "Madrid",
  tfn: "123456789",
  email: "hotel@witinon.com",
});

const establishmentForm = ref({ ...establishmentOriginal.value });
const establishmentErrors = ref<Record<string, string>>({});

const establishmentModified = computed(
  () =>
    JSON.stringify(establishmentForm.value) !==
    JSON.stringify(establishmentOriginal.value)
);

function submitEstablishmentForm() {
  establishmentErrors.value = {};

  if (!establishmentForm.value.name)
    establishmentErrors.value.name = "El nombre es obligatorio";
  if (!establishmentForm.value.location)
    establishmentErrors.value.location = "La dirección es obligatoria";
  if (!establishmentForm.value.city)
    establishmentErrors.value.city = "La ciudad es obligatoria";
  if (!establishmentForm.value.province)
    establishmentErrors.value.province = "Selecciona una provincia";
  if (!establishmentForm.value.tfn) {
    establishmentErrors.value.tfn = "El teléfono es obligatorio";
  } else if (!/^\d+$/.test(establishmentForm.value.tfn)) {
    establishmentErrors.value.tfn = "El teléfono solo puede contener números";
  }
  if (
    !establishmentForm.value.email ||
    !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(establishmentForm.value.email)
  ) {
    establishmentErrors.value.email = "Email no válido";
  }

  if (Object.keys(establishmentErrors.value).length === 0) {
    establishmentOriginal.value = { ...establishmentForm.value };
    alert("Cambios guardados correctamente");
  }
}

function deleteEstablishment() {
  if (confirm("¿Estás seguro de que quieres eliminar el establecimiento?")) {
    alert("Establecimiento eliminado (simulado)");
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

const eventCategories = [
  "Cultural",
  "Social",
  "Educativo",
  "Deportivo",
  "Empresarial",
  "Tecnológico",
  "Religioso / Espiritual",
  "Salud y bienestar",
  "Entretenimiento",
  "Otros",
];

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
