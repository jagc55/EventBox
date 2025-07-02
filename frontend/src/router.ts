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
import { useSession } from "./composables/useSession";

// Route access levels:
// 0: Public (accessible without login)
// 1: User (any logged in user)
// 2: Organizer (roleId = 2) or Admin (roleId = 3)
// 3: Admin only (roleId = 3)

const routes = [
  {
    path: "/",
    name: "events",
    component: Events,
    meta: { requiresAuth: false }, // Public
  },
  {
    path: "/events/:eventId",
    name: "event",
    component: Event,
    meta: { requiresAuth: false }, // Public
  },
  {
    path: "/profile",
    name: "profile",
    component: Profile,
    meta: { requiresAuth: true }, // Any logged in user
  },
  {
    path: "/login",
    name: "login",
    component: Login,
    meta: { requiresAuth: false }, // Public
  },
  {
    path: "/register",
    name: "register",
    component: Register,
    meta: { requiresAuth: false }, // Public
  },
  {
    path: "/admin",
    name: "admin",
    component: Admin,
    meta: { requiresAuth: true, requiredRole: 3 }, // Admin only
  },
  {
    path: "/social",
    name: "social",
    component: Social,
    meta: { requiresAuth: true }, // Any logged in user
  },

  // Organiser routes (Organizer or Admin)
  {
    path: "/organiser",
    name: "establishments",
    component: Establishments,
    meta: { requiresAuth: true, requiredRole: 2 }, // Organizer or Admin
  },
  {
    path: "/organiser/establishments/:establishmentId",
    name: "establishment",
    component: Establishment,
    meta: { requiresAuth: true, requiredRole: 2 }, // Organizer or Admin
  },
  {
    path: "/organiser/establishments/:establishmentId/events/:eventId",
    name: "organiser-event",
    component: OrganiserEvent,
    meta: { requiresAuth: true, requiredRole: 2 }, // Organizer or Admin
  },

  // Catch-all route for 404
  {
    path: "/:pathMatch(.*)*",
    redirect: "/",
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Navigation guard to protect routes
router.beforeEach((to, from, next) => {
  // Load session from localStorage if needed
  const { session, isLoggedIn, loadSessionFromStorage } = useSession();
  loadSessionFromStorage();

  // If trying to access login/register while already logged in, redirect to home
  if ((to.name === "login" || to.name === "register") && isLoggedIn.value) {
    next({ name: "events" });
    return;
  }

  // Check if the route requires authentication
  const requiresAuth = to.meta.requiresAuth;
  const requiredRole = to.meta.requiredRole as number | undefined;

  if (!requiresAuth) {
    // Public route, allow access
    next();
    return;
  }

  // Route requires authentication
  if (!isLoggedIn.value) {
    // Not logged in, redirect to login with return URL
    next({
      name: "login",
      query: { redirect: to.fullPath },
    });
    return;
  }

  // Check if specific role is required
  if (requiredRole !== undefined) {
    const userRoleId = session.value.roleId;

    // Allow access if user is admin (roleId = 3) or has the required role
    if (userRoleId === 3 || userRoleId === requiredRole) {
      next();
      return;
    } else {
      // User doesn't have the required role, redirect to main page
      next({ name: "events" });
      return;
    }
  }

  // User is authenticated and no specific role required
  next();
});

export default router;
