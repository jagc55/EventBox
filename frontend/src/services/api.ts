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
}
