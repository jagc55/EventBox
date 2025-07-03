<template>
  <div class="bg-black-secundary min-h-screen pt-[68px]">
    <div class="mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52">
      <!-- mt-4 debe ir AQUÍ, fuera del card principal, igual que en Admin.vue -->
      <div class="mt-4">
        <div class="border border-white-bg rounded-xl bg-dark-primary shadow-xl overflow-hidden mb-8">
          <!-- Pestañas -->
          <div class="border-b border-white-bg flex">
            <button
              :class="[
                'flex-1 px-4 py-3 text-sm text-white-bg border-r border-white-bg hover:bg-purple-600 transition-colors',
                activeTab === 'users' ? 'bg-purple-primary' : 'hover:bg-purple-primary',
              ]"
              @click="activeTab = 'users'"
            >
              Usuarios
            </button>
            <button
              :class="[
                'flex-1 px-4 py-3 text-sm text-white-bg hover:bg-purple-600 transition-colors',
                activeTab === 'establishments' ? 'bg-purple-primary' : 'hover:bg-purple-primary',
              ]"
              @click="activeTab = 'establishments'"
            >
              Establecimientos
            </button>
          </div>
          <!-- Contenido -->
          <div class="p-6">
            <!-- Usuarios -->
            <div v-if="activeTab === 'users'">
              <div class="mb-4 flex flex-col gap-2">
                <input
                  v-model="searchUser"
                  type="text"
                  placeholder="Buscar organizadores o administradores..."
                  class="w-full px-4 py-2 border border-gray-600 rounded bg-white text-black"
                />
                <div class="p-4 border border-white-bg bg-dark-primary rounded-xl flex flex-wrap gap-2 mt-4">
                  <button
                    @click="userRoleFilter = 'all'"
                    :class="[
                      'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                      userRoleFilter === 'all'
                        ? 'bg-purple-primary text-white'
                        : 'bg-gray-600 text-white hover:bg-gray-700',
                    ]"
                  >
                    Todos ({{ filteredUsersAll.length }})
                  </button>
                  <button
                    @click="userRoleFilter = 'organizers'"
                    :class="[
                      'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                      userRoleFilter === 'organizers'
                        ? 'bg-purple-primary text-white'
                        : 'bg-gray-600 text-white hover:bg-gray-700',
                    ]"
                  >
                    Organizadores ({{ filteredUsersOrganizers.length }})
                  </button>
                  <button
                    @click="userRoleFilter = 'admins'"
                    :class="[
                      'px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                      userRoleFilter === 'admins'
                        ? 'bg-purple-primary text-white'
                        : 'bg-gray-600 text-white hover:bg-gray-700',
                    ]"
                  >
                    Administradores ({{ filteredUsersAdmins.length }})
                  </button>
                </div>
              </div>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4 max-h-[calc(100vh-300px)] overflow-y-auto pb-8">
                <div
                  v-for="user in filteredUsers"
                  :key="user.id"
                  class="bg-dark-primary border border-white-bg rounded shadow-xl flex flex-col gap-2 transition-colors duration-300 hover:border-purple-primary cursor-pointer p-4"
                >
                  <div class="flex justify-between items-start">
                    <div>
                      <div class="text-lg font-bold text-white-bg">{{ user.name }}</div>
                      <div class="text-sm text-gray-300 mt-1">
                        {{ establishments.filter((e) => e.user?.id === user.id).length }} establecimiento{{
                          establishments.filter((e) => e.user?.id === user.id).length === 1 ? "" : "s"
                        }}
                      </div>
                    </div>
                    <button
                      @click.stop="toggleFollow(user.id)"
                      :class="[
                        isFollowingUser(user.id)
                          ? 'bg-gray-500 hover:bg-gray-600'
                          : 'bg-purple-primary hover:bg-purple-600',
                        'text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                      ]"
                    >
                      {{ isFollowingUser(user.id) ? "Dejar de seguir" : "Seguir" }}
                    </button>
                  </div>
                  <div class="flex gap-6 mt-2 text-sm text-white-bg opacity-80">
                    <span>Seguidores: {{ user.followersCount ?? 0 }}</span>
                    <span>Siguiendo: {{ user.followingCount ?? 0 }}</span>
                  </div>
                </div>
              </div>
              <div v-if="filteredUsers.length === 0" class="text-center text-gray-400 mt-8">
                No se encontraron usuarios
              </div>
            </div>
            <!-- Establecimientos -->
            <div v-else>
              <div class="mb-4 flex flex-col sm:flex-row gap-2">
                <input
                  v-model="searchEstablishment"
                  type="text"
                  placeholder="Buscar establecimientos..."
                  class="w-full px-4 py-2 border border-gray-600 rounded bg-white text-black"
                />
              </div>
              <div class="grid grid-cols-1 md:grid-cols-2 gap-4 max-h-[calc(100vh-300px)] overflow-y-auto pb-8">
                <div
                  v-for="est in filteredEstablishments"
                  :key="est.id"
                  class="bg-dark-primary border border-white-bg rounded shadow-xl flex flex-col transition-colors duration-300 hover:border-purple-primary cursor-pointer p-4"
                >
                  <div class="flex justify-between items-start mb-1">
                    <span class="text-xl font-bold text-white-bg">{{ est.name }}</span>
                    <button
                      @click.stop="toggleFollowEstablishment(est.id)"
                      :class="[
                        isFollowingEstablishment(est.id)
                          ? 'bg-gray-500 hover:bg-gray-600'
                          : 'bg-purple-primary hover:bg-purple-600',
                        'text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200',
                      ]"
                    >
                      {{ isFollowingEstablishment(est.id) ? "Dejar de seguir" : "Seguir" }}
                    </button>
                  </div>
                  <div class="text-white-bg opacity-80 mb-1">{{ est.address || "No especificada" }}</div>
                  <div class="text-white-bg opacity-80 mb-1">
                    {{ est.city ? est.city + "," : "" }} {{ est.province || "" }}
                  </div>
                  <div class="text-white-bg opacity-80 mb-1">
                    {{ establishmentStats[est.id]?.eventsCount ?? 0 }} evento{{
                      (establishmentStats[est.id]?.eventsCount ?? 0) === 1 ? "" : "s"
                    }}
                    organizados
                  </div>
                  <div class="text-white-bg opacity-80 mb-1">
                    Seguidores: {{ establishmentStats[est.id]?.followersCount ?? 0 }}
                  </div>
                  <div class="text-white-bg opacity-80 mb-1">
                    <strong>Organizador:</strong> {{ est.user?.name || "Desconocido" }}
                  </div>
                  <div class="text-white-bg opacity-80 mb-1">
                    <strong>Teléfono:</strong> {{ est.phone || "No especificado" }}
                  </div>
                  <div class="text-white-bg opacity-80 mb-1">
                    <strong>Email:</strong> {{ est.email || "No especificado" }}
                  </div>
                </div>
              </div>
              <div v-if="filteredEstablishments.length === 0" class="text-center text-gray-400 mt-8">
                No se encontraron establecimientos
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted, watch } from "vue";
import { ApiService } from "@/services/api";
import { useSession } from "@/composables/useSession";

