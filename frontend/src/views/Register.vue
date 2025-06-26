<template>
  <div class="h-full bg-cover bg-[center_10%] scale-105 bg-[url(@/assets/images/bg.png)]">
    <div class="flex items-center justify-center size-full bg-black/30">
      <div class="bg-dark-primary rounded-lg shadow-xl w-full max-w-lg p-10 border border-white-bg">
        <h1 class="text-2xl font-bold text-white-bg mb-4">Registrar usuario</h1>
        <form class="flex flex-col gap-8" @submit.prevent="onSubmit">
          <!-- Email -->
          <div class="flex flex-col">
            <div class="flex border border-dark-primary rounded overflow-hidden bg-white-bg h-12">
              <input
                v-model="email"
                type="email"
                placeholder="Correo electrónico"
                class="w-full pl-3 border-dark-primary"
              />
            </div>
            <p v-if="errors.email" class="text-red-600 text-sm mt-1 h-[1.25rem]">
              {{ errors.email }}
            </p>
          </div>

          <!-- User -->
          <div class="flex flex-col">
            <div class="flex border border-dark-primary rounded overflow-hidden bg-white-bg h-12">
              <input v-model="username" type="text" placeholder="Usuario" class="w-full pl-3 border-dark-primary" />
            </div>
            <p v-if="errors.username" class="text-red-600 text-sm mt-1 h-[1.25rem]">
              {{ errors.username }}
            </p>
          </div>

          <!-- Password -->
          <div class="flex flex-col">
            <div class="flex border border-dark-primary rounded overflow-hidden bg-white-bg">
              <input
                v-model="password"
                :type="showPassword1 ? 'text' : 'password'"
                placeholder="Contraseña"
                class="w-full pl-3 border-dark-primary"
              />
              <button
                type="button"
                class="flex shrink-0 justify-center items-center size-12"
                @click="showPassword1 = !showPassword1"
              >
                <component :is="showPassword1 ? EyeOff : Eye" class="size-7 text-dark-primary" />
              </button>
            </div>
            <p v-if="errors.password" class="text-red-600 text-sm mt-1 h-[1.25rem]">
              {{ errors.password }}
            </p>
          </div>

          <!-- Confirm Password -->
          <div class="flex flex-col">
            <div class="flex border border-dark-primary rounded overflow-hidden bg-white-bg">
              <input
                v-model="confirmPassword"
                :type="showPassword2 ? 'text' : 'password'"
                placeholder="Repite la contraseña"
                class="w-full pl-3 border-dark-primary"
              />
              <button
                type="button"
                class="flex shrink-0 justify-center items-center size-12"
                @click="showPassword2 = !showPassword2"
              >
                <component :is="showPassword2 ? EyeOff : Eye" class="size-7 text-dark-primary" />
              </button>
            </div>
            <p v-if="errors.confirmPassword" class="text-red-600 text-sm mt-1 h-[1.25rem]">
              {{ errors.confirmPassword }}
            </p>
          </div>

          <!-- Link to login -->
          <div class="flex items-center place-content-evenly">
            <div class="flex max-md:flex-col">
              <h2 class="text-white-bg">¿Ya tienes una cuenta?</h2>
              <RouterLink
                :to="{ name: 'login' }"
                class="ml-1 text-blue-400 underline hover:text-blue-600 hover:underline decoration-2 transition-colors duration-200"
              >
                Clica aquí
              </RouterLink>
            </div>
            <!--Button-->
            <input
              type="submit"
              value="Registrarse"
              class="bg-purple-primary hover:bg-purple-600 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg cursor-pointer"
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
import { Eye, EyeOff } from "lucide-vue-next";
import { useSession } from "@/composables/useSession";

const router = useRouter();
const { setSession } = useSession();

const email = ref("");
const username = ref("");
const password = ref("");
const confirmPassword = ref("");
const showPassword1 = ref(false);
const showPassword2 = ref(false);

const errors = ref({
  email: "",
  username: "",
  password: "",
  confirmPassword: "",
});

const validateForm = () => {
  let valid = true;
  errors.value = { email: "", username: "", password: "", confirmPassword: "" };

  if (!email.value) {
    errors.value.email = "El correo electrónico es obligatorio.";
    valid = false;
  } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.value)) {
    errors.value.email = "Introduce un correo electrónico válido.";
    valid = false;
  }

  if (!username.value) {
    errors.value.username = "El nombre de usuario es un campo requerido.";
    valid = false;
  } else if (username.value.length < 5 || username.value.length > 12) {
    errors.value.username = "Debe tener entre 5 y 12 caracteres.";
    valid = false;
  } else if (!/^[a-zA-Z0-9]+$/.test(username.value)) {
    errors.value.username = "No puede tener espacios ni símbolos.";
    valid = false;
  }

  if (!password.value) {
    errors.value.password = "La contraseña es un campo requerido.";
    valid = false;
  } else if (password.value.length < 6) {
    errors.value.password = "Debe tener al menos 6 caracteres.";
    valid = false;
  } else if (!/\d/.test(password.value)) {
    errors.value.password = "Debe contener al menos un número.";
    valid = false;
  }

  if (confirmPassword.value !== password.value) {
    errors.value.confirmPassword = "Las contraseñas deben coincidir.";
    valid = false;
  }

  return valid;
};

const checkUserExists = async () => {
  try {
    let hasErrors = false;

    // Verificar si el email ya existe
    const emailRes = await fetch(`http://localhost:8080/api/auth/check-email?email=${encodeURIComponent(email.value)}`);

    if (emailRes.ok) {
      const emailData = await emailRes.json();
      if (emailData.exists) {
        errors.value.email = "Este correo electrónico ya está registrado.";
        hasErrors = true;
      }
    }

    // Verificar si el nombre de usuario ya existe
    const usernameRes = await fetch(
      `http://localhost:8080/api/auth/check-username?username=${encodeURIComponent(username.value)}`
    );

    if (usernameRes.ok) {
      const usernameData = await usernameRes.json();
      if (usernameData.exists) {
        errors.value.username = "Este nombre de usuario ya está en uso.";
        hasErrors = true;
      }
    }

    return !hasErrors;
  } catch (error) {
    console.error("Error al verificar usuario:", error);
    alert("Error de conexión al verificar usuario. Por favor, inténtalo de nuevo.");
    return false;
  }
};

const onSubmit = async () => {
  // Limpiar errores previos
  errors.value = { email: "", username: "", password: "", confirmPassword: "" };

  if (!validateForm()) return;

  // Verificar si el usuario ya existe antes de registrar
  const userExists = await checkUserExists();
  if (!userExists) {
    // Los errores ya están establecidos en checkUserExists(), simplemente retornamos
    return;
  }

  try {
    const res = await fetch("http://localhost:8080/api/auth/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        email: email.value,
        name: username.value,
        password: password.value,
        roleId: 1,
      }),
    });

    const data = await res.json();

    if (!res.ok) {
      // Manejar errores específicos del servidor como backup
      if (data.message && data.message.includes("email")) {
        errors.value.email = "Este correo electrónico ya está registrado.";
      } else if (
        (data.message && data.message.includes("usuario")) ||
        (data.message && data.message.includes("username"))
      ) {
        errors.value.username = "Este nombre de usuario ya está en uso.";
      } else {
        alert("Error: " + (data.message || "Error al registrar el usuario"));
      }
      return;
    }

    setSession({
      token: data.token,
      userId: data.userId,
      roleId: data.roleId,
      email: data.email,
      name: data.name,
    });

    alert("Registro exitoso");
    router.push({ name: "events" });
  } catch (err: any) {
    alert("Error: " + err.message);
  }
};
</script>
