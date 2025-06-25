const API_BASE_URL = "http://localhost:8080/api";

interface EventType {
  id: number;
  name: string;
}

interface Province {
  id: number;
  name: string;
}

interface Locality {
  id: number;
  name: string;
  province: Province;
}

interface Establishment {
  id: number;
  name: string;
  address: string;
  email: string;
  phone: string;
  locality: Locality;
}

interface User {
  id: number;
  name: string;
  email: string;
}

export interface Event {
  id: number;
  title: string;
  description: string;
  date: string; // LocalDate se convierte a string en JSON
  time: string; // LocalTime se convierte a string en JSON
  price: number;
  minimumAge: number;
  imageUrl: string;
  tags: string;
  eventType: EventType;
  establishment: Establishment;
  user: User;
}

export interface Comment {
  id: number;
  description: string;
  date: string; // LocalDateTime se convierte a string en JSON
  user: User;
  event: Event;
}

export interface Favorite {
  user: User;
  event: Event;
  addedAt: string; // LocalDateTime se convierte a string en JSON
}

export class ApiService {
  private static async makeRequest<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const url = `${API_BASE_URL}${endpoint}`;

    const defaultHeaders = {
      "Content-Type": "application/json",
    };

    const config: RequestInit = {
      ...options,
      headers: {
        ...defaultHeaders,
        ...options.headers,
      },
    };

    try {
      const response = await fetch(url, config);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      return await response.json();
    } catch (error) {
      console.error("API Error:", error);
      throw error;
    }
  }

  // Events endpoints
  static async getAllEvents(): Promise<Event[]> {
    return this.makeRequest<Event[]>("/events");
  }

  static async getAllEventsOrdered(): Promise<Event[]> {
    return this.makeRequest<Event[]>("/events/ordered");
  }

  static async getEventById(id: number): Promise<Event> {
    return this.makeRequest<Event>(`/events/${id}`);
  }

  static async getEventsByEstablishment(establishmentId: number): Promise<Event[]> {
    return this.makeRequest<Event[]>(`/events/establishments/${establishmentId}/events`);
  }

  // Comments endpoints
  static async getCommentsByEvent(eventId: number): Promise<Comment[]> {
    return this.makeRequest<Comment[]>(`/comments/event/${eventId}`);
  }

  static async createComment(comment: { description: string; userId: number; eventId: number }): Promise<Comment> {
    return this.makeRequest<Comment>("/comments/create", {
      method: "POST",
      body: JSON.stringify(comment),
    });
  }

  static async deleteComment(commentId: number): Promise<void> {
    const response = await fetch(`${API_BASE_URL}/comments/${commentId}`, {
      method: "DELETE",
      headers: {
        "Content-Type": "application/json",
      },
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    // No intentar parsear JSON para respuestas DELETE
    // Solo verificar que la respuesta sea exitosa
    return;
  }

  // Favorites endpoints
  static async getFavoritesByUser(userId: number): Promise<Favorite[]> {
    return this.makeRequest<Favorite[]>(`/favorites/user/${userId}`);
  }

  static async isFavorite(userId: number, eventId: number): Promise<boolean> {
    return this.makeRequest<boolean>(`/favorites/user/${userId}/event/${eventId}`);
  }

  static async addToFavorites(userId: number, eventId: number): Promise<{ message: string }> {
    return this.makeRequest<{ message: string }>(`/favorites/user/${userId}/event/${eventId}`, {
      method: "POST",
    });
  }

  static async removeFromFavorites(userId: number, eventId: number): Promise<{ message: string }> {
    return this.makeRequest<{ message: string }>(`/favorites/user/${userId}/event/${eventId}`, {
      method: "DELETE",
    });
  }
}
