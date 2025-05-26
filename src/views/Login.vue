<template>
  <div
    class="h-full bg-cover bg-[center_10%] scale-105 bg-[url(@/assets/images/bg.png)]"
  >
    <div class="flex items-center justify-center size-full bg-black/30">
      <!-- backdrop-blur-sm-->
      <div class="bg-blue-primary rounded-lg shadow-xl w-full max-w-lg p-10">
        <h1 class="text-2xl font-bold text-dark-primary mb-4">
          Iniciar sesión
        </h1>
        <form class="flex flex-col gap-8">
          <div
            class="flex border border-dark-primary rounded overflow-hidden bg-white-bg"
          >
            <div class="flex shrink-0 justify-center items-center size-12">
              <User class="size-9 text-dark-primary" />
            </div>
            <input
              type="text"
              placeholder="Usuario"
              class="w-full pl-3 border-l border-dark-primary"
            />
          </div>
          <div
            class="flex border border-dark-primary rounded overflow-hidden bg-white-bg"
          >
            <div class="flex shrink-0 justify-center items-center size-12">
              <Lock class="size-7 text-dark-primary" />
            </div>
            <input
              :type="showPassword ? 'text' : 'password'"
              placeholder="Contraseña"
              class="w-full pl-3 border-l border-dark-primary"
            />
            <button
              type="button"
              class="flex shrink-0 justify-center items-center size-12"
              @click="showPassword = !showPassword"
            >
              <component
                :is="showPassword ? EyeOff : Eye"
                class="size-7 text-dark-primary"
              />
            </button>
          </div>
          <div class="flex items-center place-content-evenly">
            <div class="flex max-md:flex-col">
              <h2>¿No tienes una cuenta?</h2>
              <RouterLink :to="{ name: 'register' }" class="ml-1 text-blue-900"
                >Clica aquí</RouterLink
              >
            </div>
            <input
              type="submit"
              value="Acceder"
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

const showPassword = ref(false);
</script>
