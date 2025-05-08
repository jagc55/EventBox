<template>
  <div
    class="h-full bg-cover bg-[center_10%] scale-105 bg-[url(@/assets/images/bg.png)]"
  >
    <div class="flex items-center justify-center size-full backdrop-blur-sm">
      <div class="bg-blue-primary rounded-lg shadow-xl w-full max-w-lg p-10">
        <h2 class="text-2xl font-bold text-dark-primary m-4">Iniciar sesión</h2>
        <form class="flex flex-col gap-3">
          <div class="flex border border-dark-primary">
            <User
              class="w-12 h-12 text-dark-primary border-r border-dark-primary bg-white-bg"
            />
            <input
              type="text"
              placeholder="Usuario"
              class="rounded w-full p-3"
            />
          </div>
          <div class="flex border border-dark-primary">
            <Lock
              class="w-12 h-12 text-dark-primary border-r border-dark-primary bg-white-bg"
            />
            <input
              type="password"
              placeholder="Contraseña"
              class="rounded w-full p-3"
            />
          </div>
        </form>
      </div>
    </div>
  </div>
</template>
