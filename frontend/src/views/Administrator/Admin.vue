<template>
  <div class="bg-black-secundary pt-[68px] min-h-screen">
    <div class="mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52">
      <!-- Tabs Navigation -->
      <div class="mt-4 border border-white-bg bg-dark-primary rounded-xl overflow-hidden">
        <div
          class="flex sm:flex-row flex-nowrap sm:overflow-visible overflow-x-auto scrollbar-thin scrollbar-thumb-gray-700 scrollbar-track-transparent"
        >
          <button
            @click="currentTab = 'requests'"
            :class="[
              'flex-shrink-0 sm:flex-1 px-6 py-3 font-medium transition-colors duration-200',
              currentTab === 'requests' ? 'bg-purple-primary text-white' : 'text-white-bg hover:bg-gray-700',
            ]"
            style="min-width: 140px"
          >
            Solicitudes
          </button>
          <button
            @click="currentTab = 'users'"
            :class="[
              'flex-shrink-0 sm:flex-1 px-6 py-3 font-medium transition-colors duration-200',
              currentTab === 'users' ? 'bg-purple-primary text-white' : 'text-white-bg hover:bg-gray-700',
            ]"
            style="min-width: 140px"
          >
            Usuarios
          </button>
          <button
            @click="currentTab = 'establishments'"
            :class="[
              'flex-shrink-0 sm:flex-1 px-6 py-3 font-medium transition-colors duration-200',
              currentTab === 'establishments' ? 'bg-purple-primary text-white' : 'text-white-bg hover:bg-gray-700',
            ]"
            style="min-width: 140px"
          >
            Establecimientos
          </button>
          <button
            @click="currentTab = 'events'"
            :class="[
              'flex-shrink-0 sm:flex-1 px-6 py-3 font-medium transition-colors duration-200',
              currentTab === 'events' ? 'bg-purple-primary text-white' : 'text-white-bg hover:bg-gray-700',
            ]"
            style="min-width: 140px"
          >
            Eventos
          </button>
        </div>
      </div>

      <!-- Header -->
      <div class="mt-4 p-4 border border-white-bg bg-dark-primary rounded-xl">
        <div class="flex flex-col sm:flex-row sm:justify-between sm:items-center gap-2">
          <h1 class="text-2xl font-bold text-white-bg">Panel de Administración</h1>
          <div class="flex flex-col sm:flex-row sm:items-center gap-2 sm:gap-4 w-full sm:w-auto">
            <div class="flex-1 flex justify-between sm:justify-end items-center gap-2 sm:gap-4">
              <span v-if="currentTab === 'requests'" class="text-white-bg">
                {{ pendingRequestsCount }} solicitudes pendientes
              </span>
              <span v-else-if="currentTab === 'users'" class="text-white-bg">
                {{ totalUsers }} usuarios registrados
              </span>
              <span v-else-if="currentTab === 'establishments'" class="text-white-bg">
                {{ totalEstablishments }} establecimientos registrados
              </span>
              <span v-else-if="currentTab === 'events'" class="text-white-bg">
                {{ totalEvents }} eventos registrados
              </span>
              <button
                @click="refreshCurrentTab"
                class="bg-purple-primary hover:bg-purple-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200"
              >
                Actualizar
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- Tab Content -->
      <div class="mt-4">
        <!-- Solicitudes Tab -->
        <div v-if="currentTab === 'requests'" class="p-4 border border-white-bg bg-dark-primary rounded-xl">
          <h2 class="text-xl font-bold text-white-bg mb-4">Solicitudes de Organizador</h2>

          <div v-if="loading" class="text-center py-8">
            <p class="text-white-bg">Cargando solicitudes...</p>
          </div>

          <div v-else-if="requests.length === 0" class="text-center py-8">
            <p class="text-white-bg opacity-70">No hay solicitudes pendientes</p>
          </div>

          <div v-else class="max-h-[calc(100vh-420px)] overflow-y-auto space-y-3 pr-2">
            <div v-for="request in requests" :key="request.id" class="border border-white bg-white p-4 rounded-lg">
              <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div class="flex-1">
                  <div class="flex items-center gap-3">
                    <h3 class="text-lg font-semibold text-black">
                      {{ request.user.name }}
                    </h3>
                  </div>
                  <p class="text-black opacity-80">{{ request.user.email }}</p>
                  <p class="text-black opacity-60 text-sm">Solicitado: {{ formatDate(request.requestedAt) }}</p>
                  <div v-if="request.comments" class="mt-2">
                    <p class="text-black text-sm"><strong>Comentarios:</strong> {{ request.comments }}</p>
                  </div>
                </div>

                <div v-if="request.status === 'PENDING'" class="flex flex-col gap-2">
                  <button
                    @click="approveRequest(request)"
                    :disabled="processing"
                    class="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 disabled:opacity-50"
                  >
                    Aprobar
                  </button>
                  <button
                    @click="rejectRequest(request)"
                    :disabled="processing"
                    class="bg-red-primary hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 disabled:opacity-50"
                  >
                    Rechazar
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Usuarios Tab -->
        <div v-else-if="currentTab === 'users'" class="space-y-4">
          <!-- Filter buttons -->
          <div class="p-4 border border-white-bg bg-dark-primary rounded-xl">
            <div class="flex flex-wrap gap-2">
              <button
                @click="userFilter = 'all'"
                :class="[
                  'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                  userFilter === 'all' ? 'bg-purple-primary text-white' : 'bg-gray-600 text-white hover:bg-gray-700',
                ]"
              >
                Todos ({{ allUsers.length }})
              </button>
              <button
                @click="userFilter = 'users'"
                :class="[
                  'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                  userFilter === 'users' ? 'bg-purple-primary text-white' : 'bg-gray-600 text-white hover:bg-gray-700',
                ]"
              >
                Usuarios ({{ filteredUsers.users.length }})
              </button>
              <button
                @click="userFilter = 'organizers'"
                :class="[
                  'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                  userFilter === 'organizers'
                    ? 'bg-purple-primary text-white'
                    : 'bg-gray-600 text-white hover:bg-gray-700',
                ]"
              >
                Organizadores ({{ filteredUsers.organizers.length }})
              </button>
              <button
                @click="userFilter = 'admins'"
                :class="[
                  'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                  userFilter === 'admins' ? 'bg-purple-primary text-white' : 'bg-gray-600 text-white hover:bg-gray-700',
                ]"
              >
                Administradores ({{ filteredUsers.admins.length }})
              </button>
            </div>
          </div>

          <!-- Users list -->
          <div class="p-4 border border-white-bg bg-dark-primary rounded-xl">
            <h2 class="text-xl font-bold text-white-bg mb-4">
              Gestión de Usuarios
              <span v-if="userFilter !== 'all'" class="text-lg font-normal">
                -
                {{
                  userFilter === "users"
                    ? "Usuarios"
                    : userFilter === "organizers"
                    ? "Organizadores"
                    : "Administradores"
                }}
              </span>
            </h2>

            <div v-if="loadingUsers" class="text-center py-8">
              <p class="text-white-bg">Cargando usuarios...</p>
            </div>

            <div v-else-if="displayedUsers.length === 0" class="text-center py-8">
              <p class="text-white-bg opacity-70">No hay usuarios en esta categoría</p>
            </div>

            <div v-else class="max-h-[calc(100vh-436px)] overflow-y-auto space-y-3 pr-2">
              <div v-for="user in displayedUsers" :key="user.id" class="border border-white bg-white p-4 rounded-lg">
                <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                  <div class="flex-1">
                    <div class="flex items-center gap-3">
                      <h3 class="text-lg font-semibold text-black">{{ user.name }}</h3>
                      <span
                        :class="[
                          'px-2 py-1 rounded text-xs font-medium',
                          user.role.id === 1
                            ? 'bg-blue-100 text-blue-800'
                            : user.role.id === 2
                            ? 'bg-green-100 text-green-800'
                            : 'bg-purple-100 text-purple-800',
                        ]"
                      >
                        {{ user.role.id === 1 ? "Usuario" : user.role.id === 2 ? "Organizador" : "Administrador" }}
                      </span>
                    </div>
                    <p class="text-black opacity-80">{{ user.email }}</p>
                  </div>

                  <div class="flex flex-col sm:flex-row gap-2">
                    <!-- Demote organizer button -->
                    <button
                      v-if="user.role.id === 2"
                      @click="demoteOrganizer(user)"
                      :disabled="processing"
                      class="bg-orange-500 hover:bg-orange-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200 disabled:opacity-50 text-sm"
                    >
                      Quitar rol organizador
                    </button>

                    <!-- Delete user button (no self-delete and no delete other admins) -->
                    <button
                      v-if="user.id !== session?.userId && user.role.id !== 3"
                      @click="deleteUser(user)"
                      :disabled="processing"
                      class="bg-red-primary hover:bg-red-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200 disabled:opacity-50 text-sm"
                    >
                      Eliminar usuario
                    </button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Establecimientos Tab -->
        <div v-else-if="currentTab === 'establishments'" class="p-4 border border-white-bg bg-dark-primary rounded-xl">
          <h2 class="text-xl font-bold text-white-bg mb-4">Gestión de Establecimientos</h2>
          <div v-if="loadingEstablishments" class="text-center py-8">
            <p class="text-white-bg">Cargando establecimientos...</p>
          </div>
          <div v-else-if="allEstablishments.length === 0" class="text-center py-8 text-white-bg opacity-70">
            No hay establecimientos registrados
          </div>
          <div v-else class="max-h-[calc(100vh-340px)] overflow-y-auto space-y-3 pr-2">
            <div
              v-for="establishment in allEstablishments"
              :key="establishment.id"
              class="border border-white bg-white p-4 rounded-lg"
            >
              <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div class="flex-1">
                  <div class="flex items-center gap-3">
                    <h3 class="text-lg font-semibold text-black">{{ establishment.name }}</h3>
                  </div>
                  <p class="text-black opacity-80">
                    <strong>Dirección:</strong> {{ establishment.address || "No especificada" }}
                  </p>
                  <p class="text-black opacity-80">
                    <strong>Organizador:</strong> {{ establishment.user?.name || "Desconocido" }}
                  </p>
                  <p class="text-black opacity-80">
                    <strong>Teléfono:</strong> {{ establishment.phone || "No especificado" }}
                  </p>
                  <p class="text-black opacity-80">
                    <strong>Email:</strong> {{ establishment.email || "No especificado" }}
                  </p>
                </div>
                <div class="flex flex-col gap-2">
                  <button
                    @click="deleteEstablishment(establishment)"
                    :disabled="processing"
                    class="bg-red-primary hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 disabled:opacity-50"
                  >
                    Eliminar establecimiento
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- Eventos Tab -->
        <div v-else-if="currentTab === 'events'" class="p-4 border border-white-bg bg-dark-primary rounded-xl">
          <h2 class="text-xl font-bold text-white-bg mb-4">Gestión de Eventos</h2>

          <div v-if="loadingEvents" class="text-center py-8">
            <p class="text-white-bg">Cargando eventos...</p>
          </div>

          <div v-else-if="allEvents.length === 0" class="text-center py-8">
            <p class="text-white-bg opacity-70">No hay eventos registrados</p>
          </div>

          <div v-else class="max-h-[calc(100vh-340px)] overflow-y-auto space-y-3 pr-2">
            <div v-for="event in allEvents" :key="event.id" class="border border-white bg-white p-4 rounded-lg">
              <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div class="flex-1">
                  <div class="flex items-center gap-3">
                    <h3 class="text-lg font-semibold text-black">{{ event.title }}</h3>
                    <span class="px-2 py-1 bg-blue-100 text-blue-800 rounded text-xs font-medium">
                      {{ event.eventType?.name || "Sin categoría" }}
                    </span>
                  </div>
                  <p class="text-black opacity-80">
                    <strong>Organizador:</strong> {{ event.user?.name || "Desconocido" }}
                  </p>
                  <p class="text-black opacity-80">
                    <strong>Establecimiento:</strong> {{ event.establishment?.name || "Sin establecimiento" }}
                  </p>
                  <p class="text-black opacity-60 text-sm">
                    <strong>Fecha:</strong> {{ formatEventDate(event.date, event.time) }}
                  </p>
                  <p class="text-black opacity-60 text-sm">
                    <strong>Ubicación:</strong> {{ event.establishment?.address || "No especificada" }}
                  </p>
                </div>

                <div class="flex flex-col gap-2">
                  <button
                    @click="deleteEvent(event)"
                    :disabled="processing"
                    class="bg-red-primary hover:bg-red-600 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 disabled:opacity-50"
                  >
                    Eliminar evento
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed, watch } from "vue";
import { useSession } from "@/composables/useSession";

