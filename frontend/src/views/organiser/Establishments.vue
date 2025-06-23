<template>
  <div class="bg-black-secundary">
    <button
      @click="showModal = true"
      class="bg-purple-primary text-white p-1 mt-3 ml-4 font-bold border border-white-bg rounded"
    >
      Añadir establecimiento
    </button>

    <h1 class="text-2xl font-bold text-white-bg px-4 py-2">Mis establecimientos</h1>

    <!-- Establishments grid-->
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
              <span class="font-bold">Eventos pendientes:</span>
            </p>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal -->
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
            class="mt-2 bg-purple-primary text-white font-bold px-4 py-2 ml-auto rounded border border-white-bg"
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
import { useSession } from "@/composables/useSession";
import { useRouter } from "vue-router";

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
    const res = await fetch(`/api/establishments/user/${session.value.userId}`, {
      headers: { Authorization: `Bearer ${session.value.token}` },
    });
    if (!res.ok) throw new Error("Error al obtener establecimientos");
    establishments.value = await res.json();
  } catch (e) {
    establishments.value = [];
  } finally {
    loading.value = false;
  }
}

async function fetchLocalities() {
  try {
    const res = await fetch("/api/localities");
    if (!res.ok) throw new Error("Error al obtener localidades");
    localities.value = await res.json();
  } catch (e) {
    localities.value = [];
  }
}

async function fetchProvinces() {
  try {
    const res = await fetch("/api/provinces");
    if (!res.ok) throw new Error("Error al obtener provincias");
    provinces.value = await res.json();
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
      const res = await fetch(`/api/localities/province/${encodeURIComponent(selectedProvince.name)}`);
      if (!res.ok) throw new Error("Error al obtener localidades");
      localities.value = await res.json();
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
      ...form.value,
      user: { id: session.value.userId },
      locality: { id: form.value.locality },
    };
    console.log("Enviando datos:", payload);
    const res = await fetch("/api/establishments", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value.token}`,
      },
      body: JSON.stringify(payload),
    });
    if (!res.ok) {
      const errorText = await res.text();
      console.error("Error al crear establecimiento:", errorText);
      throw new Error("Error al crear establecimiento");
    }
    await fetchEstablishments();
    showModal.value = false;
  } catch (e: any) {
    errors.value.general = e.message || "No se pudo crear el establecimiento";
  }
}

function goToEstablishment(establishmentId: string) {
  router.push({ name: "establishment", params: { establishmentId } });
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
