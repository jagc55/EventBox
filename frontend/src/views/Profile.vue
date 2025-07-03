<template>
  <div class="bg-black-secundary pt-[68px]">
    <div
      class="mt-6 p-6 border border-white-bg bg-dark-primary mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 rounded-2xl shadow-2xl"
    >
      <div class="flex items-center gap-3 mb-6">
        <div class="w-2 h-8 bg-gradient-to-b from-purple-primary to-purple-600 rounded-full"></div>
        <h2 class="text-2xl font-bold text-white-bg">Información Personal</h2>
      </div>

      <div class="space-y-6">
        <!-- Información del usuario en horizontal -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-purple-primary rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-white-bg" fill="currentColor" viewBox="0 0 20 20">
                <path d="M2.003 5.884L10 9.882l7.997-3.998A2 2 0 0016 4H4a2 2 0 00-1.997 1.884z"></path>
                <path d="M18 8.118l-8 4-8-4V14a2 2 0 002 2h12a2 2 0 002-2V8.118z"></path>
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-400">Email</p>
              <p class="text-lg font-semibold text-white-bg">{{ email }}</p>
            </div>
          </div>

          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-purple-primary rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-white-bg" fill="currentColor" viewBox="0 0 20 20">
                <path
                  fill-rule="evenodd"
                  d="M10 9a3 3 0 100-6 3 3 0 000 6zm-7 9a7 7 0 1114 0H3z"
                  clip-rule="evenodd"
                ></path>
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-400">Usuario</p>
              <p class="text-lg font-semibold text-white-bg">{{ name }}</p>
            </div>
          </div>

          <div class="flex items-center gap-4">
            <div class="w-12 h-12 bg-purple-primary rounded-lg flex items-center justify-center">
              <svg class="w-6 h-6 text-white-bg" fill="currentColor" viewBox="0 0 20 20">
                <path
                  fill-rule="evenodd"
                  d="M6 6V5a3 3 0 013-3h2a3 3 0 013 3v1h2a2 2 0 012 2v6a2 2 0 01-2 2H4a2 2 0 01-2-2V8a2 2 0 012-2h2zm2-1a1 1 0 011-1h2a1 1 0 011 1v1H8V5z"
                  clip-rule="evenodd"
                ></path>
              </svg>
            </div>
            <div>
              <p class="text-sm text-gray-400">Rol</p>
              <p class="text-lg font-semibold text-white-bg">
                {{ roleId === 1 ? "Usuario" : roleId === 2 ? "Organizador" : "Administrador" }}
              </p>
            </div>
          </div>
        </div>

        <!-- Seguidores, Siguiendo y Botones de acción -->
        <div>
          <h3 class="text-sm text-gray-400 mb-4">Social</h3>

          <!-- Container principal con justificación entre elementos -->
          <div class="flex flex-col lg:flex-row lg:justify-between lg:items-end gap-6">
            <!-- Lado izquierdo: Seguidores, Siguiendo y Establecimientos -->
            <div class="space-y-4">
              <!-- Primera fila: Seguidores y Siguiendo -->
              <div class="flex gap-6">
                <!-- Solo mostrar seguidores si el usuario es organizador o administrador -->
                <button
                  v-if="roleId && roleId >= 2"
                  @click="openFollowersModal"
                  class="group flex items-center gap-4 text-white-bg hover:text-purple-300 transition-all duration-200"
                >
                  <div
                    class="w-12 h-12 bg-purple-primary group-hover:bg-purple-600 rounded-lg flex items-center justify-center transition-colors duration-200"
                  >
                    <svg class="w-6 h-6 text-white-bg" fill="currentColor" viewBox="0 0 20 20">
                      <path
                        d="M13 6a3 3 0 11-6 0 3 3 0 016 0zM18 8a2 2 0 11-4 0 2 2 0 014 0zM14 15a4 4 0 00-8 0v3h8v-3z"
                      ></path>
                    </svg>
                  </div>
                  <div class="text-left">
                    <p class="text-xl font-bold">{{ followersCount }}</p>
                    <p class="text-sm opacity-80 group-hover:underline">Seguidores</p>
                  </div>
                </button>

                <button
                  @click="openFollowingModal"
                  class="group flex items-center gap-4 text-white-bg hover:text-purple-300 transition-all duration-200"
                >
                  <div
                    class="w-12 h-12 bg-purple-primary group-hover:bg-purple-600 rounded-lg flex items-center justify-center transition-colors duration-200"
                  >
                    <svg class="w-6 h-6 text-white-bg" fill="currentColor" viewBox="0 0 20 20">
                      <path
                        d="M8 9a3 3 0 100-6 3 3 0 000 6zM8 11a6 6 0 016 6H2a6 6 0 016-6zM16 7a1 1 0 10-2 0v1h-1a1 1 0 100 2h1v1a1 1 0 102 0v-1h1a1 1 0 100-2h-1V7z"
                      ></path>
                    </svg>
                  </div>
                  <div class="text-left">
                    <p class="text-xl font-bold">{{ followingCount }}</p>
                    <p class="text-sm opacity-80 group-hover:underline">Siguiendo</p>
                  </div>
                </button>
              </div>

              <!-- Segunda fila: Solo Establecimientos seguidos -->
              <div>
                <button
                  @click="openEstablishmentsModal"
                  class="group flex items-center gap-4 text-white-bg hover:text-purple-300 transition-all duration-200"
                >
                  <div
                    class="w-12 h-12 bg-purple-primary group-hover:bg-purple-600 rounded-lg flex items-center justify-center transition-colors duration-200"
                  >
                    <svg class="w-6 h-6 text-white-bg" fill="currentColor" viewBox="0 0 20 20">
                      <path
                        fill-rule="evenodd"
                        d="M4 4a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2H4zm0 2h12v8H4V6z"
                        clip-rule="evenodd"
                      ></path>
                    </svg>
                  </div>
                  <div class="text-left">
                    <p class="text-xl font-bold">{{ establishmentsFollowingCount }}</p>
                    <p class="text-sm opacity-80 group-hover:underline">Establecimientos seguidos</p>
                  </div>
                </button>
              </div>
            </div>

            <!-- Lado derecho: Botones de acción completamente separados y alineados al borde derecho -->
            <div class="flex flex-col sm:flex-row gap-3">
              <button
                v-if="session?.userId && roleId === 1 && !hasPendingRequest"
                @click="requestOrganizer"
                :disabled="loadingPendingRequest"
                class="bg-purple-primary hover:bg-purple-600 text-white font-medium px-4 py-2 rounded-lg transition-colors duration-200 flex items-center justify-center gap-2 text-sm w-full sm:w-48 disabled:opacity-50"
              >
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 16 16">
                  <path
                    fill-rule="evenodd"
                    d="M3 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 4a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z"
                    clip-rule="evenodd"
                  ></path>
                </svg>
                {{ loadingPendingRequest ? "Verificando..." : "Solicitar organizador" }}
              </button>

              <button
                v-else-if="session?.userId && roleId === 1 && hasPendingRequest"
                class="bg-purple-primary text-white font-medium px-4 py-2 rounded-lg flex items-center justify-center gap-2 text-sm w-full sm:w-48 cursor-not-allowed opacity-75"
                disabled
              >
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fill-rule="evenodd"
                    d="M10 18a8 8 0 100-16 8 8 0 000 16zm3.707-9.293a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"
                    clip-rule="evenodd"
                  ></path>
                </svg>
                Solicitud pendiente
              </button>

              <button
                v-else-if="session?.userId && roleId && roleId >= 2"
                @click="goToEstablishments"
                class="bg-purple-primary hover:bg-purple-600 text-white font-medium px-4 py-2 rounded-lg transition-colors duration-200 flex items-center justify-center gap-2 text-sm w-full sm:w-48"
              >
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fill-rule="evenodd"
                    d="M4 4a2 2 0 00-2 2v8a2 2 0 002 2h12a2 2 0 002-2V6a2 2 0 00-2-2H4zm0 2h12v8H4V6z"
                    clip-rule="evenodd"
                  ></path>
                </svg>
                Establecimientos
              </button>

              <button
                @click="showPasswordModal = true"
                class="bg-gray-600 hover:bg-gray-700 text-white font-medium px-4 py-2 rounded-lg transition-colors duration-200 flex items-center justify-center gap-2 text-sm w-full sm:w-48"
              >
                <svg class="w-5 h-5" fill="currentColor" viewBox="0 0 20 20">
                  <path
                    fill-rule="evenodd"
                    d="M5 9V7a5 5 0 0110 0v2a2 2 0 012 2v5a2 2 0 01-2 2H5a2 2 0 01-2-2v-5a2 2 0 012-2zm8-2v2H7V7a3 3 0 016 0z"
                    clip-rule="evenodd"
                  ></path>
                </svg>
                Cambiar contraseña
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div
      class="mt-4 p-6 border border-white-bg bg-dark-primary mx-4 sm:mx-12 md:mx-24 lg:mx-36 xl:mx-52 mb-4 rounded-2xl shadow-2xl"
    >
      <div class="flex items-center gap-3 mb-6">
        <div class="w-2 h-8 bg-gradient-to-b from-purple-primary to-purple-600 rounded-full"></div>
        <h2 class="text-2xl font-bold text-white-bg">Estadísticas</h2>

        <!-- Botón para alternar gráficos -->
        <div class="ml-auto">
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
        <div class="flex flex-col items-center gap-2 text-white-bg text-3xl max-w-[200px]">
          <span class="font-bold text-4xl text-center leading-tight">{{ favoriteEventType }}</span>
          <span class="text-center">Tipo de evento favorito</span>
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

    <!-- Modal de seguidores -->
    <div
      v-show="showFollowersModal"
      class="fixed inset-0 flex items-center justify-center bg-black/20 backdrop-blur z-50"
      @click.self="closeFollowersModal"
    >
      <div
        class="w-[90%] md:w-[50%] lg:w-[40%] border border-white-bg bg-dark-primary p-6 rounded-xl max-h-[80vh] overflow-hidden flex flex-col"
      >
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold text-white-bg">Seguidores ({{ followersCount }})</h2>
          <button
            @click="closeFollowersModal"
            class="bg-red-primary hover:bg-red-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg flex items-center justify-center"
          >
            ✕
          </button>
        </div>

        <div class="flex-1 overflow-y-auto">
          <div v-if="loadingFollowData" class="text-center py-8">
            <p class="text-white-bg">Cargando seguidores...</p>
          </div>

          <div v-else-if="myFollowers.length === 0" class="text-center py-8">
            <p class="text-white-bg opacity-70">No tienes seguidores aún</p>
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="follow in myFollowers"
              :key="follow.id"
              class="flex items-center gap-3 p-3 bg-black-secundary rounded-lg"
            >
              <div class="flex-1">
                <p class="text-white-bg font-medium">{{ follow.name }}</p>
                <p class="text-white-bg text-xs opacity-50">Te sigue desde: {{ formatDate(follow.createdAt) }}</p>
              </div>
              <div class="text-white-bg text-sm opacity-60">
                {{ follow.role?.id === 1 ? "Usuario" : follow.role?.id === 2 ? "Organizador" : "Admin" }}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de siguiendo -->
    <div
      v-show="showFollowingModal"
      class="fixed inset-0 flex items-center justify-center bg-black/20 backdrop-blur z-50"
      @click.self="closeFollowingModal"
    >
      <div
        class="w-[90%] md:w-[50%] lg:w-[40%] border border-white-bg bg-dark-primary p-6 rounded-xl max-h-[80vh] overflow-hidden flex flex-col"
      >
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold text-white-bg">Siguiendo ({{ followingCount }})</h2>
          <button
            @click="closeFollowingModal"
            class="bg-red-primary hover:bg-red-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg flex items-center justify-center"
          >
            ✕
          </button>
        </div>

        <div class="flex-1 overflow-y-auto">
          <div v-if="loadingFollowData" class="text-center py-8">
            <p class="text-white-bg">Cargando usuarios que sigues...</p>
          </div>

          <div v-else-if="myFollowing.length === 0" class="text-center py-8">
            <p class="text-white-bg opacity-70">No sigues a ningún usuario aún</p>
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="follow in myFollowing"
              :key="follow.id"
              class="flex items-center justify-between p-3 bg-black-secundary rounded-lg"
            >
              <div class="flex-1">
                <p class="text-white-bg font-medium">{{ follow.name }}</p>
                <p class="text-white-bg text-xs opacity-50">Siguiendo desde: {{ formatDate(follow.createdAt) }}</p>
              </div>
              <div class="flex items-center gap-3">
                <div class="text-white-bg text-sm opacity-60">
                  {{ follow.role?.id === 1 ? "Usuario" : follow.role?.id === 2 ? "Organizador" : "Admin" }}
                </div>
                <button
                  @click="unfollowUser(follow.id)"
                  class="bg-red-primary hover:bg-red-600 text-white px-3 py-1 rounded text-sm font-medium transition-colors duration-200"
                >
                  Dejar de seguir
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de establecimientos seguidos -->
    <div
      v-show="showEstablishmentsModal"
      class="fixed inset-0 flex items-center justify-center bg-black/20 backdrop-blur z-50"
      @click.self="closeEstablishmentsModal"
    >
      <div
        class="w-[90%] md:w-[50%] lg:w-[40%] border border-white-bg bg-dark-primary p-6 rounded-xl max-h-[80vh] overflow-hidden flex flex-col"
      >
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold text-white-bg">
            Establecimientos Seguidos ({{ establishmentsFollowingCount }})
          </h2>
          <button
            @click="closeEstablishmentsModal"
            class="bg-red-primary hover:bg-red-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg flex items-center justify-center"
          >
            ✕
          </button>
        </div>

        <div class="flex-1 overflow-y-auto">
          <div v-if="loadingFollowData" class="text-center py-8">
            <p class="text-white-bg">Cargando establecimientos que sigues...</p>
          </div>

          <div v-else-if="myEstablishments.length === 0" class="text-center py-8">
            <p class="text-white-bg opacity-70">No sigues ningún establecimiento aún</p>
          </div>

          <div v-else class="space-y-3">
            <div
              v-for="establishmentFollow in myEstablishments"
              :key="establishmentFollow.id"
              class="flex items-center justify-between p-4 bg-black-secundary rounded-lg"
            >
              <div class="flex-1">
                <h3 class="text-white-bg font-bold text-lg">{{ establishmentFollow.name }}</h3>
                <p class="text-white-bg text-sm opacity-70 mt-1">
                  <span class="font-medium">Dirección:</span> {{ establishmentFollow.address }}
                </p>
                <p class="text-white-bg text-sm opacity-70">
                  <span class="font-medium">Teléfono:</span> {{ establishmentFollow.phone }}
                </p>
                <p class="text-white-bg text-sm opacity-70">
                  <span class="font-medium">Email:</span> {{ establishmentFollow.email }}
                </p>
                <p class="text-white-bg text-xs opacity-50 mt-2">
                  Siguiendo desde: {{ formatDate(establishmentFollow.createdAt) }}
                </p>
              </div>
              <div class="flex items-center gap-3 ml-4">
                <button
                  @click="unfollowEstablishment(establishmentFollow.id)"
                  class="bg-red-primary hover:bg-red-600 text-white px-3 py-1 rounded text-sm font-medium transition-colors duration-200"
                >
                  Dejar de seguir
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Modal de cambio de contraseña -->
    <div
      v-show="showPasswordModal"
      class="fixed inset-0 flex items-center justify-center bg-black/20 backdrop-blur z-50"
      @click.self="closePasswordModal"
    >
      <div class="w-[90%] md:w-[50%] lg:w-[35%] border border-white-bg bg-dark-primary p-6 rounded-xl">
        <div class="flex justify-between items-center mb-4">
          <h2 class="text-xl font-bold text-white-bg">Cambiar contraseña</h2>
          <button
            @click="closePasswordModal"
            class="bg-red-primary hover:bg-red-600 text-white px-3 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg flex items-center justify-center"
          >
            ✕
          </button>
        </div>

        <form @submit.prevent="changePassword" class="flex flex-col gap-4">
          <div>
            <label class="block text-white-bg font-medium mb-2">Contraseña actual</label>
            <div class="flex border border-dark-primary rounded-lg overflow-hidden bg-white">
              <input
                v-model="passwordForm.currentPassword"
                :type="showCurrentPassword ? 'text' : 'password'"
                placeholder="Introduce tu contraseña actual"
                class="w-full p-3 border-none outline-none"
                required
              />
              <button
                type="button"
                class="flex shrink-0 justify-center items-center px-3"
                @click="showCurrentPassword = !showCurrentPassword"
              >
                <component :is="showCurrentPassword ? EyeOff : Eye" class="w-5 h-5 text-dark-primary" />
              </button>
            </div>
            <p v-if="passwordErrors.currentPassword" class="text-red-500 text-sm mt-1">
              {{ passwordErrors.currentPassword }}
            </p>
          </div>

          <div>
            <label class="block text-white-bg font-medium mb-2">Nueva contraseña</label>
            <div class="flex border border-dark-primary rounded-lg overflow-hidden bg-white">
              <input
                v-model="passwordForm.newPassword"
                :type="showNewPassword ? 'text' : 'password'"
                placeholder="Introduce tu nueva contraseña"
                class="w-full p-3 border-none outline-none"
                required
              />
              <button
                type="button"
                class="flex shrink-0 justify-center items-center px-3"
                @click="showNewPassword = !showNewPassword"
              >
                <component :is="showNewPassword ? EyeOff : Eye" class="w-5 h-5 text-dark-primary" />
              </button>
            </div>
            <p v-if="passwordErrors.newPassword" class="text-red-500 text-sm mt-1">
              {{ passwordErrors.newPassword }}
            </p>
          </div>

          <div>
            <label class="block text-white-bg font-medium mb-2">Confirmar nueva contraseña</label>
            <div class="flex border border-dark-primary rounded-lg overflow-hidden bg-white">
              <input
                v-model="passwordForm.confirmPassword"
                :type="showConfirmPassword ? 'text' : 'password'"
                placeholder="Repite tu nueva contraseña"
                class="w-full p-3 border-none outline-none"
                required
              />
              <button
                type="button"
                class="flex shrink-0 justify-center items-center px-3"
                @click="showConfirmPassword = !showConfirmPassword"
              >
                <component :is="showConfirmPassword ? EyeOff : Eye" class="w-5 h-5 text-dark-primary" />
              </button>
            </div>
            <p v-if="passwordErrors.confirmPassword" class="text-red-500 text-sm mt-1">
              {{ passwordErrors.confirmPassword }}
            </p>
          </div>

          <p v-if="passwordErrors.general" class="text-red-500 text-sm text-center">
            {{ passwordErrors.general }}
          </p>

          <p v-if="passwordSuccess" class="text-green-500 text-sm text-center">
            {{ passwordSuccess }}
          </p>

          <div class="flex gap-3 justify-end mt-4">
            <button
              type="button"
              @click="closePasswordModal"
              class="bg-gray-600 hover:bg-gray-700 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200"
            >
              Cancelar
            </button>
            <button
              type="submit"
              :disabled="changingPassword"
              class="bg-purple-primary hover:bg-purple-600 text-white px-4 py-2 rounded-lg font-medium transition-colors duration-200 shadow-md hover:shadow-lg disabled:opacity-50"
            >
              {{ changingPassword ? "Cambiando..." : "Cambiar contraseña" }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref, watch } from "vue";
