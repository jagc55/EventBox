const API_BASE_URL = "http://144.24.204.95:8080/api";

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
  role?: { id: number; name?: string }; // Cambiado para reflejar la estructura real de la API
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

  static async getEventsByFollowedUsers(userId: number): Promise<Event[]> {
    return this.makeRequest<Event[]>(`/events/following/${userId}`);
  }

  static async getEventsByFollowedEstablishments(userId: number): Promise<Event[]> {
    // Implementación usando endpoints existentes
    const [allEvents, followedEstablishments] = await Promise.all([
      this.getAllEvents(),
      this.getFollowedEstablishments(userId),
    ]);

    // Extraer los IDs de los establecimientos seguidos
    const followedEstablishmentIds = followedEstablishments.map((est) => est.id);

    // Filtrar eventos que pertenecen a establecimientos seguidos
    return allEvents
      .filter((event) => followedEstablishmentIds.includes(event.establishment.id))
      .sort((a, b) => {
        const dateTimeA = new Date(`${a.date}T${a.time}`);
        const dateTimeB = new Date(`${b.date}T${b.time}`);
        return dateTimeA.getTime() - dateTimeB.getTime();
      });
  }

  // Event Types endpoints
  static async getAllEventTypes(): Promise<EventType[]> {
    return this.makeRequest<EventType[]>("/event-types");
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

  // User Follow interfaces and endpoints
  static async followUser(followerId: number, followedId: number): Promise<{ message: string; follow: any }> {
    return this.makeRequest<{ message: string; follow: any }>("/user-follows", {
      method: "POST",
      body: JSON.stringify({ followerId, followedId }),
    });
  }

  static async unfollowUser(followerId: number, followedId: number): Promise<{ message: string }> {
    return this.makeRequest<{ message: string }>("/user-follows", {
      method: "DELETE",
      body: JSON.stringify({ followerId, followedId }),
    });
  }

  static async isFollowing(followerId: number, followedId: number): Promise<{ isFollowing: boolean }> {
    return this.makeRequest<{ isFollowing: boolean }>(
      `/user-follows/check?followerId=${followerId}&followedId=${followedId}`
    );
  }

  static async getFollowing(userId: number): Promise<any[]> {
    return this.makeRequest<any[]>(`/user-follows/${userId}/following`);
  }

  static async getFollowers(userId: number): Promise<any[]> {
    return this.makeRequest<any[]>(`/user-follows/${userId}/followers`);
  }

  static async getFollowCounts(userId: number): Promise<{ followersCount: number; followingCount: number }> {
    return this.makeRequest<{ followersCount: number; followingCount: number }>(`/user-follows/${userId}/counts`);
  }

  static async getMostFollowedUsers(): Promise<any[]> {
    return this.makeRequest<any[]>("/user-follows/most-followed");
  }

  // Establishment Follow methods
  static async followEstablishment(userId: number, establishmentId: number): Promise<{ message: string; follow: any }> {
    return this.makeRequest<{ message: string; follow: any }>("/establishment-follows", {
      method: "POST",
      body: JSON.stringify({ userId, establishmentId }),
    });
  }

  static async unfollowEstablishment(userId: number, establishmentId: number): Promise<{ message: string }> {
    return this.makeRequest<{ message: string }>("/establishment-follows", {
      method: "DELETE",
      body: JSON.stringify({ userId, establishmentId }),
    });
  }

  static async isFollowingEstablishment(userId: number, establishmentId: number): Promise<{ isFollowing: boolean }> {
    return this.makeRequest<{ isFollowing: boolean }>(
      `/establishment-follows/check?userId=${userId}&establishmentId=${establishmentId}`
    );
  }

  static async getFollowedEstablishments(userId: number): Promise<any[]> {
    return this.makeRequest<any[]>(`/establishment-follows/user/${userId}/establishments`);
  }

  static async getEstablishmentFollowers(establishmentId: number): Promise<any[]> {
    return this.makeRequest<any[]>(`/establishment-follows/establishment/${establishmentId}/followers`);
  }

  static async getFollowedEstablishmentsCount(userId: number): Promise<{ establishmentsFollowingCount: number }> {
    return this.makeRequest<{ establishmentsFollowingCount: number }>(`/establishment-follows/user/${userId}/count`);
  }

  static async getEstablishmentFollowersCount(establishmentId: number): Promise<{ followersCount: number }> {
    return this.makeRequest<{ followersCount: number }>(
      `/establishment-follows/establishment/${establishmentId}/count`
    );
  }

  static async getMostFollowedEstablishments(): Promise<any[]> {
    return this.makeRequest<any[]>("/establishment-follows/most-followed");
  }

  public static async getAllUsers(): Promise<User[]> {
    return this.makeRequest<User[]>("/users");
  }
  public static async getAllEstablishments(): Promise<Establishment[]> {
    return this.makeRequest<Establishment[]>("/establishments");
  }
}
