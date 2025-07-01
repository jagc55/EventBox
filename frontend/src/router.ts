import { createWebHistory, createRouter } from "vue-router";
import Profile from "./views/Profile.vue";
import Events from "./views/Events.vue";
import Event from "./views/Event.vue";
import Login from "./views/Login.vue";
import Register from "./views/Register.vue";
import Admin from "./views/Administrator/Admin.vue";
import Establishments from "./views/organiser/Establishments.vue";
import Establishment from "./views/organiser/Establishment.vue";
import OrganiserEvent from "./views/organiser/Event.vue";
import Social from "./views/Social.vue";

const routes = [
  {
    path: "/",
    name: "events",
    component: Events,
  },
  {
    path: "/events/:eventId",
    name: "event",
    component: Event,
  },
  { path: "/profile", name: "profile", component: Profile },
  { path: "/login", name: "login", component: Login },
  { path: "/register", name: "register", component: Register },
  { path: "/admin", name: "admin", component: Admin },
  { path: "/social", name: "social", component: Social },

  // Organiser
  {
    path: "/organiser",
    name: "establishments",
    component: Establishments,
  },
  {
    path: "/organiser/establishments/:establishmentId",
    name: "establishment",
    component: Establishment,
  },
  {
    path: "/organiser/establishments/:establishmentId/events/:eventId",
    name: "organiser-event",
    component: OrganiserEvent,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
