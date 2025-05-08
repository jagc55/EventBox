<template>
  <header class="bg-darknav text-white relative z-10">
    <div class="h-16 flex items-center justify-between px-4">
      <!-- Logo + texto -->
      <router-link
        to="/"
        class="flex-shrink-0 flex items-center h-full space-x-2 hover:opacity-80 transition"
      >
        <Logo class="w-[64px] h-[64px]" />
        <span class="text-xl font-semibold tracking-wide">EVENTBOX</span>
      </router-link>

      <!-- Botón hamburguesa solo en móvil -->
      <button class="sm:hidden" @click="menuOpen = !menuOpen">☰</button>

      <!-- Enlaces a la derecha -->
      <nav class="hidden sm:flex items-center space-x-4">
        <router-link to="/login" class="hover:underline">
          Iniciar sesión
        </router-link>
        <div class="h-5 border-r border-white"></div>
        <router-link to="/register" class="hover:underline">
          Registrarse
        </router-link>
      </nav>
    </div>

    <div v-if="menuOpen" class="sm:hidden px-4 pb-3 space-y-2">
      <a href="#" class="block hover:underline">Iniciar sesión</a>
      <a href="#" class="block hover:underline">Registrarse</a>
    </div>
    <!-- Barra inferior -->
    <div class="h-1 bg-blue-300"></div>
  </header>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { RouterLink } from "vue-router";
import Logo from "@/assets/logo.svg";

const menuOpen = ref(false);
</script>
