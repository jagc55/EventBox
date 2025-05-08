<template>
  <header class="bg-dark-primary text-white relative z-10">
    <div class="h-16 flex items-center justify-between px-4">
      <!-- Logo + texto -->
      <RouterLink
        to="/"
        class="flex-shrink-0 flex items-center h-full space-x-2 hover:opacity-70 transition"
      >
        <Logo class="size-16" />
        <span class="text-xl font-semibold tracking-wide">EVENTBOX</span>
      </RouterLink>

      <!-- Botón hamburguesa solo en móvil -->
      <button class="sm:hidden" @click="menuOpen = !menuOpen">☰</button>

      <!-- Enlaces a la derecha -->
      <nav class="hidden sm:flex items-center space-x-4">
        <RouterLink :to="{ name: 'login' }" class="hover:underline">
          Iniciar sesión
        </RouterLink>
        <hr class="h-5 border-r border-white-bg" />
        <RouterLink :to="{ name: 'register' }" class="hover:underline">
          Registrarse
        </RouterLink>
      </nav>
    </div>
    <hr class="h-1 bg-blue-header border-none" />

    <!-- Menú desplegable móvil como capa superpuesta -->
    <div
      v-show="menuOpen"
      class="w-full bg-dark-primary px-4 py-2 sm:hidden shadow-md flex flex-col gap-2"
    >
      <RouterLink :to="{ name: 'login' }" class="block hover:underline">
        Iniciar sesión
      </RouterLink>
      <hr class="w-full border-t border-white-bg" />
      <RouterLink :to="{ name: 'register' }" class="block hover:underline">
        Registrarse
      </RouterLink>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { RouterLink } from "vue-router";
import Logo from "@/assets/logo.svg";

const menuOpen = ref(false);
</script>
