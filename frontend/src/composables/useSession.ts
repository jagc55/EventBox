import { ref, computed } from "vue";

const session = ref({
  token: "",
  userId: null as number | null,
  roleId: null as number | null,
  email: "",
  name: "",
});

// Establece los datos de sesión y guarda en localStorage
const setSession = (data: { token: string; userId: number; roleId: number; email: string; name: string }) => {
  session.value = data;
  localStorage.setItem("session", JSON.stringify(session.value));
};

// Limpia los datos de sesión y localStorage
const clearSession = () => {
  session.value = {
    token: "",
    userId: null,
    roleId: null,
    email: "",
    name: "",
  };
  localStorage.removeItem("session");
};

// Carga los datos desde localStorage al iniciar la app
const loadSessionFromStorage = () => {
  const stored = localStorage.getItem("session");
  if (stored) {
    try {
      session.value = JSON.parse(stored);
    } catch {
      clearSession();
    }
  }
};

// Computed para saber si hay una sesión activa
const isLoggedIn = computed(() => !!session.value.token);

// Permite actualizar solo el rol
const updateRole = (newRoleId: number) => {
  session.value.roleId = newRoleId;
  localStorage.setItem("session", JSON.stringify(session.value));
};

// Refresca la información del usuario desde el servidor
const refreshSession = async () => {
  if (!session.value.token) return false;

  try {
    const response = await fetch("http://localhost:8080/api/auth/refresh", {
      headers: {
        Authorization: `Bearer ${session.value.token}`,
      },
    });

    if (response.ok) {
      const data = await response.json();
      session.value = {
        ...session.value,
        userId: data.userId,
        roleId: data.roleId,
        email: data.email,
        name: data.name,
      };
      localStorage.setItem("session", JSON.stringify(session.value));
      return true;
    } else {
      clearSession();
      return false;
    }
  } catch (error) {
    console.error("Error refreshing session:", error);
    return false;
  }
};

export function useSession() {
  return {
    session,
    setSession,
    clearSession,
    loadSessionFromStorage,
    updateRole,
    refreshSession,
    isLoggedIn,
  };
}
