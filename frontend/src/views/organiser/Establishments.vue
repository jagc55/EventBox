<template>
  <div class="bg-black-secundary pt-[68px]">
    <button
      @click="showModal = true"
      class="bg-purple-primary hover:bg-purple-600 text-white px-3 py-1.5 mt-3 ml-4 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg text-sm"
    >
      Añadir establecimiento
    </button>

    <div class="flex items-center gap-3 px-4 py-2">
      <div class="w-2 h-8 bg-gradient-to-b from-purple-primary to-purple-600 rounded-full"></div>
      <h1 class="text-2xl font-bold text-white-bg">Mis establecimientos</h1>
    </div>

    <div class="grid grid-cols-1 md:grid-cols-2 gap-8 mx-5">
      <div
        v-for="establishment in establishments"
        :key="establishment.id"
        class="flex border border-white-bg rounded-xl overflow-hidden bg-dark-primary cursor-pointer hover:border-purple-primary transition"
        @click="goToEstablishment(establishment.id)"
      >
        <div class="flex flex-col justify-between p-4 w-full">
          <div>
            <h2 class="font-bold text-2xl text-white-bg">
              {{ establishment.name }}
            </h2>
            <p class="text-lg text-white-bg">
              <span class="font-bold">Dirección:</span>
              {{ establishment.address }}
            </p>
            <p class="text-lg text-white-bg">
              <span class="font-bold">Ciudad:</span>
              {{ establishment.locality?.name }}
            </p>
            <p class="text-lg text-white-bg">
              <span class="font-bold">Provincia:</span>
              {{ establishment.locality?.province?.name }}
            </p>
            <p class="text-lg text-white-bg"><span class="font-bold">Teléfono:</span> {{ establishment.phone }}</p>
            <p class="text-lg text-white-bg"><span class="font-bold">Email:</span> {{ establishment.email }}</p>
            <p class="text-lg text-white-bg">
              <span class="font-bold">Eventos pendientes: </span>
              <span v-if="establishment.events">{{ countUpcomingEvents(establishment.events) }}</span>
            </p>
          </div>
        </div>
      </div>
    </div>

    <div
      v-show="showModal"
      class="absolute inset-0 flex items-center justify-center bg-black/20 backdrop-blur"
      @click.self="showModal = false"
    >
      <div class="w-[80%] md:w-[50%] lg:w-[35%] border border-white-bg bg-dark-primary p-4 rounded-xl">
        <div class="flex justify-end mb-2">
          <button
            @click="showModal = false"
            class="bg-red-primary hover:bg-red-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg flex items-center justify-center"
          >
            <X class="w-5 h-5" />
          </button>
        </div>

        <form @submit.prevent="submitForm" class="flex flex-col items-center">
          <input
            v-model="form.name"
            type="text"
            placeholder="Nombre del establecimiento"
            class="mb-1 w-full p-2 border border-dark-primary"
          />
          <p v-if="errors.name" class="text-red-600 text-sm mb-2">
            {{ errors.name }}
          </p>

          <input
            v-model="form.address"
            type="text"
            placeholder="Dirección del establecimiento"
            class="mb-1 w-full p-2 border border-dark-primary"
          />
          <p v-if="errors.address" class="text-red-600 text-sm mb-2">
            {{ errors.address }}
          </p>

          <select v-model="form.province" class="w-full p-2 border border-dark-primary rounded mb-1">
            <option disabled value="">Selecciona una provincia</option>
            <option v-for="prov in provinces" :key="prov.id" :value="prov.id">
              {{ prov.name }}
            </option>
          </select>
          <p v-if="errors.province" class="text-red-600 text-sm mb-2">
            {{ errors.province }}
          </p>

          <select
            v-model.number="form.locality"
            class="w-full p-2 border border-dark-primary rounded mb-1"
            :disabled="!form.province || localities.length === 0"
          >
            <option disabled value="">Selecciona una localidad</option>
            <option v-for="loc in localities" :key="loc.id" :value="loc.id">
              {{ loc.name }}
            </option>
          </select>
          <p v-if="errors.locality" class="text-red-600 text-sm mb-2">
            {{ errors.locality }}
          </p>

          <input
            v-model="form.phone"
            type="text"
            placeholder="Teléfono de contacto"
            class="mb-1 w-full p-2 border border-dark-primary"
          />
          <p v-if="errors.phone" class="text-red-600 text-sm mb-2">
            {{ errors.phone }}
          </p>

          <input
            v-model="form.email"
            type="email"
            placeholder="Email de contacto"
            class="mb-1 w-full p-2 border border-dark-primary"
          />
          <p v-if="errors.email" class="text-red-600 text-sm mb-2">
            {{ errors.email }}
          </p>

          <button
            type="submit"
            class="bg-purple-primary hover:bg-purple-600 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg self-end"
          >
            Añadir
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, watch, onMounted } from "vue";
import { X } from "lucide-vue-next";
import { useRouter } from "vue-router";
import { useSession } from "../../composables/useSession";
import { ApiService } from "../../services/api";

