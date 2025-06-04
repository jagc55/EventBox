<template>
  <div class="bg-black-secundary">
    <div
      class="mt-4 p-4 sm:py-2 border border-white-bg bg-dark-primary mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 rounded-xl"
    >
      <p class="text-2xl font-bold text-white-bg">Datos</p>
      <div class="flex flex-col sm:flex-row">
        <div class="flex flex-col">
          <p class="text-lg font-bold text-white-bg">marsupilami@gmail.com</p>
          <p class="text-lg font-bold text-white-bg">marsupilami32</p>
          <p class="text-lg font-bold text-white-bg mb-2">Organizador</p>
        </div>

        <div class="flex flex-col mx-0 sm:ml-auto gap-3">
          <!--Cambiar texto si rol organizador-->
          <button class="bg-purple-primary text-white-bg px-2 rounded">Solicitar organizador</button>
          <button class="bg-purple-primary text-white-bg px-2 rounded">Cambiar contraseña</button>
        </div>
      </div>
    </div>

    <div
      class="mt-4 px-4 py-2 border border-white-bg bg-dark-primary mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 mb-4 rounded-xl"
    >
      <p class="text-2xl font-bold text-white-bg mb-4">Tus estadísticas</p>
      <div class="w-full flex flex-col md:flex-row justify-center items-center md:items-start gap-8">
        <svg viewBox="0 0 32 32" class="size-40 md:size-48 -rotate-90 rounded-full shrink-0">
          <circle
            v-for="(slice, index) in slices"
            :key="index"
            r="16"
            cx="16"
            cy="16"
            fill="transparent"
            stroke-width="32"
            :stroke="slice.colour"
            :stroke-dasharray="slice.dash"
            :stroke-dashoffset="slice.offset"
          />
        </svg>
        <ul class="ml-0 sm:ml-10 grid grid-rows-5 grid-flow-col gap-x-6 gap-y-2 text-lg text-white-bg">
          <li v-for="label in Object.keys(userCategories)" :key="label" class="flex items-center gap-2">
            <span
              class="size-3 rounded-sm"
              :style="{
                backgroundColor: categoryColors[label],
              }"
            />
            {{ `${label}: ${userCategories[label]}` }}
          </li>
        </ul>
      </div>
      <div class="flex flex-col md:flex-row items-center justify-center gap-14 my-4 sm:my-14">
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">5</span> <span>Eventos favoritos</span>
        </div>
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-4xl">Tecnológico</span> <span>Tipo de evento favorito</span>
        </div>
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">5</span> <span>Eventos en los ultimos tres meses</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

const categoryColors: Record<string, string> = {
  cultural: "#9C27B0",
  social: "#3F51B5",
  tecnológico: "#00BCD4",
  deportivo: "#4CAF50",
  religioso: "#8BC34A",
  educativo: "#F44336",
  salud: "#E91E63",
  empresarial: "#FF9800",
  entretenimiento: "#FFC107",
  otros: "#9E9E9E",
};

const userCategories: Record<string, number> = {
  cultural: 5,
  social: 3,
  tecnológico: 8,
  deportivo: 5,
  religioso: 1,
  educativo: 1,
  salud: 2,
  empresarial: 5,
  entretenimiento: 6,
  otros: 5,
};

const slices = computed(() => {
  const total = Object.values(userCategories).reduce((sum, val) => sum + val, 0);
  let offset = 0;

  return Object.keys(userCategories).map((label) => {
    const dash = (userCategories[label] / total) * 100;
    const slice = {
      colour: categoryColors[label],
      dash: `${dash} ${100 - dash}`,
      offset,
    };

    offset -= dash;

    return slice;
  });
});
</script>
