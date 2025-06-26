<template>
  <div class="bg-black-secundary pt-[68px] min-h-screen">
    <div class="mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52">
      <!-- Header -->
      <div class="mt-4 p-4 border border-white-bg bg-dark-primary rounded-xl">
        <div class="flex justify-between items-center">
          <h1 class="text-2xl font-bold text-white-bg">Panel de Administración</h1>
          <div class="flex items-center gap-4">
            <span class="text-white-bg"> {{ pendingRequestsCount }} solicitudes pendientes </span>
            <button
              @click="loadRequests"
              class="bg-purple-primary hover:bg-purple-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200"
            >
              Actualizar
            </button>
          </div>
        </div>
      </div>

      <!-- Solicitudes pendientes -->
      <div class="mt-4 p-4 border border-white-bg bg-dark-primary rounded-xl">
        <h2 class="text-xl font-bold text-white-bg mb-4">Solicitudes de Organizador</h2>

        <div v-if="loading" class="text-center py-8">
          <p class="text-white-bg">Cargando solicitudes...</p>
        </div>

        <div v-else-if="requests.length === 0" class="text-center py-8">
          <p class="text-white-bg opacity-70">No hay solicitudes pendientes</p>
        </div>

        <div v-else class="space-y-4">
          <div
            v-for="request in requests"
            :key="request.id"
            class="border border-gray-600 bg-black-secundary p-4 rounded-lg"
          >
            <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
              <div class="flex-1">
                <div class="flex items-center gap-3 mb-2">
                  <h3 class="text-lg font-semibold text-white-bg">
                    {{ request.user.name }}
                  </h3>
                </div>
                <p class="text-white-bg opacity-80">{{ request.user.email }}</p>
                <p class="text-white-bg opacity-60 text-sm">Solicitado: {{ formatDate(request.requestedAt) }}</p>
                <div v-if="request.comments" class="mt-2">
                  <p class="text-white-bg text-sm"><strong>Comentarios:</strong> {{ request.comments }}</p>
                </div>
              </div>

              <div v-if="request.status === 'PENDING'" class="flex flex-col gap-2">
                <button
                  @click="approveRequest(request)"
                  class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200"
                >
                  Aprobar
                </button>
                <button
                  @click="rejectRequest(request)"
                  class="bg-red-primary hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200"
                >
                  Rechazar
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from "vue";
import { useSession } from "@/composables/useSession";

const { session } = useSession();

// Estado
const requests = ref<any[]>([]);
const loading = ref(false);
const processing = ref(false);

// Computed
const pendingRequestsCount = computed(() => requests.value.filter((r) => r.status === "PENDING").length);

// Funciones
const loadRequests = async () => {
  loading.value = true;
  try {
    const response = await fetch("http://localhost:8080/api/organizer-requests/pending", {
      headers: {
        Authorization: `Bearer ${session.value?.token}`,
      },
    });

    if (response.ok) {
      requests.value = await response.json();
    } else {
      console.error("Error loading requests");
    }
  } catch (error) {
    console.error("Error:", error);
  } finally {
    loading.value = false;
  }
};

const approveRequest = async (request: any) => {
  if (!request) return;

  if (!confirm(`¿Estás seguro de aprobar la solicitud de ${request.user.name}?`)) {
    return;
  }

  processing.value = true;
  try {
    const response = await fetch(`http://localhost:8080/api/organizer-requests/${request.id}/approve`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value?.token}`,
      },
      body: JSON.stringify({
        adminId: session.value?.userId,
        comments: "Aprobada por el administrador",
      }),
    });

    if (response.ok) {
      alert("Solicitud aprobada correctamente");

      // Disparar un evento personalizado para notificar el cambio de rol
      window.dispatchEvent(
        new CustomEvent("userRoleUpdated", {
          detail: { userId: request.user.id, newRoleId: 2 },
        })
      );

      loadRequests();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al aprobar solicitud"));
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión");
  } finally {
    processing.value = false;
  }
};

const rejectRequest = async (request: any) => {
  if (!request) return;

  if (!confirm(`¿Estás seguro de rechazar la solicitud de ${request.user.name}?`)) {
    return;
  }

  processing.value = true;
  try {
    const response = await fetch(`http://localhost:8080/api/organizer-requests/${request.id}/reject`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value?.token}`,
      },
      body: JSON.stringify({
        adminId: session.value?.userId,
        comments: "Rechazada por el administrador",
      }),
    });

    if (response.ok) {
      alert("Solicitud rechazada");
      loadRequests();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al rechazar solicitud"));
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión");
  } finally {
    processing.value = false;
  }
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString("es-ES", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

// Cargar solicitudes al montar el componente
onMounted(() => {
  loadRequests();
});
</script>