import { useRouter } from "vue-router";
import { Eye, EyeOff } from "lucide-vue-next";
import { useSession } from "@/composables/useSession";
import { useFavorites } from "@/composables/useFavorites";
import { ApiService } from "@/services/api";

const router = useRouter();
const { session } = useSession();

// Datos de favoritos y tipos de eventos
const favoritesData = ref<any[]>([]);
const eventTypes = ref<any[]>([]);
const isLoadingFavorites = ref(false);
const isLoadingEventTypes = ref(false);

// Estado para alternar entre gráficos
const showBarChart = ref(false);

// Estado para el modal de cambio de contraseña
const showPasswordModal = ref(false);
const changingPassword = ref(false);
const passwordSuccess = ref("");
const showCurrentPassword = ref(false);
const showNewPassword = ref(false);
const showConfirmPassword = ref(false);

// Estado para modales de seguidores
const showFollowersModal = ref(false);
const showFollowingModal = ref(false);
const showEstablishmentsModal = ref(false);
const myFollowers = ref<any[]>([]);
const myFollowing = ref<any[]>([]);
const myEstablishments = ref<any[]>([]);
const followersCount = ref(0);
const followingCount = ref(0);
const establishmentsFollowingCount = ref(0);
const loadingFollowData = ref(false);

// Estado para solicitud de organizador
const hasPendingRequest = ref(false);
const loadingPendingRequest = ref(false);

