<template>
  <div class="bg-black-secundary">
    <button
      @click="openModal()"
      class="bg-purple-primary text-white p-1 mt-3 ml-4 font-bold rounded border border-white-bg"
    >
      Añadir evento
    </button>

    <!-- Formulario de edición de establecimiento -->
    <div class="bg-dark-primary border border-white-bg p-4 m-4 rounded shadow-lg w-[95%] max-w-3xl mx-auto rounded-xl">
      <h2 class="text-xl font-bold text-white-bg mb-4">Información del establecimiento</h2>
      <div v-if="!establishment">
        <p class="text-red-600">No se pudo cargar el establecimiento.</p>
      </div>
      <form v-if="establishment" @submit.prevent="submitEstablishmentForm" class="flex flex-col gap-2">
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
          v-model="establishmentForm.address"
          type="text"
          placeholder="Dirección"
          class="p-2 border border-dark-primary w-full"
        />
        <p v-if="establishmentErrors.location" class="text-red-600 text-sm">
          {{ establishmentErrors.location }}
        </p>
        <!-- Select de provincia -->
        <select v-model="establishmentForm.province" class="p-2 border border-dark-primary w-full rounded">
          <option disabled value="">Selecciona una provincia</option>
          <option v-for="prov in provinces" :key="prov.id" :value="prov.name">
            {{ prov.name }}
          </option>
        </select>
        <p v-if="establishmentErrors.province" class="text-red-600 text-sm">
          {{ establishmentErrors.province }}
        </p>
        <!-- Select de localidad -->
        <select
          v-model="establishmentForm.city"
          class="p-2 border border-dark-primary w-full rounded"
          :disabled="!establishmentForm.province || localities.length === 0"
        >
          <option disabled value="">Selecciona una localidad</option>
          <option v-for="loc in localities" :key="loc.id" :value="loc.name">
            {{ loc.name }}
          </option>
        </select>
        <p v-if="establishmentErrors.city" class="text-red-600 text-sm">
          {{ establishmentErrors.city }}
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
            class="bg-red-primary text-white px-4 py-2 rounded font-bold border border-white-bg"
          >
            Eliminar
          </button>
          <button
            v-if="establishmentModified"
            type="submit"
            class="bg-blue-secundary text-white px-4 py-2 rounded font-bold border border-white-bg"
          >
            Modificar
          </button>
        </div>
      </form>
    </div>

    <div class="my-8 mx-2 md:mx-8 lg:mx-10">
      <button
        class="w-full flex items-center justify-between bg-dark-primary border border-white-bg px-4 py-3 rounded-t-xl text-left text-2xl font-bold text-white-bg focus:outline-none"
        :class="!showUpcoming ? 'rounded-b-xl' : ''"
        @click="showUpcoming = !showUpcoming"
      >
        <span>Eventos pendientes en {{ establishment && establishment.name ? establishment.name : "" }}</span>
        <span>{{ showUpcoming ? "▲" : "▼" }}</span>
      </button>
      <div v-show="showUpcoming" class="border-x border-b border-white-bg rounded-b-xl bg-dark-primary">
        <div v-if="upcomingEvents.length === 0" class="p-4 text-white-bg">No hay eventos pendientes.</div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-5 mx-2 md:mx-5 mb-5">
          <div
            v-for="event in upcomingEvents"
            :key="event.id"
            class="flex flex-col border border-white-bg overflow-hidden bg-dark-primary shadow-xl rounded-xl mt-4"
          >
            <div class="p-4 w-full space-y-2">
              <h2 class="font-bold text-2xl text-white-bg">{{ event.title }}</h2>
              <p class="font-bold text-lg text-white-bg">{{ formatDate(event.date) }} {{ formatHour(event.time) }}</p>

              <div class="flex items-center justify-between gap-2">
                <div
                  class="flex-1 overflow-hidden whitespace-nowrap text-ellipsis"
                  :title="
                    (typeof event.tags === 'string' ? event.tags.split(',') : event.tags)
                      .map((tag: string) => '#' + tag.trim())
                      .join(' ')
                  "
                >
                  <span
                    v-for="(tag, index) in typeof event.tags === 'string' ? event.tags.split(',') : event.tags"
                    :key="index"
                    class="inline-block font-bold bg-purple-primary text-white px-3 py-1 text-sm mr-1 border border-white-bg rounded"
                  >
                    #{{ (tag as string).trim() }}
                  </span>
                </div>
                <div class="flex-shrink-0 flex gap-2 ml-2">
                  <button @click="openModal(event)">
                    <Pencil class="size-9 bg-blue-secundary" />
                  </button>
                  <button @click="deleteEvent(event.id)">
                    <Trash2 class="size-9 bg-red-primary" />
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <div class="my-8 mx-2 md:mx-8 lg:mx-10">
      <button
        class="w-full flex items-center justify-between bg-dark-primary border border-white-bg px-4 py-3 rounded-t-xl text-left text-2xl font-bold text-white-bg focus:outline-none"
        :class="!showPast ? 'rounded-b-xl' : ''"
        @click="showPast = !showPast"
      >
        <span>Eventos finalizados en {{ establishment && establishment.name ? establishment.name : "" }}</span>
        <span>{{ showPast ? "▲" : "▼" }}</span>
      </button>
      <div v-show="showPast" class="border-x border-b border-white-bg rounded-b-xl bg-dark-primary">
        <div v-if="pastEvents.length === 0" class="p-4 text-white-bg">No hay eventos finalizados.</div>
        <div v-else class="grid grid-cols-1 md:grid-cols-2 gap-5 mx-2 md:mx-5 mb-5">
          <div
            v-for="event in pastEvents"
            :key="event.id"
            class="flex flex-col border border-white-bg overflow-hidden bg-dark-primary shadow-xl rounded-xl mt-4"
          >
            <div class="p-4 w-full space-y-2">
              <h2 class="font-bold text-2xl text-white-bg">{{ event.title }}</h2>
              <p class="font-bold text-lg text-white-bg">{{ formatDate(event.date) }} {{ formatHour(event.time) }}</p>
              <div class="flex items-center justify-between gap-2">
                <div
                  class="flex-1 overflow-hidden whitespace-nowrap text-ellipsis"
                  :title="
                    (typeof event.tags === 'string' ? event.tags.split(',') : event.tags)
                      .map((tag: string) => '#' + tag.trim())
                      .join(' ')
                  "
                >
                  <span
                    v-for="(tag, index) in typeof event.tags === 'string' ? event.tags.split(',') : event.tags"
                    :key="index"
                    class="inline-block font-bold bg-purple-primary text-white px-3 py-1 text-sm mr-1 border border-white-bg rounded"
                  >
                    #{{ tag.trim() }}
                  </span>
                </div>
                <div class="flex-shrink-0 flex gap-2 ml-2">
                  <!-- Solo botón de eliminar para eventos pasados -->
                  <button @click="deleteEvent(event.id)">
                    <Trash2 class="size-9 bg-red-primary" />
                  </button>
                </div>
              </div>
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
      <div class="w-[80%] md:w-[50%] lg:w-[35%] border border-white-bg bg-dark-primary p-4 rounded-xl">
        <div class="flex justify-end mb-2">
          <button
            @click="showModal = false"
            class="border border-dark-primary size-7 bg-red-primary text-white flex items-center justify-center rounded"
          >
            <X />
          </button>
        </div>

        <form class="flex flex-col gap-2" @submit.prevent="handleSubmit">
          <p v-if="errors.form" class="text-red-600 text-sm text-center font-bold">{{ errors.form }}</p>
          <input v-model="form.title" type="text" placeholder="Título" class="p-2 border border-dark-primary" />
          <p v-if="errors.title" class="text-red-600 text-sm">
            {{ errors.title }}
          </p>

          <select v-model="form.category" class="w-full border border-dark-primary p-2 rounded">
            <option disabled value="">Selecciona una categoría</option>
            <option v-for="cat in eventCategoriesList" :key="cat.id" :value="cat.name">
              {{ cat.name }}
            </option>
          </select>
          <p v-if="errors.category" class="text-red-600 text-sm">
            {{ errors.category }}
          </p>

          <input v-model="form.date" type="date" :min="minDate" class="p-2 border border-dark-primary" />
          <p v-if="errors.date" class="text-red-600 text-sm">
            {{ errors.date }}
          </p>

          <input v-model="form.hour" type="time" class="p-2 border border-dark-primary" />
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

          <input v-model="form.image" type="text" placeholder="URL de imagen" class="p-2 border border-dark-primary" />
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
            class="bg-purple-primary text-white font-bold py-2 px-4 mt-2 self-end rounded border border-white-bg"
          >
            {{ editingEventId ? "Modificar evento" : "Añadir evento" }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, computed, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { Pencil, Trash2, X } from "lucide-vue-next";
import { useSession } from "@/composables/useSession";

const showModal = ref(false);
const editingEventId = ref<string | null>(null);
const route = useRoute();
const router = useRouter();
const { session } = useSession();

const establishment = ref<any>(null);
const events = ref<any[]>([]);

const establishmentForm = ref<any>({});
const establishmentErrors = ref<Record<string, string>>({});
const establishmentModified = computed(() => {
  if (!establishment.value) return false;
  return (
    establishmentForm.value.name !== (establishment.value.name || "") ||
    establishmentForm.value.address !== (establishment.value.address || "") ||
    establishmentForm.value.tfn !== (establishment.value.phone || "") ||
    establishmentForm.value.email !== (establishment.value.email || "") ||
    establishmentForm.value.city !== (establishment.value.locality?.name || "") ||
    establishmentForm.value.province !== (establishment.value.locality?.province?.name || "")
  );
});

const provinces = ref<{ id: number; name: string }[]>([]);
const localities = ref<{ id: number; name: string }[]>([]);
const eventCategoriesList = ref<{ id: number; name: string }[]>([]);

onMounted(async () => {
  const id = route.params.establishmentId;
  try {
    const token = localStorage.getItem("token") || sessionStorage.getItem("token");
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    // Cargar provincias
    const resProvinces = await fetch("/api/provinces", { headers });
    if (resProvinces.ok) {
      provinces.value = await resProvinces.json();
    } else {
      provinces.value = [];
    }
    // Cargar tipos de evento desde la API
    const resEventTypes = await fetch("/api/event-types", { headers });
    if (resEventTypes.ok) {
      eventCategoriesList.value = await resEventTypes.json();
    } else {
      eventCategoriesList.value = [];
    }
    // Cargar datos del establecimiento
    const resEst = await fetch(`/api/establishments/${id}`, { headers });
    if (!resEst.ok) {
      const errorText = await resEst.text();
      console.error("Error al cargar establecimiento:", errorText);
      throw new Error("No se pudo cargar el establecimiento");
    }
    establishment.value = await resEst.json();
    establishmentForm.value = {
      name: establishment.value.name || "",
      address: establishment.value.address || "",
      phone: establishment.value.phone || "",
      email: establishment.value.email || "",
      city: establishment.value.locality?.name || "",
      province: establishment.value.locality?.province?.name || "",
      tfn: establishment.value.phone || "",
    };
    // Si hay provincia, cargar localidades
    if (establishmentForm.value.province) {
      await fetchLocalities(establishmentForm.value.province);
    }
    // Cargar eventos del establecimiento
    const resEvents = await fetch(`/api/establishments/${id}/events`, { headers });
    if (!resEvents.ok) throw new Error("No se pudieron cargar los eventos");
    events.value = await resEvents.json();
  } catch (e) {
    establishment.value = null;
    events.value = [];
  }
});

async function fetchLocalities(provinceName: string) {
  if (!provinceName) {
    localities.value = [];
    establishmentForm.value.city = "";
    return;
  }
  try {
    const res = await fetch(`/api/localities/province/${encodeURIComponent(provinceName)}`);
    if (!res.ok) throw new Error("Error al obtener localidades");
    localities.value = await res.json();
    // Si la ciudad actual no está en la lista, resetear
    if (!localities.value.some((l) => l.name === establishmentForm.value.city)) {
      establishmentForm.value.city = "";
    }
  } catch (e) {
    localities.value = [];
    establishmentForm.value.city = "";
  }
}

watch(
  () => establishmentForm.value.province,
  async (newProvince) => {
    await fetchLocalities(newProvince);
  }
);

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
  if (!values.category) {
    validationErrors.category = "La categoría es obligatoria";
  } else {
    // Validar que la categoría exista en eventCategoriesList
    const eventTypeObj = eventCategoriesList.value.find(
      (et: { id: number; name: string }) => et.name === values.category
    );
    if (!eventTypeObj) {
      validationErrors.category = "Selecciona una categoría válida";
    }
  }
  if (!values.date) {
    validationErrors.date = "La fecha es obligatoria";
  } else {
    // Validar que la fecha no sea pasada
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const selectedDate = new Date(values.date);
    if (selectedDate < today) {
      validationErrors.date = "No puedes seleccionar una fecha pasada";
    }
  }
  if (!values.hour) validationErrors.hour = "La hora es obligatoria";
  if (!values.tags || values.tags.trim() === "") validationErrors.tags = "Debe añadir al menos una etiqueta";

  if (!values.image) {
    validationErrors.image = "La imagen es obligatoria";
  } else if (!isValidURL(values.image)) {
    validationErrors.image = "URL de imagen inválida";
  }

  if (values.minimumAge == null || values.minimumAge < 0) validationErrors.minimumAge = "Edad mínima no válida";

  if (values.price == null || values.price < 0) validationErrors.price = "Precio no válido";

  if (!values.description) validationErrors.description = "La descripción es obligatoria";

  return validationErrors;
}

