<template>
  <div class="relative w-screen h-screen overflow-hidden">
    <!-- Blur -->
    <div
      class="absolute inset-0 bg-cover bg-[center_10%] scale-110"
      :style="{ backgroundImage: `url(${bg})` }"
    ></div>

    <!-- Capa oscura suave -->
    <div class="fixed inset-0 bg-black/40"></div>

    <!-- Contenido centrado -->
    <div class="relative z-10 flex items-center justify-center h-full">
      <!-- Cuadro del formulario -->
      <div class="bg-mainBlue/100 p-8 rounded-lg shadow-lg w-full max-w-lg">
        <!-- Aquí va tu formulario -->
        <div>
          <h2 class="text-2xl font-semibold text-center text-gray-800 mb-4">
            Iniciar sesión
          </h2>
          <!-- Ejemplo de campos -->
          <form>
            <input
              type="email"
              placeholder="Correo electrónico"
              class="w-full mb-4 p-3 rounded border border-gray-300"
            />
            <input
              type="password"
              placeholder="Contraseña"
              class="w-full mb-4 p-3 rounded border border-gray-300"
            />
            <button
              type="submit"
              class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-700"
            >
              Entrar
            </button>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import bg from "@/assets/images/bg2.png";
</script>