const passwordForm = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
});

const passwordErrors = ref({
  currentPassword: "",
  newPassword: "",
  confirmPassword: "",
  general: "",
});

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

onMounted(() => {
  // Cargar datos de forma asíncrona
  Promise.all([loadUserFavorites(), loadEventTypes(), loadFollowData(), checkPendingRequest()]);
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
const roleId = computed(() => session.value?.roleId);
const name = computed(() => session.value?.name || "");
const email = computed(() => session.value?.email || "");

// Watch para detectar cambios en el rol del usuario
watch(roleId, (newRoleId, oldRoleId) => {
  if (newRoleId && newRoleId !== 1) {
    // Si ya no es usuario normal, limpiar estado de solicitud pendiente
    hasPendingRequest.value = false;
  } else if (newRoleId === 1 && oldRoleId !== 1) {
    // Si cambió a usuario normal, verificar solicitudes pendientes
    checkPendingRequest();
  }
});

const requestOrganizer = async () => {
  if (!session.value?.userId) return;

  try {
    // NUEVO: Crear solicitud en lugar de cambiar rol directamente
    const res = await fetch(`http://144.24.204.95:8080/api/organizer-requests`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value.token}`,
      },
      body: JSON.stringify({ userId: session.value.userId }),
    });

    if (!res.ok) {
      const errorData = await res.json();
      throw new Error(errorData.error || "Error al enviar solicitud");
    }

    alert("Solicitud de organizador enviada. Esperando aprobación del administrador.");

    // Actualizar el estado de la solicitud pendiente
    hasPendingRequest.value = true;
  } catch (err: any) {
    alert("Error: " + err.message);
  }
};

const goToEstablishments = () => {
  router.push({ name: "establishments" });
};

// Funciones para el modal de cambio de contraseña
const closePasswordModal = () => {
  showPasswordModal.value = false;
  passwordForm.value = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
  };
  passwordErrors.value = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
    general: "",
  };
  passwordSuccess.value = "";
  showCurrentPassword.value = false;
  showNewPassword.value = false;
  showConfirmPassword.value = false;
};

const validatePasswords = () => {
  passwordErrors.value = {
    currentPassword: "",
    newPassword: "",
    confirmPassword: "",
    general: "",
  };

  let isValid = true;

  // Validar contraseña actual
  if (!passwordForm.value.currentPassword) {
    passwordErrors.value.currentPassword = "La contraseña actual es obligatoria";
    isValid = false;
  }

  // Validar nueva contraseña (mismas reglas que Register.vue)
  if (!passwordForm.value.newPassword) {
    passwordErrors.value.newPassword = "La nueva contraseña es un campo requerido.";
    isValid = false;
  } else if (passwordForm.value.newPassword.length < 6) {
    passwordErrors.value.newPassword = "Debe tener al menos 6 caracteres.";
    isValid = false;
  } else if (!/\d/.test(passwordForm.value.newPassword)) {
    passwordErrors.value.newPassword = "Debe contener al menos un número.";
    isValid = false;
  }

  // Validar confirmación de contraseña
  if (!passwordForm.value.confirmPassword) {
    passwordErrors.value.confirmPassword = "Debes confirmar la nueva contraseña";
    isValid = false;
  } else if (passwordForm.value.newPassword !== passwordForm.value.confirmPassword) {
    passwordErrors.value.confirmPassword = "Las contraseñas deben coincidir.";
    isValid = false;
  }

  return isValid;
};

const changePassword = async () => {
  if (!validatePasswords()) return;

  changingPassword.value = true;
  passwordSuccess.value = "";

  try {
    const response = await fetch(`http://144.24.204.95:8080/api/users/${session.value.userId}/password`, {
      method: "PATCH",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value.token}`,
      },
      body: JSON.stringify({
        currentPassword: passwordForm.value.currentPassword,
        newPassword: passwordForm.value.newPassword,
      }),
    });

    if (response.ok) {
      const data = await response.json();
      passwordSuccess.value = data.message || "Contraseña cambiada correctamente";
      setTimeout(() => {
        closePasswordModal();
      }, 2000);
    } else {
      // Intentar leer como JSON, si falla leer como texto
      try {
        const data = await response.json();
        passwordErrors.value.general = data.error || "Error al cambiar la contraseña";
      } catch (jsonError) {
        const text = await response.text();
        passwordErrors.value.general = text || "Error al cambiar la contraseña";
      }
    }
  } catch (error) {
    passwordErrors.value.general = "Error de conexión. Inténtalo de nuevo.";
  } finally {
    changingPassword.value = false;
  }
};

// Funciones para el sistema de seguidores
const loadFollowData = async () => {
  if (!session.value?.userId) return;

  try {
    // Cargar contadores de usuarios
    const countsResponse = await fetch(`http://144.24.204.95:8080/api/user-follows/${session.value.userId}/counts`, {
      headers: {
        Authorization: `Bearer ${session.value.token}`,
      },
    });

    if (countsResponse.ok) {
      const counts = await countsResponse.json();
      followersCount.value = counts.followersCount;
      followingCount.value = counts.followingCount;
    }

    // Cargar contador de establecimientos seguidos
    const establishmentCountResponse = await fetch(
      `http://144.24.204.95:8080/api/establishment-follows/user/${session.value.userId}/count`,
      {
        headers: {
          Authorization: `Bearer ${session.value.token}`,
        },
      }
    );

    if (establishmentCountResponse.ok) {
      const establishmentCounts = await establishmentCountResponse.json();
      establishmentsFollowingCount.value = establishmentCounts.establishmentsFollowingCount;
    }
  } catch (error) {
    console.error("Error loading follow data:", error);
  }
};

