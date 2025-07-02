<template>
  <header class="bg-dark-primary text-white fixed top-0 left-0 right-0 z-50">
    <div class="h-16 flex items-center justify-between px-4">
      <RouterLink to="/" class="flex-shrink-0 flex items-center h-full space-x-2 hover:opacity-70 transition">
        <Logo class="size-16" />
        <span class="text-xl font-semibold tracking-wide">EVENTBOX</span>
      </RouterLink>

      <button class="sm:hidden" @click="menuOpen = !menuOpen">☰</button>

      <nav class="hidden sm:flex items-center space-x-4">
        <template v-if="!isLoggedIn">
          <RouterLink :to="{ name: 'login' }" class="hover:underline"> Iniciar sesión </RouterLink>
          <hr class="h-5 border-r border-white-bg" />
          <RouterLink :to="{ name: 'register' }" class="hover:underline"> Registrarse </RouterLink>
        </template>
        <template v-else>
          <RouterLink :to="{ name: 'profile' }" class="hover:underline"> Mi perfil </RouterLink>
          <hr class="h-5 border-r border-white-bg" />
          <RouterLink to="/social" class="hover:underline">Social</RouterLink>
          <hr class="h-5 border-r border-white-bg" />
          <RouterLink v-if="session?.roleId === 2" :to="{ name: 'establishments' }" class="hover:underline">
            Organizador
          </RouterLink>
          <hr v-if="session?.roleId === 2" class="h-5 border-r border-white-bg" />
          <RouterLink v-if="session?.roleId === 3" :to="{ name: 'admin' }" class="hover:underline">
            Administrador
          </RouterLink>
          <hr v-if="session?.roleId === 3" class="h-5 border-r border-white-bg" />
          <button @click="logout" class="hover:underline">Cerrar sesión</button>
        </template>
      </nav>
    </div>

    <hr class="h-1 bg-purple-primary border-none" />

    <div v-show="menuOpen" class="w-full bg-dark-primary px-4 py-2 sm:hidden shadow-md flex flex-col gap-2">
      <template v-if="!isLoggedIn">
        <RouterLink :to="{ name: 'login' }" class="block hover:underline"> Iniciar sesión </RouterLink>
        <hr class="w-full border-t border-white-bg" />
        <RouterLink :to="{ name: 'register' }" class="block hover:underline"> Registrarse </RouterLink>
      </template>
      <template v-else>
        <RouterLink to="/social" class="block hover:underline">Social</RouterLink>
        <hr class="w-full border-t border-white-bg" />
        <RouterLink :to="{ name: 'profile' }" class="block hover:underline"> Mi perfil </RouterLink>
        <hr class="w-full border-t border-white-bg" />
        <RouterLink v-if="session?.roleId === 2" :to="{ name: 'establishments' }" class="block hover:underline">
          Organizador
        </RouterLink>
        <hr v-if="session?.roleId === 2" class="w-full border-t border-white-bg" />
        <RouterLink v-if="session?.roleId === 3" :to="{ name: 'admin' }" class="block hover:underline">
          Administrador
        </RouterLink>
        <hr v-if="session?.roleId === 3" class="w-full border-t border-white-bg" />
        <button @click="logout" class="block hover:underline text-left">Cerrar sesión</button>
      </template>
    </div>
  </header>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { RouterLink, useRouter } from "vue-router";
import Logo from "@/assets/logo.svg";
import { useSession } from "@/composables/useSession";

const menuOpen = ref(false);
const router = useRouter();
const { clearSession, isLoggedIn, session } = useSession();

const logout = () => {
  clearSession();
  router.push({ name: "login" });
};
</script>