const { session } = useSession();

// Estado general
const currentTab = ref<"requests" | "users" | "events" | "establishments">("requests");
const processing = ref(false);

// Estado de solicitudes
const requests = ref<any[]>([]);
const loading = ref(false);

// Estado de usuarios
const allUsers = ref<any[]>([]);
const loadingUsers = ref(false);
const userFilter = ref<"all" | "users" | "organizers" | "admins">("all");

// Estado de eventos
const allEvents = ref<any[]>([]);
const loadingEvents = ref(false);

// Estado de establecimientos
const allEstablishments = ref<any[]>([]);
const loadingEstablishments = ref(false);

// Computed properties
const pendingRequestsCount = computed(() => requests.value.filter((r) => r.status === "PENDING").length);

const totalUsers = computed(() => allUsers.value.length);
const totalEvents = computed(() => allEvents.value.length);
const totalEstablishments = computed(() => allEstablishments.value.length);

const filteredUsers = computed(() => {
  return {
    users: allUsers.value.filter((user) => user.role.id === 1),
    organizers: allUsers.value.filter((user) => user.role.id === 2),
    admins: allUsers.value.filter((user) => user.role.id === 3),
  };
});

const displayedUsers = computed(() => {
  if (userFilter.value === "all") return allUsers.value;
  if (userFilter.value === "users") return filteredUsers.value.users;
  if (userFilter.value === "organizers") return filteredUsers.value.organizers;
  if (userFilter.value === "admins") return filteredUsers.value.admins;
  return [];
});

