import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import VueDevTools from "vite-plugin-vue-devtools";
import svgLoader from "vite-svg-loader";
import { fileURLToPath, URL } from "node:url";

export default defineConfig({
  plugins: [vue(), VueDevTools(), svgLoader()],
  resolve: { alias: { "@": fileURLToPath(new URL("./src", import.meta.url)) } },
  server: {
    proxy: {
      "/api": {
        target: "http://144.24.204.95:8080",
        changeOrigin: true,
        // rewrite: (path) => path.replace(/^\/api/, '/api'), // descomenta si tu backend usa el prefijo /api
      },
    },
  },
});