const users = ref<any[]>([]);
const establishments = ref<any[]>([]);
const following = ref<number[]>([]); // IDs de usuarios seguidos
const followingEstablishments = ref<number[]>([]); // IDs de establecimientos seguidos
const activeTab = ref<"users" | "establishments">("users");
const searchUser = ref("");
const searchEstablishment = ref("");
const userRoleFilter = ref<"all" | "organizers" | "admins">("all");
const establishmentStats = ref<Record<number, { followersCount: number; eventsCount: number }>>({});

const { session } = useSession();

const filteredUsersAll = computed(() =>
  users.value.filter(
    (u) =>
      (u.role?.id === 2 || u.role?.id === 3) &&
      (u.name.toLowerCase().includes(searchUser.value.toLowerCase()) ||
        u.email.toLowerCase().includes(searchUser.value.toLowerCase()))
  )
);
const filteredUsersOrganizers = computed(() =>
  users.value.filter(
    (u) =>
      u.role?.id === 2 &&
      (u.name.toLowerCase().includes(searchUser.value.toLowerCase()) ||
        u.email.toLowerCase().includes(searchUser.value.toLowerCase()))
  )
);
const filteredUsersAdmins = computed(() =>
  users.value.filter(
    (u) =>
      u.role?.id === 3 &&
      (u.name.toLowerCase().includes(searchUser.value.toLowerCase()) ||
        u.email.toLowerCase().includes(searchUser.value.toLowerCase()))
  )
);
const filteredUsers = computed(() => {
  if (userRoleFilter.value === "all") return filteredUsersAll.value;
  if (userRoleFilter.value === "organizers") return filteredUsersOrganizers.value;
  if (userRoleFilter.value === "admins") return filteredUsersAdmins.value;
  return [];
});
const filteredEstablishments = computed(() =>
  establishments.value.filter(
    (e) =>
      (e.name.toLowerCase().includes(searchEstablishment.value.toLowerCase()) ||
        (e.user?.name && e.user.name.toLowerCase().includes(searchEstablishment.value.toLowerCase()))) &&
      e.user?.id !== session.value.userId
  )
);