// Verificar si el usuario tiene una solicitud pendiente de organizador
const checkPendingRequest = async () => {
  // Solo verificar si es usuario (rol 1) y tiene sesión activa
  if (!session.value?.userId || roleId.value !== 1) {
    hasPendingRequest.value = false;
    return;
  }

  loadingPendingRequest.value = true;
  try {
    const response = await fetch(
      `http://144.24.204.95:8080/api/organizer-requests/user/${session.value.userId}/pending`,
      {
        headers: { Authorization: `Bearer ${session.value.token}` },
      }
    );

    if (response.ok) {
      const data = await response.json();
      hasPendingRequest.value = data.hasPendingRequest;
    } else {
      hasPendingRequest.value = false;
    }
  } catch (error) {
    console.error("Error checking pending request:", error);
    hasPendingRequest.value = false;
  } finally {
    loadingPendingRequest.value = false;
  }
};

// Watch para detectar cuando la sesión se carga inicialmente
watch(
  () => session.value?.userId,
  (newUserId) => {
    if (newUserId && roleId.value === 1) {
      checkPendingRequest();
    }
  },
  { immediate: true }
);

const loadFollowersData = async () => {
  if (!session.value?.userId) return;

  loadingFollowData.value = true;
  try {
    const response = await fetch(`http://144.24.204.95:8080/api/user-follows/${session.value.userId}/followers`, {
      headers: {
        Authorization: `Bearer ${session.value.token}`,
      },
    });

    if (response.ok) {
      myFollowers.value = await response.json();
    }
  } catch (error) {
    console.error("Error loading followers:", error);
  } finally {
    loadingFollowData.value = false;
  }
};

