<template>
  <div
    class="h-full bg-cover bg-[center_10%] scale-105 bg-[url(@/assets/images/bg.png)]"
  >
    <div class="flex items-center justify-center size-full bg-black/30">
      <!-- backdrop-blur-sm-->
      <div class="bg-blue-primary rounded-lg shadow-xl w-full max-w-lg p-10">
        <h2 class="text-2xl font-bold text-dark-primary mb-4">
          Iniciar sesión
        </h2>
        <form class="flex flex-col gap-5">
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
              type="password"
              placeholder="Contraseña"
              class="w-full pl-3 border-l border-dark-primary"
            />
            <div class="flex shrink-0 justify-center items-center size-12">
              <Eye class="size-7 text-dark-primary" />
            </div>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { User, Lock, Eye, EyeOff } from "lucide-vue-next";
</script>
