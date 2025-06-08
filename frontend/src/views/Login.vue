<template>
  <div class="h-full bg-cover bg-[center_10%] scale-105 bg-[url(@/assets/images/bg.png)]">
    <div class="flex items-center justify-center size-full bg-black/30">
      <div class="bg-dark-primary rounded-lg shadow-xl w-full max-w-lg p-10 border border-white-bg">
        <h1 class="text-2xl font-bold text-white-bg mb-4">Iniciar sesión</h1>
        <form class="flex flex-col gap-8" @submit.prevent="loginUser">
          <div class="flex rounded overflow-hidden bg-white-bg">
            <div class="flex shrink-0 justify-center items-center size-12">
              <AtSign class="size-7 text-dark-primary" />
            </div>
            <input v-model="email" type="text" placeholder="Email" class="w-full pl-3 border-l border-dark-primary" />
          </div>
          <div class="flex border border-dark-primary rounded overflow-hidden bg-white-bg">
            <div class="flex shrink-0 justify-center items-center size-12">
              <Lock class="size-7 text-dark-primary" />
            </div>
            <input
              v-model="password"
              :type="showPassword ? 'text' : 'password'"
              placeholder="Contraseña"
              class="w-full pl-3 border-l border-dark-primary"
            />
            <button
              type="button"
              class="flex shrink-0 justify-center items-center size-12"
              @click="showPassword = !showPassword"
            >
              <component :is="showPassword ? EyeOff : Eye" class="size-7 text-dark-primary" />
            </button>
          </div>
          <div class="flex items-center place-content-evenly">
            <div class="flex max-md:flex-col">
              <h2 class="text-white-bg">¿No tienes una cuenta?</h2>
              <RouterLink
                :to="{ name: 'register' }"
                class="ml-1 text-blue-400 underline hover:text-blue-600 hover:underline decoration-2 transition-colors duration-200"
              >
                Clica aquí
              </RouterLink>
            </div>
            <input
              type="submit"
              value="Acceder"
              class="bg-purple-primary border border-dark-primary rounded h-11 text-white font-semibold px-4 shadow-md"
            />
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";
import { AtSign, Lock, Eye, EyeOff } from "lucide-vue-next";
import { useSession } from "@/composables/useSession";

const router = useRouter();
const { setSession } = useSession();

const showPassword = ref(false);
const email = ref("");
const password = ref("");

const loginUser = async () => {
  try {
    const response = await fetch("http://localhost:8080/api/auth/login", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: email.value, password: password.value }),
    });

    const data = await response.json();

    if (!response.ok) throw new Error(data.message || "Error al iniciar sesión");

    setSession({
      token: data.token,
      userId: data.userId,
      roleId: data.roleId,
      email: data.email,
      name: data.name,
    });

    alert("Inicio de sesión exitoso");
    router.push({ name: "events" });
  } catch (err: any) {
    alert("Error: " + err.message);
  }
};
</script>