// --- Envío Evento ---
async function handleSubmit() {
  errors.value = validateEventForm(form.value);
  if (Object.keys(errors.value).length > 0) return;

  const token = localStorage.getItem("token") || sessionStorage.getItem("token");
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(token ? { Authorization: `Bearer ${token}` } : {}),
  };

  // Buscar el id del eventType según el nombre seleccionado
  const eventTypeObj = eventCategoriesList.value.find(
    (et: { id: number; name: string }) => et.name === form.value.category
  );
  if (!eventTypeObj) {
    errors.value.form = "Selecciona una categoría válida.";
    return;
  }

  // Convertir tags a array de strings, limpiando espacios y vacíos
  const tagsArray = form.value.tags
    .split(",")
    .map((t: string) => t.trim())
    .filter((t: string) => t.length > 0);

  const eventPayload = {
    title: form.value.title,
    description: form.value.description,
    date: form.value.date,
    time: form.value.hour.length === 5 ? form.value.hour + ":00" : form.value.hour, // asegurar formato HH:mm:ss
    price: form.value.price !== null ? Number(form.value.price) : null,
    minimumAge: form.value.minimumAge !== null ? Number(form.value.minimumAge) : null,
    imageUrl: form.value.image,
    tags: tagsArray.join(","), // <-- Enviar como string
    eventType: { id: eventTypeObj.id },
    establishment: { id: establishment.value.id },
    user: { id: session.value.userId },
  };

  console.log("Payload enviado a /api/events:", eventPayload);

  try {
    let res;
    if (editingEventId.value) {
      res = await fetch(`/api/events/${editingEventId.value}`, {
        method: "PUT",
        headers,
        body: JSON.stringify(eventPayload),
      });
    } else {
      res = await fetch(`/api/events`, {
        method: "POST",
        headers,
        body: JSON.stringify(eventPayload),
      });
    }
    if (!res.ok) {
      let errorMsg = "Error al guardar el evento.";
      try {
        const errorJson = await res.json();
        if (errorJson && errorJson.message) errorMsg = errorJson.message;
        else if (typeof errorJson === "string") errorMsg = errorJson;
      } catch {
        const errorText = await res.text();
        if (errorText) errorMsg = errorText;
      }
      errors.value.form = errorMsg;
      return;
    }
    const resEvents = await fetch(`/api/establishments/${establishment.value.id}/events`, { headers });
    if (resEvents.ok) {
      events.value = await resEvents.json();
    }
    showModal.value = false;
    resetForm();
  } catch (e: any) {
    errors.value.form = e.message || e;
  }
}