const loadFollowingData = async () => {
  if (!session.value?.userId) return;

  loadingFollowData.value = true;
  try {
    // Usar el endpoint correcto para obtener usuarios que sigue
    const response = await fetch(`http://144.24.204.95:8080/api/user-follows/${session.value.userId}/following`, {
      headers: {
        Authorization: `Bearer ${session.value.token}`,
      },
    });

    if (response.ok) {
      const followData = await response.json();

      // Los datos ahora vienen directamente como objetos usuario
      myFollowing.value = Array.isArray(followData) ? followData : [];
    } else {
      console.warn(`Following API returned ${response.status}`);
      myFollowing.value = [];
    }
  } catch (error) {
    console.error("Error loading following:", error);
    myFollowing.value = [];
  } finally {
    loadingFollowData.value = false;
  }
};

const loadEstablishmentsData = async () => {
  if (!session.value?.userId) return;

  loadingFollowData.value = true;
  try {
    // Usar el endpoint que funciona para obtener establishment follows
    const response = await fetch(
      `http://144.24.204.95:8080/api/establishment-follows/user/${session.value.userId}/establishments`,
      {
        headers: {
          Authorization: `Bearer ${session.value.token}`,
        },
      }
    );

    if (response.ok) {
      const followData = await response.json();

      // Los datos ahora vienen directamente como objetos establecimiento
      myEstablishments.value = Array.isArray(followData) ? followData : [];
    } else {
      console.warn(`Establishments API returned ${response.status}`);
      myEstablishments.value = [];
    }
  } catch (error) {
    console.error("Error loading establishments:", error);
    myEstablishments.value = [];
  } finally {
    loadingFollowData.value = false;
  }
};

