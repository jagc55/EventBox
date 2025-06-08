<template>
  <header class="bg-dark-primary text-white relative z-10">
    <div class="h-16 flex items-center justify-between px-4">
      <!-- Logo + texto -->
      <RouterLink to="/" class="flex-shrink-0 flex items-center h-full space-x-2 hover:opacity-70 transition">
        <Logo class="size-16" />
        <span class="text-xl font-semibold tracking-wide">EVENTBOX</span>
      </RouterLink>

      <!-- Botón hamburguesa solo en móvil -->
      <button class="sm:hidden" @click="menuOpen = !menuOpen">☰</button>

      <!-- Enlaces a la derecha (versión escritorio) -->
      <nav class="hidden sm:flex items-center space-x-4">
        <template v-if="!isLoggedIn">
          <RouterLink :to="{ name: 'login' }" class="hover:underline"> Iniciar sesión </RouterLink>
          <hr class="h-5 border-r border-white-bg" />
          <RouterLink :to="{ name: 'register' }" class="hover:underline"> Registrarse </RouterLink>
        </template>
        <template v-else>
          <RouterLink :to="{ name: 'profile' }" class="hover:underline"> Mi perfil </RouterLink>
          <hr class="h-5 border-r border-white-bg" />
          <button @click="logout" class="hover:underline">Cerrar sesión</button>
        </template>
      </nav>
    </div>

    <hr class="h-1 bg-purple-primary border-none" />

    <!-- Menú móvil desplegable -->
    <div v-show="menuOpen" class="w-full bg-dark-primary px-4 py-2 sm:hidden shadow-md flex flex-col gap-2">
      <template v-if="!isLoggedIn">
        <RouterLink :to="{ name: 'login' }" class="block hover:underline"> Iniciar sesión </RouterLink>
        <hr class="w-full border-t border-white-bg" />
        <RouterLink :to="{ name: 'register' }" class="block hover:underline"> Registrarse </RouterLink>
      </template>
      <template v-else>
        <RouterLink :to="{ name: 'profile' }" class="block hover:underline"> Mi perfil </RouterLink>
        <hr class="w-full border-t border-white-bg" />
        <button @click="logout" class="block hover:underline text-left">Cerrar sesión</button>
      </template>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref, onMounted, watchEffect } from "vue";
import { RouterLink, useRouter } from "vue-router";
import Logo from "@/assets/logo.svg";
import { sessionStatus } from "@/stores/session";

const menuOpen = ref(false);
const isLoggedIn = ref(false);
const router = useRouter();

watchEffect(() => {
  isLoggedIn.value = sessionStatus.value || !!localStorage.getItem("token");
});

// Detectar si hay token al cargar
onMounted(() => {
  isLoggedIn.value = !!localStorage.getItem("token");
});

// Actualizar estado si cambia dinámicamente
watchEffect(() => {
  isLoggedIn.value = !!localStorage.getItem("token");
});

// Cerrar sesión
const logout = () => {
  localStorage.removeItem("token");
  sessionStatus.value = false;
  isLoggedIn.value = false;
  router.push({ name: "login" });
};
</script>