// --- Abrir/Cerrar Modal Evento ---
function openModal(eventToEdit: any = null) {
  if (eventToEdit) {
    editingEventId.value = eventToEdit.id;
    form.value = {
      title: eventToEdit.title,
      date: eventToEdit.date,
      hour: eventToEdit.time ? eventToEdit.time.slice(0, 5) : "",
      tags:
        typeof eventToEdit.tags === "string"
          ? eventToEdit.tags
          : Array.isArray(eventToEdit.tags)
          ? eventToEdit.tags.join(", ")
          : "",
      image: eventToEdit.imageUrl || eventToEdit.image || "",
      minimumAge: eventToEdit.minimumAge ?? null,
      price: eventToEdit.price ?? null,
      description: eventToEdit.description ?? "",
      category: eventToEdit.eventType?.name ?? "",
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

function submitEstablishmentForm() {
  establishmentErrors.value = {};

  if (!establishmentForm.value.name) establishmentErrors.value.name = "El nombre es obligatorio";
  if (!establishmentForm.value.address) establishmentErrors.value.location = "La dirección es obligatoria";
  if (!establishmentForm.value.city) establishmentErrors.value.city = "La ciudad es obligatoria";
  if (!establishmentForm.value.province) establishmentErrors.value.province = "Selecciona una provincia";
  if (!establishmentForm.value.tfn) {
    establishmentErrors.value.tfn = "El teléfono es obligatorio";
  } else if (!/^\d+$/.test(establishmentForm.value.tfn)) {
    establishmentErrors.value.tfn = "El teléfono solo puede contener números";
  }
  if (!establishmentForm.value.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(establishmentForm.value.email)) {
    establishmentErrors.value.email = "Email no válido";
  }

  if (Object.keys(establishmentErrors.value).length === 0) {
    // Enviar datos al backend para modificar el establecimiento
    const token = localStorage.getItem("token") || sessionStorage.getItem("token");
    const headers = {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    };
    const payload = {
      name: establishmentForm.value.name,
      address: establishmentForm.value.address,
      phone: establishmentForm.value.tfn,
      email: establishmentForm.value.email,
      locality: {
        name: establishmentForm.value.city,
        province: { name: establishmentForm.value.province },
      },
    };
    fetch(`/api/establishments/${establishment.value.id}`, {
      method: "PUT",
      headers,
      body: JSON.stringify(payload),
    })
      .then(async (res) => {
        if (!res.ok) {
          let errorMsg = "Error al guardar los cambios.";
          try {
            const errorJson = await res.json();
            if (errorJson && errorJson.message) errorMsg = errorJson.message;
            else if (typeof errorJson === "string") errorMsg = errorJson;
          } catch {
            const errorText = await res.text();
            if (errorText) errorMsg = errorText;
          }
          alert(errorMsg);
          return;
        }
        // Actualizar datos locales
        establishment.value = await res.json();
        establishmentForm.value = {
          name: establishment.value.name || "",
          address: establishment.value.address || "",
          phone: establishment.value.phone || "",
          email: establishment.value.email || "",
          city: establishment.value.locality?.name || "",
          province: establishment.value.locality?.province?.name || "",
          tfn: establishment.value.phone || "",
        };
        alert("Cambios guardados correctamente");
      })
      .catch((e) => {
        alert(e.message || e);
      });
  }
}

function deleteEstablishment() {
  if (!establishment.value) return;
  if (
    confirm(
      "¿Estás seguro de que quieres eliminar el establecimiento? Esto eliminará también todos los eventos asociados."
    )
  ) {
    const token = localStorage.getItem("token") || sessionStorage.getItem("token");
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    fetch(`/api/establishments/${establishment.value.id}`, {
      method: "DELETE",
      headers,
    })
      .then(async (res) => {
        if (!res.ok) {
          let errorMsg = "Error al eliminar el establecimiento.";
          try {
            const errorJson = await res.json();
            if (errorJson && errorJson.message) errorMsg = errorJson.message;
            else if (typeof errorJson === "string") errorMsg = errorJson;
          } catch {
            const errorText = await res.text();
            if (errorText) errorMsg = errorText;
          }
          alert(errorMsg);
          return;
        }
        alert("Establecimiento y eventos asociados eliminados correctamente.");
        router.push({ name: "establishments" });
      })
      .catch((e) => {
        alert(e.message || e);
      });
  }
}

async function deleteEvent(id: string) {
  if (confirm("¿Quieres eliminar este evento?")) {
    const token = localStorage.getItem("token") || sessionStorage.getItem("token");
    const headers: Record<string, string> = token ? { Authorization: `Bearer ${token}` } : {};
    try {
      const res = await fetch(`/api/events/${id}`, {
        method: "DELETE",
        headers,
      });
      if (!res.ok) {
        let errorMsg = "Error al eliminar el evento.";
        try {
          const errorJson = await res.json();
          if (errorJson && errorJson.message) errorMsg = errorJson.message;
          else if (typeof errorJson === "string") errorMsg = errorJson;
        } catch {
          const errorText = await res.text();
          if (errorText) errorMsg = errorText;
        }
        alert(errorMsg);
        return;
      }
      // Recargar eventos tras eliminar
      const resEvents = await fetch(`/api/establishments/${establishment.value.id}/events`, { headers });
      if (resEvents.ok) {
        events.value = await resEvents.json();
      } else {
        events.value = events.value.filter((event) => event.id !== id);
      }
    } catch (e: any) {
      alert(e.message || e);
    }
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

function formatDate(dateStr: string) {
  if (!dateStr) return "";
  const [year, month, day] = dateStr.split("-");
  return `${day}/${month}/${year}`;
}
function formatHour(timeStr: string) {
  if (!timeStr) return "";
  // Puede venir como HH:mm:ss o HH:mm
  return timeStr.slice(0, 5);
}

const showUpcoming = ref(true);
const showPast = ref(false);

const upcomingEvents = computed(() => {
  const now = new Date();
  return events.value.filter((event) => {
    if (!event.date) return false;
    // Combina fecha y hora para comparar correctamente
    const eventDateTime = new Date(event.date + "T" + (event.time ? event.time : "00:00:00"));
    return eventDateTime >= now;
  });
});
const pastEvents = computed(() => {
  const now = new Date();
  return events.value.filter((event) => {
    if (!event.date) return false;
    const eventDateTime = new Date(event.date + "T" + (event.time ? event.time : "00:00:00"));
    return eventDateTime < now;
  });
});
</script>
