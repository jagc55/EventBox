import { describe, it, expect, beforeEach, vi } from "vitest";
import { mount } from "@vue/test-utils";
import { createRouter, createWebHistory } from "vue-router";
import Events from "../../src/views/Events.vue";
import Event from "../../src/views/Event.vue";
import { ApiService } from "../../src/services/api";

// Mock the API service
vi.mock("../../src/services/api", () => ({
  ApiService: {
    getAllEvents: vi.fn(),
    getEventById: vi.fn(),
    addToFavorites: vi.fn(),
    removeFromFavorites: vi.fn(),
    getFavorites: vi.fn(),
  },
}));

describe("Component Integration", () => {
  let router: any;

  beforeEach(() => {
    vi.clearAllMocks();

    // Setup router
    router = createRouter({
      history: createWebHistory(),
      routes: [
        { path: "/events", component: Events },
        { path: "/event/:id", component: Event },
      ],
    });
  });

  it("should mount Events component without errors", async () => {
    // Mock API response con estructura completa del Event
    const mockEvents = [
      {
        id: 1,
        title: "Evento Test 1",
        description: "Descripción del evento 1",
        date: "2024-12-25",
        time: "20:00",
        price: 25.5,
        minimumAge: 18,
        imageUrl: "test-image.jpg",
        tags: "música,concierto",
        eventType: { id: 1, name: "Concierto" },
        establishment: {
          id: 1,
          name: "Venue Test",
          address: "Calle Test 123",
          email: "venue@test.com",
          phone: "123456789",
          locality: {
            id: 1,
            name: "Madrid",
            province: { id: 1, name: "Madrid" },
          },
        },
        user: { id: 1, name: "Organizer", email: "org@test.com" },
      },
      {
        id: 2,
        title: "Evento Test 2",
        description: "Descripción del evento 2",
        date: "2024-12-26",
        time: "21:00",
        price: 30.0,
        minimumAge: 21,
        imageUrl: "test-image2.jpg",
        tags: "fiesta,electrónica",
        eventType: { id: 2, name: "Fiesta" },
        establishment: {
          id: 2,
          name: "Club Test",
          address: "Avenida Test 456",
          email: "club@test.com",
          phone: "987654321",
          locality: {
            id: 2,
            name: "Barcelona",
            province: { id: 2, name: "Barcelona" },
          },
        },
        user: { id: 1, name: "Organizer", email: "org@test.com" },
      },
    ];

    vi.mocked(ApiService.getAllEvents).mockResolvedValue(mockEvents);

    const wrapper = mount(Events, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.exists()).toBe(true);
    expect(ApiService.getAllEvents).toHaveBeenCalled();
  });

  it("should mount Event component without errors", async () => {
    // Mock single event response
    const mockEvent = {
      id: 1,
      title: "Evento Test Individual",
      description: "Descripción detallada del evento",
      date: "2024-12-25",
      time: "20:00",
      price: 25.5,
      minimumAge: 18,
      imageUrl: "test-image.jpg",
      tags: "música,concierto",
      eventType: { id: 1, name: "Concierto" },
      establishment: {
        id: 1,
        name: "Venue Test",
        address: "Calle Individual 789",
        email: "individual@test.com",
        phone: "111222333",
        locality: {
          id: 1,
          name: "Madrid",
          province: { id: 1, name: "Madrid" },
        },
      },
      user: { id: 1, name: "Organizer", email: "org@test.com" },
    };

    vi.mocked(ApiService.getEventById).mockResolvedValue(mockEvent);

    // Navigate to event route
    await router.push("/event/1");

    const wrapper = mount(Event, {
      global: {
        plugins: [router],
      },
    });

    expect(wrapper.exists()).toBe(true);
  });

  it("should handle API calls correctly in Events component", async () => {
    const mockEvents = [
      {
        id: 1,
        title: "Evento API Test",
        description: "Evento para probar API",
        date: "2024-12-25",
        time: "20:00",
        price: 15.0,
        minimumAge: 18,
        imageUrl: "api-test.jpg",
        tags: "test,api",
        eventType: { id: 1, name: "Test" },
        establishment: {
          id: 1,
          name: "Test Venue",
          address: "Calle API 999",
          email: "api@test.com",
          phone: "444555666",
          locality: {
            id: 1,
            name: "Madrid",
            province: { id: 1, name: "Madrid" },
          },
        },
        user: { id: 1, name: "Tester", email: "tester@test.com" },
      },
    ];

    vi.mocked(ApiService.getAllEvents).mockResolvedValue(mockEvents);

    const wrapper = mount(Events, {
      global: {
        plugins: [router],
      },
    });

    // Wait for component to mount and make API calls
    await wrapper.vm.$nextTick();

    expect(ApiService.getAllEvents).toHaveBeenCalled();
    expect(wrapper.exists()).toBe(true);
  });

  it("should handle router navigation", async () => {
    await router.push("/events");
    expect(router.currentRoute.value.path).toBe("/events");

    await router.push("/event/1");
    expect(router.currentRoute.value.path).toBe("/event/1");
  });
});