const showModal = ref(false);

const form = ref({
  name: "",
  address: "",
  locality: "",
  province: "",
  phone: "",
  email: "",
});

const errors = ref<Record<string, string>>({});

interface Province {
  id: number;
  name: string;
}

interface Locality {
  id: number;
  name: string;
  province: Province;
}

interface Establishment {
  id: string;
  user?: { id: string };
  name: string;
  address: string;
  locality: Locality;
  province: string;
  phone: string;
  email: string;
  events?: any[];
}

const establishments = ref<Establishment[]>([]);
const loading = ref(false);

const provinces = ref<{ id: number; name: string }[]>([]);
const localities = ref<{ id: number; name: string }[]>([]);

const { session } = useSession();
const router = useRouter();

async function fetchEstablishments() {
  if (!session.value.userId) return;
  loading.value = true;
  try {
    // Usar ApiService en lugar de fetch directo
    const ests = await ApiService.getAllEstablishments();
    // Filtrar solo los establecimientos del usuario actual
    const userEstablishments = ests.filter((est: any) => est.user?.id === session.value.userId);

    const withEvents = await Promise.all(
      userEstablishments.map(async (est: any) => {
        try {
          const events = await ApiService.getEventsByEstablishment(est.id);
          est.events = events;
        } catch {
          est.events = [];
        }
        return est;
      })
    );
    establishments.value = withEvents;
  } catch (e) {
    establishments.value = [];
  } finally {
    loading.value = false;
  }
}

async function fetchLocalities() {
  try {
    localities.value = await ApiService.getAllLocalities();
  } catch (e) {
    localities.value = [];
  }
}

async function fetchProvinces() {
  try {
    provinces.value = await ApiService.getAllProvinces();
  } catch (e) {
    provinces.value = [];
  }
}

watch(
  () => form.value.province,
  async (newProvinceId) => {
    if (!newProvinceId) {
      localities.value = [];
      form.value.locality = "";
      return;
    }
    const selectedProvince = provinces.value.find((p) => p.id === Number(newProvinceId));
    if (!selectedProvince) {
      localities.value = [];
      form.value.locality = "";
      return;
    }
    try {
      localities.value = await ApiService.getLocalitiesByProvince(selectedProvince.name);
      form.value.locality = "";
    } catch (e) {
      localities.value = [];
      form.value.locality = "";
    }
  }
);

function isNumeric(str: string) {
  return /^\d+$/.test(str);
}

function isValidEmail(str: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(str);
}

async function submitForm() {
  errors.value = {};

  if (!form.value.name) errors.value.name = "El nombre es obligatorio";
  if (!form.value.address) errors.value.address = "La dirección es obligatoria";
  if (!form.value.locality) errors.value.locality = "La localidad es obligatoria";
  if (!form.value.province) errors.value.province = "Selecciona una provincia";

  if (!form.value.phone) {
    errors.value.phone = "El teléfono es obligatorio";
  } else if (!isNumeric(form.value.phone)) {
    errors.value.phone = "El teléfono debe contener solo números";
  } else if (form.value.phone.length < 9 || form.value.phone.length > 12) {
    errors.value.phone = "El teléfono no es válido";
  }

  if (!form.value.email) {
    errors.value.email = "El email es obligatorio";
  } else if (!isValidEmail(form.value.email)) {
    errors.value.email = "El email no es válido";
  }

  if (Object.keys(errors.value).length > 0) return;

  try {
    const payload = {
      name: form.value.name,
      address: form.value.address,
      phone: form.value.phone,
      email: form.value.email,
      user: { id: session.value.userId! },
      locality: { id: Number(form.value.locality) },
    };
    console.log("Enviando datos:", payload);
    await ApiService.createEstablishment(payload);
    await fetchEstablishments();
    showModal.value = false;
  } catch (e: any) {
    errors.value.general = e.message || "No se pudo crear el establecimiento";
  }
}

function goToEstablishment(establishmentId: string) {
  router.push({ name: "establishment", params: { establishmentId } });
}

function countUpcomingEvents(events: any[]) {
  if (!Array.isArray(events)) return 0;
  const now = new Date();
  return events.filter((ev) => {
    if (!ev.date) return false;
    const [year, month, day] = ev.date.split("-");
    const eventDate = new Date(Number(year), Number(month) - 1, Number(day));
    return eventDate >= new Date(now.getFullYear(), now.getMonth(), now.getDate());
  }).length;
}

watch(showModal, (newVal) => {
  if (!newVal) {
    errors.value = {};
    form.value = {
      name: "",
      address: "",
      locality: "",
      province: "",
      phone: "",
      email: "",
    };
  }
});

onMounted(() => {
  fetchEstablishments();
  fetchLocalities();
  fetchProvinces();
});
</script>