// Watch for tab changes to load data
watch(currentTab, (newTab) => {
  if (newTab === "requests" && requests.value.length === 0) {
    loadRequests();
  } else if (newTab === "users" && allUsers.value.length === 0) {
    loadUsers();
  } else if (newTab === "events" && allEvents.value.length === 0) {
    loadEvents();
  } else if (newTab === "establishments" && allEstablishments.value.length === 0) {
    loadEstablishments();
  }
});

// Funciones de solicitudes
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

// Funciones de usuarios
const loadUsers = async () => {
  loadingUsers.value = true;
  try {
    const response = await fetch("http://localhost:8080/api/users", {
      headers: {
        Authorization: `Bearer ${session.value?.token}`,
      },
    });

    if (response.ok) {
      allUsers.value = await response.json();
    } else {
      console.error("Error loading users");
      alert("Error al cargar usuarios");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión al cargar usuarios");
  } finally {
    loadingUsers.value = false;
  }
};

const deleteUser = async (user: any) => {
  if (!user) return;

  const confirmMessage = `¿Estás seguro de eliminar al ${user.role.id === 2 ? "organizador" : "usuario"} ${user.name}?
${user.role.id === 2 ? "\nEsto eliminará todos sus establecimientos y eventos." : ""}
Esta acción no se puede deshacer.`;

  if (!confirm(confirmMessage)) {
    return;
  }

  processing.value = true;
  try {
    const response = await fetch(`http://localhost:8080/api/users/${user.id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${session.value?.token}`,
      },
    });

    if (response.ok) {
      alert(`${user.role.id === 2 ? "Organizador" : "Usuario"} eliminado correctamente`);
      loadUsers();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al eliminar usuario"));
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión");
  } finally {
    processing.value = false;
  }
};

