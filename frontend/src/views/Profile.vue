<template>
  <div class="bg-black-secundary pt-[68px]">
    <div
      class="mt-4 p-4 sm:py-2 border border-white-bg bg-dark-primary mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 rounded-xl"
    >
      <p class="text-2xl font-bold text-white-bg">Datos</p>
      <div class="flex flex-col sm:flex-row">
        <div class="flex flex-col">
          <p class="text-lg font-bold text-white-bg">{{ email }}</p>
          <p class="text-lg font-bold text-white-bg">{{ name }}</p>
          <p class="text-lg font-bold text-white-bg mb-2">
            Rol de {{ roleId === 1 ? "Usuario" : roleId === 2 ? "Organizador" : "Administrador" }}
          </p>
        </div>

        <div class="flex flex-col mx-0 sm:ml-auto gap-3">
          <button
            v-if="roleId === 1"
            @click="requestOrganizer"
            class="bg-purple-primary hover:bg-purple-600 text-white-bg px-3 py-1.5 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg text-sm"
          >
            Solicitar organizador
          </button>
          <button
            v-else
            @click="goToEstablishments"
            class="bg-purple-primary hover:bg-purple-600 text-white-bg px-3 py-1.5 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg text-sm"
          >
            Mis establecimientos
          </button>

          <button
            class="bg-purple-primary hover:bg-purple-600 text-white-bg px-3 py-1.5 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg text-sm"
          >
            Cambiar contraseña
          </button>
        </div>
      </div>
    </div>

    <div
      class="mt-4 px-4 py-2 border border-white-bg bg-dark-primary mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 mb-4 rounded-xl"
    >
      <div class="flex justify-between items-baseline mb-4">
        <p class="text-2xl font-bold text-white-bg">Tus estadísticas</p>

        <!-- Botón para alternar gráficos -->
        <button
          v-if="!isLoadingFavorites && !favoritesLoading && !isLoadingEventTypes"
          @click="showBarChart = !showBarChart"
          class="bg-purple-primary hover:bg-purple-600 text-white-bg px-3 py-1.5 rounded-lg transition-colors duration-200 flex items-center gap-2 text-sm"
        >
          <svg v-if="showBarChart" class="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <!-- Icono circular -->
            <circle cx="12" cy="12" r="10" />
          </svg>
          <svg v-else class="w-4 h-4" fill="currentColor" viewBox="0 0 20 20">
            <!-- Icono de barras -->
            <rect x="3" y="12" width="3" height="5" />
            <rect x="7" y="8" width="3" height="9" />
            <rect x="11" y="6" width="3" height="11" />
            <rect x="15" y="10" width="3" height="7" />
          </svg>
          {{ showBarChart ? "Circular" : "Barras" }}
        </button>
      </div>

      <!-- Mensaje de carga -->
      <div v-if="isLoadingFavorites || favoritesLoading || isLoadingEventTypes" class="text-center py-8">
        <p class="text-white-bg text-lg">Cargando estadísticas...</p>
      </div>

      <!-- Contenido de estadísticas -->
      <div v-else class="w-full flex flex-col md:flex-row justify-center items-center md:items-start gap-8">
        <!-- Gráfico circular -->
        <div v-if="!showBarChart" class="flex justify-center">
          <svg viewBox="0 0 32 32" class="size-48 md:size-56 -rotate-90 rounded-full shrink-0">
            <!-- Si no hay favoritos, mostrar un círculo gris completo -->
            <circle
              v-if="totalFavorites === 0"
              r="16"
              cx="16"
              cy="16"
              fill="transparent"
              stroke-width="32"
              stroke="#374151"
              stroke-dasharray="100 0"
            />
            <!-- Si hay favoritos, mostrar el gráfico normal -->
            <circle
              v-else
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
        </div>

        <!-- Gráfico de barras -->
        <div v-else class="flex justify-center">
          <div class="size-48 md:size-56 p-3 overflow-hidden">
            <div v-if="totalFavorites === 0" class="w-full h-full flex items-center justify-center">
              <div class="text-center text-white-bg opacity-60">
                <div class="w-full h-4 bg-gray-600 rounded mb-2"></div>
                <div class="w-3/4 h-4 bg-gray-600 rounded mb-2 mx-auto"></div>
                <div class="w-1/2 h-4 bg-gray-600 rounded mx-auto"></div>
                <p class="mt-4 text-sm">Sin eventos favoritos</p>
              </div>
            </div>
            <div v-else class="w-full h-full flex items-end justify-center gap-2 pb-6">
              <div
                v-for="(category, index) in barChartData"
                :key="category.name"
                class="flex flex-col items-center gap-1 flex-1 min-w-0"
                :style="{ maxWidth: `${Math.max(100 / Object.keys(userCategories).length, 8)}%` }"
              >
                <div
                  class="w-full rounded-t transition-all duration-500 ease-in-out"
                  :style="{
                    height: `${category.percentage}%`,
                    backgroundColor: category.color,
                    minHeight: category.count > 0 ? '12px' : '0px',
                  }"
                ></div>
                <span class="text-xs text-white-bg text-center transform -rotate-45 origin-center whitespace-nowrap">
                  {{ category.name.slice(0, 4) }}
                </span>
                <span class="text-xs text-white-bg">{{ category.count }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- Leyenda (común para ambos gráficos) -->
        <ul class="ml-0 sm:ml-10 grid grid-rows-5 grid-flow-col gap-x-6 gap-y-2 text-lg text-white-bg">
          <li v-for="label in Object.keys(userCategories)" :key="label" class="flex items-center gap-2">
            <span class="size-3 rounded-sm" :style="{ backgroundColor: getColorForCategory(label) }" />
            {{ `${label.charAt(0).toUpperCase() + label.slice(1)}: ${userCategories[label]}` }}
          </li>
        </ul>
      </div>

      <!-- Primera fila de estadísticas -->
      <div class="flex flex-col md:flex-row items-center justify-center gap-14 my-4 sm:my-8">
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">{{ totalFavorites }}</span>
          <span>Eventos favoritos</span>
        </div>
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-4xl">{{ favoriteEventType }}</span>
          <span>Tipo de evento favorito</span>
        </div>
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">{{ favoriteEventTypePercentage }}</span>
          <span>Porcentaje de categoría favorita</span>
        </div>
      </div>

      <!-- Segunda fila de estadísticas -->
      <div class="flex flex-col md:flex-row items-center justify-center gap-14 my-4 sm:my-8">
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">{{ pastEventsCount }}</span>
          <span>Eventos pasados</span>
        </div>
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">{{ recentEventsCount }}</span>
          <span>Eventos en los últimos 3 meses</span>
        </div>
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">{{ futureEventsCount }}</span>
          <span>Eventos futuros</span>
        </div>
        <div class="flex flex-col gap-2 text-center text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-5xl">{{ upcomingEventsCount }}</span>
          <span>Eventos en los próximos 3 meses</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { useSession } from "@/composables/useSession";
import { useFavorites } from "@/composables/useFavorites";
import { ApiService } from "@/services/api";

const router = useRouter();
const { session, updateRole } = useSession();

// Datos de favoritos y tipos de eventos
const favoritesData = ref<any[]>([]);
const eventTypes = ref<any[]>([]);
const isLoadingFavorites = ref(false);
const isLoadingEventTypes = ref(false);

// Estado para alternar entre gráficos
const showBarChart = ref(false);

// Usar el composable de favoritos
const { favorites, loadFavorites, loading: favoritesLoading } = useFavorites();

// Cargar datos de favoritos
const loadUserFavorites = async () => {
  if (!session.value?.userId) {
    return;
  }

  isLoadingFavorites.value = true;
  try {
    await loadFavorites(session.value.userId);
    favoritesData.value = favorites.value;
  } catch (error) {
    console.error("Error loading favorites:", error);
  } finally {
    isLoadingFavorites.value = false;
  }
};

// Cargar tipos de eventos desde la base de datos
const loadEventTypes = async () => {
  isLoadingEventTypes.value = true;
  try {
    const types = await ApiService.getAllEventTypes();
    eventTypes.value = types;
  } catch (error) {
    console.error("Error loading event types:", error);
  } finally {
    isLoadingEventTypes.value = false;
  }
};

// Cargar favoritos y tipos de eventos al montar el componente
onMounted(async () => {
  await Promise.all([loadUserFavorites(), loadEventTypes()]);
});

// Computed para estadísticas basadas en favoritos reales
const userCategories = computed(() => {
  // Si no se han cargado los tipos de eventos aún, retornar objeto vacío
  if (eventTypes.value.length === 0) {
    return {};
  }

  // Inicializar todas las categorías con 0 basándose en los tipos de eventos de la BD
  const categories: Record<string, number> = {};

  eventTypes.value.forEach((eventType) => {
    const normalizedName = eventType.name.toLowerCase();
    categories[normalizedName] = 0;
  });

  // Contar los eventos favoritos por categoría
  favoritesData.value.forEach((favorite) => {
    if (favorite && favorite.event && favorite.event.eventType) {
      const eventType = favorite.event.eventType.name.toLowerCase();

      // Verificar si la categoría existe en nuestro objeto
      if (eventType in categories) {
        categories[eventType] = categories[eventType] + 1;
      } else {
        // Si el tipo no está en las categorías cargadas, crear una entrada "otros" si no existe
        if (!categories.otros) {
          categories.otros = 0;
        }
        categories.otros = categories.otros + 1;
      }
    }
  });

  return categories;
});

// Número total de favoritos
const totalFavorites = computed(() => favoritesData.value.length);

// Tipo de evento favorito (el que más aparece)
const favoriteEventType = computed(() => {
  if (!favoritesData.value.length) return "Ninguno";

  const categories = userCategories.value;
  const sortedCategories = Object.entries(categories).sort((a, b) => b[1] - a[1]);

  return sortedCategories.length > 0
    ? sortedCategories[0][0].charAt(0).toUpperCase() + sortedCategories[0][0].slice(1)
    : "Ninguno";
});

// Eventos que han ocurrido en los últimos 3 meses (solo eventos pasados)
const recentEventsCount = computed(() => {
  if (!favoritesData.value.length) return 0;

  const threeMonthsAgo = new Date();
  threeMonthsAgo.setMonth(threeMonthsAgo.getMonth() - 3);
  threeMonthsAgo.setHours(0, 0, 0, 0);

  const now = new Date(); // Usar fecha y hora actual para comparación exacta

  return favoritesData.value.filter((favorite) => {
    const eventDate = new Date(`${favorite.event.date}T${favorite.event.time}`);
    // Solo eventos que ya pasaron Y están dentro de los últimos 3 meses
    return eventDate >= threeMonthsAgo && eventDate < now;
  }).length;
});

// Porcentaje por tipo de evento favorito
const favoriteEventTypePercentage = computed(() => {
  if (!favoritesData.value.length) return "0%";

  const categories = userCategories.value;
  const sortedCategories = Object.entries(categories).sort((a, b) => b[1] - a[1]);

  if (sortedCategories.length === 0 || sortedCategories[0][1] === 0) return "0%";

  const favoriteCount = sortedCategories[0][1];
  const percentage = Math.round((favoriteCount / favoritesData.value.length) * 100);
  return `${percentage}%`;
});

// Eventos pasados (anteriores a ahora)
const pastEventsCount = computed(() => {
  if (!favoritesData.value.length) return 0;

  const now = new Date(); // Usar fecha y hora actual para comparación exacta

  return favoritesData.value.filter((favorite) => {
    const eventDate = new Date(`${favorite.event.date}T${favorite.event.time}`);
    return eventDate < now;
  }).length;
});

// Eventos futuros (a partir de ahora)
const futureEventsCount = computed(() => {
  if (!favoritesData.value.length) return 0;

  const now = new Date(); // Usar fecha y hora actual para comparación exacta

  return favoritesData.value.filter((favorite) => {
    const eventDate = new Date(`${favorite.event.date}T${favorite.event.time}`);
    return eventDate >= now;
  }).length;
});

// Eventos en los próximos 3 meses (solo eventos futuros)
const upcomingEventsCount = computed(() => {
  if (!favoritesData.value.length) return 0;

  const now = new Date(); // Usar fecha y hora actual para comparación exacta
  const threeMonthsLater = new Date();
  threeMonthsLater.setMonth(threeMonthsLater.getMonth() + 3);

  return favoritesData.value.filter((favorite) => {
    const eventDate = new Date(`${favorite.event.date}T${favorite.event.time}`);
    // Solo eventos que aún no han pasado Y están dentro de los próximos 3 meses
    return eventDate >= now && eventDate <= threeMonthsLater;
  }).length;
});

// ✅ Computed para usar en el template de forma segura y reactiva
const roleId = computed(() => session.value?.roleId || 1);
const name = computed(() => session.value?.name || "");
const email = computed(() => session.value?.email || "");

const requestOrganizer = async () => {
  if (!session.value?.userId) return;

  try {
    const res = await fetch(`http://localhost:8080/api/users/${session.value.userId}/role`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value.token}`,
      },
      body: JSON.stringify({ roleId: 2 }),
    });

    if (!res.ok) throw new Error("Error updating role");

    alert("Rol organizador concedido");
    updateRole(2);
  } catch (err: any) {
    alert("Error: " + err.message);
  }
};

const goToEstablishments = () => {
  router.push({ name: "establishments" });
};

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
  otros: "#6B7280",
};

// Función para generar colores dinámicamente si no están predefinidos
const getColorForCategory = (categoryName: string): string => {
  if (categoryColors[categoryName]) {
    return categoryColors[categoryName];
  }

  // Generar un color basado en el hash del nombre de la categoría
  let hash = 0;
  for (let i = 0; i < categoryName.length; i++) {
    hash = categoryName.charCodeAt(i) + ((hash << 5) - hash);
  }

  const hue = Math.abs(hash) % 360;
  return `hsl(${hue}, 70%, 50%)`;
};

const slices = computed(() => {
  const categories = userCategories.value;
  const total = Object.values(categories).reduce((sum, val) => sum + val, 0);

  // Si no hay favoritos, no generar slices (se mostrará el círculo gris)
  if (total === 0) {
    return [];
  }

  let offset = 0;

  return Object.keys(categories)
    .filter((label) => categories[label] > 0) // Solo mostrar categorías con valores > 0 en el gráfico
    .map((label) => {
      const dash = (categories[label] / total) * 100;
      const slice = {
        colour: getColorForCategory(label),
        dash: `${dash} ${100 - dash}`,
        offset,
      };

      offset -= dash;
      return slice;
    });
});

// Computed para datos del gráfico de barras
const barChartData = computed(() => {
  const categories = userCategories.value;
  const maxValue = Math.max(...Object.values(categories));

  return Object.keys(categories).map((label) => {
    const count = categories[label];
    // Calculamos el porcentaje y lo escalamos al 85% para un mejor equilibrio visual
    const rawPercentage = maxValue > 0 ? (count / maxValue) * 100 : 0;
    const percentage = (rawPercentage * 85) / 100; // Escalar al 85%

    return {
      name: label.charAt(0).toUpperCase() + label.slice(1),
      count,
      percentage,
      color: getColorForCategory(label),
    };
  });
});
</script>
