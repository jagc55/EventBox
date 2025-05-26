<template>
  <div
    class="h-full bg-cover bg-[center_10%] scale-105 bg-[url(@/assets/images/bg.png)]"
  >
    <div class="flex items-center justify-center size-full bg-black/30">
      <!-- backdrop-blur-sm-->
      <div class="bg-blue-primary rounded-lg shadow-xl w-full max-w-lg p-10">
        <h1 class="text-2xl font-bold text-dark-primary mb-4">
          Registrar usuario
        </h1>
        <form class="flex flex-col gap-8" @submit.prevent="onSubmit">
          <div
            class="flex border border-dark-primary rounded overflow-hidden bg-white-bg h-12"
          >
            <input
              v-model="username"
              type="text"
              placeholder="Usuario"
              class="w-full pl-3 border-dark-primary"
            />
          </div>
          <p v-if="errors.username" class="text-red-600 text-sm mt-1">
            {{ errors.username }}
          </p>
          <div
            class="flex border border-dark-primary rounded overflow-hidden bg-white-bg"
          >
            <input
              v-model="password"
              :type="showPassword1 ? 'text' : 'password'"
              placeholder="Contraseña"
              class="w-full pl-3 border-dark-primary"
            />
            <button
              type="button"
              class="flex shrink-0 justify-center items-center size-12"
              @click="showPassword1 = !showPassword1"
            >
              <component
                :is="showPassword1 ? EyeOff : Eye"
                class="size-7 text-dark-primary"
              />
            </button>
          </div>
          <p v-if="errors.password" class="text-red-600 text-sm mt-1">
            {{ errors.password }}
          </p>
          <div
            class="flex border border-dark-primary rounded overflow-hidden bg-white-bg"
          >
            <input
              v-model="confirmPassword"
              :type="showPassword2 ? 'text' : 'password'"
              placeholder="Repite la contraseña"
              class="w-full pl-3 border-dark-primary"
            />
            <button
              type="button"
              class="flex shrink-0 justify-center items-center size-12"
              @click="showPassword2 = !showPassword2"
            >
              <component
                :is="showPassword2 ? EyeOff : Eye"
                class="size-7 text-dark-primary"
              />
            </button>
          </div>
          <p v-if="errors.confirmPassword" class="text-red-600 text-sm mt-1">
            {{ errors.confirmPassword }}
          </p>
          <div class="flex items-center place-content-evenly">
            <div class="flex max-md:flex-col">
              <h2>¿Ya tienes una cuenta?</h2>
              <RouterLink :to="{ name: 'login' }" class="ml-1 text-blue-900"
                >Clica aquí</RouterLink
              >
            </div>
            <input
              type="submit"
              value="Registrarse"
              class="bg-blue-secundary border border-dark-primary rounded h-11 text-white-bg font-semibold px-4"
            />
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { User, Lock, Eye, EyeOff } from "lucide-vue-next";
import { RouterLink } from "vue-router";

const showPassword1 = ref(false);
const showPassword2 = ref(false);

const username = ref("");
const password = ref("");
const confirmPassword = ref("");

const errors = ref({
  username: "",
  password: "",
  confirmPassword: "",
});

const validateForm = () => {
  let valid = true;
  errors.value = {
    username: "",
    password: "",
    confirmPassword: "",
  };

  //Username validation
  if (!username.value) {
    errors.value.username = "El nombre de usuario es un campo requerido.";
    valid = false;
  } else if (username.value.length < 5 || username.value.length > 12) {
    errors.value.username =
      "El nombre de usuario debe tener entre 5 y 12 caracteres.";
    valid = false;
  } else if (!/^[a-zA-Z0-9]+$/.test(username.value)) {
    errors.value.username =
      "El nombre de usuario no debe contener espacios ni caracteres especiales.";
    valid = false;
  }

  //Password Validation
  if (!password.value) {
    errors.value.password = "La contraseña es un campo requerido.";
    valid = false;
  } else if (password.value.length < 6) {
    errors.value.password =
      "La contraseña debe contener al menos 6 caracteres.";
    valid = false;
  } else if (!/\d/.test(password.value)) {
    errors.value.password = "La contraseña debe contener al menos un número.";
    valid = false;
  } else if (confirmPassword.value !== password.value) {
    errors.value.confirmPassword = "Las contraseñas deben coincidir.";
    valid = false;
  }
  return valid;
};

const onSubmit = () => {
  if (validateForm()) {
    alert("Formulario válido.");
  }
};
</script>
