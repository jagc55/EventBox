<template>
  <div class="flex flex-col h-full"><Header /> <RouterView class="grow" /></div>
</template>

<script setup lang="ts">
import Header from "./components/Header.vue";
</script>