async function fetchEstablishmentStats(estId: number) {
  // Seguidores
  let followersCount = 0;
  try {
    const res = await fetch(`http://144.24.204.95:8080/api/establishment-follows/establishment/${estId}/count`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("session") || ""}` },
    });
    if (res.ok) {
      const data = await res.json();
      followersCount = data.followersCount ?? 0;
    }
  } catch {}
  // Eventos
  let eventsCount = 0;
  try {
    const res = await fetch(`http://144.24.204.95:8080/api/events`, {
      headers: { Authorization: `Bearer ${localStorage.getItem("session") || ""}` },
    });
    if (res.ok) {
      const allEvents = await res.json();
      eventsCount = Array.isArray(allEvents) ? allEvents.filter((ev) => ev.establishment?.id === estId).length : 0;
    }
  } catch {}
  establishmentStats.value[estId] = { followersCount, eventsCount };
}

onMounted(async () => {
  // Obtener usuarios (excluyendo rol 1 en el filtrado)
  const fetchedUsers = await ApiService.getAllUsers();
  users.value = Array.isArray(fetchedUsers)
    ? fetchedUsers.filter((u) => (u.role?.id === 2 || u.role?.id === 3) && u.id !== session.value.userId)
    : [];
  // Obtener establecimientos
  const fetchedEstablishments = await ApiService.getAllEstablishments();
  establishments.value = Array.isArray(fetchedEstablishments) ? fetchedEstablishments : [];
  // Obtener usuarios seguidos
  if (session.value.userId) {
    try {
      const seguidos = await ApiService.getFollowing(session.value.userId);
      following.value = seguidos.map((u: any) => u.id);
    } catch {}
  }
  // Obtener establecimientos seguidos
  if (session.value.userId) {
    try {
      const estSeguidos = await ApiService.getFollowedEstablishments(session.value.userId);
      followingEstablishments.value = estSeguidos.map((e: any) => e.id);
    } catch {}
  }
  // Cargar stats de todos los establecimientos visibles
  for (const est of establishments.value) {
    fetchEstablishmentStats(est.id);
  }
});

watch(filteredEstablishments, (newEsts) => {
  for (const est of newEsts) {
    if (!establishmentStats.value[est.id]) fetchEstablishmentStats(est.id);
  }
});

function isFollowingUser(userId: number) {
  return following.value.includes(userId);
}

async function toggleFollow(userId: number) {
  if (!session.value.userId) return;
  const userIndex = users.value.findIndex((u) => u.id === userId);
  if (isFollowingUser(userId)) {
    await ApiService.unfollowUser(session.value.userId, userId);
    following.value = following.value.filter((id) => id !== userId);
    // Actualizar contador de seguidores localmente
    if (userIndex !== -1 && users.value[userIndex].followersCount !== undefined) {
      users.value[userIndex].followersCount = Math.max(0, users.value[userIndex].followersCount - 1);
    }
  } else {
    await ApiService.followUser(session.value.userId, userId);
    following.value.push(userId);
    // Actualizar contador de seguidores localmente
    if (userIndex !== -1 && users.value[userIndex].followersCount !== undefined) {
      users.value[userIndex].followersCount += 1;
    }
  }
}

function isFollowingEstablishment(estId: number) {
  return followingEstablishments.value.includes(estId);
}

async function toggleFollowEstablishment(estId: number) {
  if (!session.value.userId) return;
  if (isFollowingEstablishment(estId)) {
    await ApiService.unfollowEstablishment(session.value.userId, estId);
    followingEstablishments.value = followingEstablishments.value.filter((id) => id !== estId);
  } else {
    await ApiService.followEstablishment(session.value.userId, estId);
    followingEstablishments.value.push(estId);
  }
  await fetchEstablishmentStats(estId); // Actualiza solo el establecimiento afectado
}
</script>
