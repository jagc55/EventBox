<template>
  <div class="bg-black-secundary">
    <div class="mt-4 px-4 py-2 border border-white-bg bg-dark-primary">
      <h1 class="text-2xl font-bold text-white-bg">Datos</h1>

      <div class="w-full flex flex-col items-center">
        <svg viewBox="0 0 32 32" class="w-40 h-40 rotate-[-90deg] rounded-full">
          <circle
            v-for="(slice, index) in slices"
            :key="index"
            r="16"
            cx="16"
            cy="16"
            fill="transparent"
            stroke-width="32"
            :stroke="colors[index % colors.length]"
            :stroke-dasharray="slice.dash"
            :stroke-dashoffset="slice.offset"
          />
        </svg>
        <ul class="mt-4 text-sm space-y-1">
          <li
            v-for="(label, index) in labels"
            :key="index"
            class="flex items-center gap-2"
          >
            <span
              class="w-3 h-3 inline-block rounded-sm"
              :style="{ backgroundColor: colors[index % colors.length] }"
            ></span>
            {{ label }}: {{ ejemplo[label] }}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from "vue";

const ejemplo: Record<string, number> = {
  Música: 6,
  Cine: 7,
  Deportes: 4,
  Tetas: 3,
  Xotas: 6,
  Tetorras: 10,
};

const labels = computed(() => Object.keys(ejemplo));
const total = computed(() =>
  Object.values(ejemplo).reduce((sum, val) => sum + val, 0)
);

const slices = computed(() => {
  let offset = 0;
  return labels.value.map((label) => {
    const value = ejemplo[label];
    const percentage = value / total.value;
    const dash = percentage * 100;
    const slice = {
      dash: `${dash} ${100 - dash}`,
      offset: offset,
    };
    offset -= dash;
    return slice;
  });
});

const colors = [
  "#3a005f",
  "#602c8d",
  "#000742",
  "#e91e63",
  "#4caf50",
  "#03a9f4",
  "#ff9800",
];
</script>