const demoteOrganizer = async (user: any) => {
  if (!user || user.role.id !== 2) return;

  const confirmMessage = `¿Estás seguro de quitar el rol de organizador a ${user.name}?
Esto eliminará todos sus establecimientos y eventos.
Esta acción no se puede deshacer.`;

  if (!confirm(confirmMessage)) {
    return;
  }

  processing.value = true;
  try {
    const response = await fetch(`http://localhost:8080/api/users/${user.id}/demote`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value?.token}`,
      },
    });

    if (response.ok) {
      alert("Rol de organizador removido correctamente");
      loadUsers();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al quitar rol de organizador"));
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión");
  } finally {
    processing.value = false;
  }
};

// Funciones de eventos
const loadEvents = async () => {
  loadingEvents.value = true;
  try {
    const response = await fetch("http://localhost:8080/api/events", {
      headers: {
        Authorization: `Bearer ${session.value?.token}`,
      },
    });

    if (response.ok) {
      allEvents.value = await response.json();
    } else {
      console.error("Error loading events");
      alert("Error al cargar eventos");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión al cargar eventos");
  } finally {
    loadingEvents.value = false;
  }
};

const deleteEvent = async (event: any) => {
  if (!event) return;

  if (!confirm(`¿Estás seguro de eliminar el evento "${event.title}"?\nEsta acción no se puede deshacer.`)) {
    return;
  }

  processing.value = true;
  try {
    const response = await fetch(`http://localhost:8080/api/events/${event.id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${session.value?.token}`,
      },
    });

    if (response.ok) {
      alert("Evento eliminado correctamente");
      loadEvents();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al eliminar evento"));
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión");
  } finally {
    processing.value = false;
  }
};

