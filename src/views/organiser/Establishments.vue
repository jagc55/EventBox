<template>
  <div class="bg-gray-300">
    <button
      @click="showModal = true"
      class="bg-blue-secundary text-white border border-dark-primary p-1 mt-3 ml-4 font-bold"
    >
      Añadir establecimiento
    </button>

    <h1 class="text-2xl font-bold text-dark-primary px-4 py-2">
      Mis establecimientos
    </h1>

    <!-- Establishments grid-->
    <div class="grid grid-cols-1 md:grid-cols-2 gap-5 mx-5">
      <div
        v-for="establishment in establishments"
        :key="establishment.id"
        class="flex border border-dark-primary rounded overflow-hidden bg-blue-primary"
      >
        <div class="flex flex-col justify-between p-4 w-full">
          <div>
            <h2 class="font-bold text-2xl">{{ establishment.name }}</h2>
            <p class="text-lg">
              <span class="font-bold">Dirección:</span>
              {{ establishment.location }}
            </p>
            <p class="text-lg">
              <span class="font-bold">Ciudad:</span> {{ establishment.city }}
            </p>
            <p class="text-lg">
              <span class="font-bold">Provincia:</span>
              {{ establishment.province }}
            </p>
            <p class="text-lg">
              <span class="font-bold">Teléfono:</span> {{ establishment.tfn }}
            </p>
            <p class="text-lg">
              <span class="font-bold">Email:</span> {{ establishment.email }}
            </p>
            <p class="text-lg">
              <span class="font-bold">Eventos pendientes:</span>
            </p>
          </div>
        </div>
        <div class="flex ml-auto mt-auto">
          <button>
            <Pencil
              class="mb-3 mr-3 border border-dark-primary size-9 shrink-0 bg-blue-secundary"
            />
          </button>
          <button>
            <Trash2
              class="mb-3 mr-3 border border-dark-primary size-9 shrink-0 bg-red-primary"
            />
          </button>
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
            v-model="form.location"
            type="text"
            placeholder="Dirección del establecimiento"
            class="mb-1 w-full p-2 border border-dark-primary"
          />
          <p v-if="errors.location" class="text-red-600 text-sm mb-2">
            {{ errors.location }}
          </p>

          <input
            v-model="form.city"
            type="text"
            placeholder="Localidad"
            class="mb-1 w-full p-2 border border-dark-primary"
          />
          <p v-if="errors.city" class="text-red-600 text-sm mb-2">
            {{ errors.city }}
          </p>

          <select
            v-model="form.province"
            class="w-full p-2 border border-dark-primary rounded mb-1"
          >
            <option disabled value="">Selecciona una provincia</option>
            <option v-for="prov in provinces" :key="prov" :value="prov">
              {{ prov }}
            </option>
          </select>
          <p v-if="errors.province" class="text-red-600 text-sm mb-2">
            {{ errors.province }}
          </p>

          <input
            v-model="form.tfn"
            type="text"
            placeholder="Teléfono de contacto"
            class="mb-1 w-full p-2 border border-dark-primary"
          />
          <p v-if="errors.tfn" class="text-red-600 text-sm mb-2">
            {{ errors.tfn }}
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
            class="mt-2 bg-blue-secundary border border-dark-primary text-white font-bold px-4 py-2 ml-auto"
          >
            Añadir
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

const form = ref({
  name: "",
  location: "",
  city: "",
  province: "",
  tfn: "",
  email: "",
});

const errors = ref<Record<string, string>>({});

const establishments = ref([
  {
    id: "1",
    organiserId: "1",
    name: "Casa Carli",
    location: "Calle Almendro 54",
    city: "Benalup",
    province: "Cadiz",
    tfn: "659174747",
    email: "casacarli@gmail.com",
  },
  {
    id: "2",
    name: "Hotel Witiñon",
    location: "Calle Trujano 14",
    city: "San Fernando",
    province: "Cádiz",
    tfn: "659181818",
    email: "hotelwitiñon@gmail.com",
  },
]);

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

function isNumeric(str: string) {
  return /^\d+$/.test(str);
}

function isValidEmail(str: string) {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(str);
}

function submitForm() {
  errors.value = {};

  if (!form.value.name) errors.value.name = "El nombre es obligatorio";
  if (!form.value.location)
    errors.value.location = "La dirección es obligatoria";
  if (!form.value.city) errors.value.city = "La localidad es obligatoria";
  if (!form.value.province) errors.value.province = "Selecciona una provincia";

  if (!form.value.tfn) {
    errors.value.tfn = "El teléfono es obligatorio";
  } else if (!isNumeric(form.value.tfn)) {
    errors.value.tfn = "El teléfono debe contener solo números";
  } else if (form.value.tfn.length < 9 || form.value.tfn.length > 12) {
    errors.value.tfn = "El teléfono no es válido";
  }

  if (!form.value.email) {
    errors.value.email = "El email es obligatorio";
  } else if (!isValidEmail(form.value.email)) {
    errors.value.email = "El email no es válido";
  }
}

watch(showModal, (newVal) => {
  if (!newVal) {
    errors.value = {};
    form.value = {
      name: "",
      location: "",
      city: "",
      province: "",
      tfn: "",
      email: "",
    };
  }
});
</script>