const openFollowersModal = async () => {
  showFollowersModal.value = true;
  await loadFollowersData();
};

const openFollowingModal = async () => {
  showFollowingModal.value = true;
  await loadFollowingData();
};

const openEstablishmentsModal = async () => {
  showEstablishmentsModal.value = true;
  await loadEstablishmentsData();
};

const closeFollowersModal = () => {
  showFollowersModal.value = false;
  myFollowers.value = [];
};

const closeFollowingModal = () => {
  showFollowingModal.value = false;
  myFollowing.value = [];
};

const closeEstablishmentsModal = () => {
  showEstablishmentsModal.value = false;
  myEstablishments.value = [];
};

const unfollowUser = async (userId: number) => {
  if (!session.value?.userId) return;

  try {
    const response = await fetch("http://144.24.204.95:8080/api/user-follows", {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value.token}`,
      },
      body: JSON.stringify({
        followerId: session.value.userId,
        followedId: userId,
      }),
    });

    if (response.ok) {
      // Actualizar los datos
      await loadFollowingData();
      await loadFollowData();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al dejar de seguir usuario"));
    }
  } catch (error) {
    console.error("Error unfollowing user:", error);
    alert("Error de conexión");
  }
};

const unfollowEstablishment = async (establishmentId: number) => {
  if (!session.value?.userId) return;

  try {
    const response = await fetch("http://144.24.204.95:8080/api/establishment-follows", {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
        Authorization: `Bearer ${session.value.token}`,
      },
      body: JSON.stringify({
        userId: session.value.userId,
        establishmentId,
      }),
    });

    if (response.ok) {
      // Actualizar los datos
      await loadEstablishmentsData();
      await loadFollowData();
    } else {
      const errorData = await response.json();
      alert("Error: " + (errorData.error || "Error al dejar de seguir establecimiento"));
    }
  } catch (error) {
    console.error("Error unfollowing establishment:", error);
    alert("Error de conexión");
  }
};

const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString("es-ES", {
    year: "numeric",
    month: "long",
    day: "numeric",
  });
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