// Funciones de establecimientos
const loadEstablishments = async () => {
  loadingEstablishments.value = true;
  try {
    const response = await fetch("http://localhost:8080/api/establishments", {
      headers: {
        Authorization: `Bearer ${session.value?.token}`,
      },
    });
    if (response.ok) {
      allEstablishments.value = await response.json();
    } else {
      console.error("Error loading establishments");
      alert("Error al cargar establecimientos");
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión al cargar establecimientos");
  } finally {
    loadingEstablishments.value = false;
  }
};

const deleteEstablishment = async (establishment: any) => {
  if (!establishment) return;
  if (
    !confirm(`¿Estás seguro de eliminar el establecimiento "${establishment.name}"?\nEsta acción no se puede deshacer.`)
  ) {
    return;
  }
  processing.value = true;
  try {
    const response = await fetch(`http://localhost:8080/api/establishments/${establishment.id}`, {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${session.value?.token}`,
      },
    });
    if (response.ok) {
      alert("Establecimiento eliminado correctamente");
      loadEstablishments();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al eliminar establecimiento"));
    }
  } catch (error) {
    console.error("Error:", error);
    alert("Error de conexión");
  } finally {
    processing.value = false;
  }
};

// Función para refrescar la pestaña actual
const refreshCurrentTab = () => {
  if (currentTab.value === "requests") {
    loadRequests();
  } else if (currentTab.value === "users") {
    loadUsers();
  } else if (currentTab.value === "events") {
    loadEvents();
  } else if (currentTab.value === "establishments") {
    loadEstablishments();
  }
};

// Funciones de utilidad
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString("es-ES", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

const formatEventDate = (date: string, time: string) => {
  const eventDate = new Date(`${date}T${time}`);
  return eventDate.toLocaleDateString("es-ES", {
    year: "numeric",
    month: "long",
    day: "numeric",
    hour: "2-digit",
    minute: "2-digit",
  });
};

// Cargar datos iniciales
onMounted(() => {
  loadRequests();
});
</script>
