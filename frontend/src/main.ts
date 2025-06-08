import { createApp } from "vue";
import App from "./App.vue";
import router from "./router";
import "./assets/main.css";
import { useSession } from "@/composables/useSession";

const app = createApp(App);

const { loadSessionFromStorage } = useSession();
loadSessionFromStorage();

app.use(router).mount("#app");
