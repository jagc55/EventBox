package com.eventbox.backend.integration;

import com.eventbox.backend.model.*;
import com.eventbox.backend.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el flujo completo de gestión de eventos
 * que validan Controller + Service + Repository + Base de datos
 */
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User organizerUser;
    private Establishment establishment;
    private EventType eventType;
    private Locality locality;

    @BeforeEach
    void setUp() {
        // Crear rol organizador
        Role organizerRole = new Role();
        organizerRole.setName("ORGANIZER");
        organizerRole = roleRepository.save(organizerRole);

        // Crear usuario organizador
        organizerUser = new User();
        organizerUser.setEmail("organizer@test.com");
        organizerUser.setName("Test Organizer");
        organizerUser.setPassword("password123");
        organizerUser.setRole(organizerRole);
        organizerUser = userRepository.save(organizerUser);

        // Crear provincia y localidad
        Province province = new Province();
        province.setName("Test Province");
        province = provinceRepository.save(province);

        locality = new Locality();
        locality.setName("Test City");
        locality.setProvince(province);
        locality = localityRepository.save(locality);

        // Crear establishment
        establishment = new Establishment();
        establishment.setName("Test Venue");
        establishment.setAddress("123 Test Street");
        establishment.setEmail("venue@test.com");
        establishment.setPhone("+1234567890");
        establishment.setLocality(locality);
        establishment.setUser(organizerUser);
        establishment = establishmentRepository.save(establishment);

        // Crear tipo de evento
        eventType = new EventType();
        eventType.setName("Concert");
        eventType = eventTypeRepository.save(eventType);
    }

    @Test
    void createEvent_ValidData_ShouldCreateSuccessfully() throws Exception {
        // Given - Datos válidos para crear evento
        Map<String, Object> eventRequest = new HashMap<>();
        eventRequest.put("title", "Summer Concert");
        eventRequest.put("description", "Amazing summer concert with live music");
        eventRequest.put("date", "2025-08-15");
        eventRequest.put("time", "20:00:00");
        eventRequest.put("price", 25.99);
        eventRequest.put("minimumAge", 18);
        eventRequest.put("imageUrl", "https://example.com/image.jpg");
        eventRequest.put("tags", "music,concert,live");
        
        // Crear objetos de relación con solo el ID
        Map<String, Object> userRef = new HashMap<>();
        userRef.put("id", organizerUser.getId());
        eventRequest.put("user", userRef);
        
        Map<String, Object> establishmentRef = new HashMap<>();
        establishmentRef.put("id", establishment.getId());
        eventRequest.put("establishment", establishmentRef);
        
        Map<String, Object> eventTypeRef = new HashMap<>();
        eventTypeRef.put("id", eventType.getId());
        eventRequest.put("eventType", eventTypeRef);

        // When - Crear evento
        String response = mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Summer Concert"))
                .andExpect(jsonPath("$.description").value("Amazing summer concert with live music"))
                .andExpect(jsonPath("$.price").value(25.99))
                .andExpect(jsonPath("$.minimumAge").value(18))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Then - Verificar que se guardó en base de datos
        @SuppressWarnings("unchecked")
        Map<String, Object> eventResponse = objectMapper.readValue(response, Map.class);
        Long eventId = Long.valueOf((Integer) eventResponse.get("id"));
        
        Event savedEvent = eventRepository.findById(eventId).orElse(null);
        assertNotNull(savedEvent);
        assertEquals("Summer Concert", savedEvent.getTitle());
        assertEquals(BigDecimal.valueOf(25.99), savedEvent.getPrice());
        assertEquals(18, savedEvent.getMinimumAge());
        assertEquals(establishment.getId(), savedEvent.getEstablishment().getId());
        assertEquals(eventType.getId(), savedEvent.getEventType().getId());
    }

    @Test
    void getAllEvents_ShouldReturnAllEvents() throws Exception {
        Event event1 = createTestEvent("Event 1", LocalDate.of(2025, 8, 15));
        Event event2 = createTestEvent("Event 2", LocalDate.of(2025, 8, 20));
        eventRepository.save(event1);
        eventRepository.save(event2);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].title").exists())
                .andExpect(jsonPath("$[1].title").exists());
    }

    @Test
    void getEventById_ExistingId_ShouldReturnEvent() throws Exception {
        // Given - Evento existente
        Event event = createTestEvent("Test Event", LocalDate.of(2025, 8, 15));
        event = eventRepository.save(event);

        // When - Buscar por ID
        mockMvc.perform(get("/api/events/" + event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.title").value("Test Event"));
    }

    @Test
    void updateEvent_ValidData_ShouldUpdateSuccessfully() throws Exception {
        // Given - Evento existente
        Event event = createTestEvent("Original Title", LocalDate.of(2025, 8, 15));
        event = eventRepository.save(event);

        // When - Actualizar evento
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("title", "Updated Title");
        updateRequest.put("description", "Updated description");
        updateRequest.put("date", "2025-08-16");
        updateRequest.put("time", "21:00:00");
        updateRequest.put("price", 30.00);
        updateRequest.put("minimumAge", 21);
        updateRequest.put("imageUrl", "https://example.com/updated-image.jpg");
        updateRequest.put("tags", "music,concert,live,updated");
        updateRequest.put("userId", organizerUser.getId());
        updateRequest.put("establishmentId", establishment.getId());
        updateRequest.put("eventTypeId", eventType.getId());

        mockMvc.perform(put("/api/events/" + event.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"))
                .andExpect(jsonPath("$.price").value(30.00))
                .andExpect(jsonPath("$.minimumAge").value(21));

        // Then - Verificar actualización en base de datos
        Event updatedEvent = eventRepository.findById(event.getId()).orElse(null);
        assertNotNull(updatedEvent);
        assertEquals("Updated Title", updatedEvent.getTitle());
        assertEquals(BigDecimal.valueOf(30.00), updatedEvent.getPrice());
        assertEquals(21, updatedEvent.getMinimumAge());
    }

    @Test
    void deleteEvent_ExistingId_ShouldDeleteSuccessfully() throws Exception {
        // Given - Evento existente
        Event event = createTestEvent("Event to Delete", LocalDate.of(2025, 8, 15));
        event = eventRepository.save(event);
        Long eventId = event.getId();

        // When - Eliminar evento
        mockMvc.perform(delete("/api/events/" + eventId))
                .andExpect(status().isOk());

        // Then - Verificar que se eliminó de la base de datos
        assertFalse(eventRepository.findById(eventId).isPresent());
    }

    @Test
    void getEventsByEstablishment_ShouldReturnCorrectEvents() throws Exception {
        // Given - Eventos de un establishment específico
        Event event1 = createTestEvent("Event 1", LocalDate.of(2025, 8, 15));
        Event event2 = createTestEvent("Event 2", LocalDate.of(2025, 8, 20));
        eventRepository.save(event1);
        eventRepository.save(event2);

        // When - Buscar eventos por establishment
        mockMvc.perform(get("/api/events/establishments/" + establishment.getId() + "/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].establishment.id").value(establishment.getId().intValue()))
                .andExpect(jsonPath("$[1].establishment.id").value(establishment.getId().intValue()));
    }

    private Event createTestEvent(String title, LocalDate eventDate) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription("Test event description");
        event.setDate(eventDate);
        event.setTime(LocalTime.of(20, 0));
        event.setPrice(BigDecimal.valueOf(25.00));
        event.setMinimumAge(18);
        event.setImageUrl("https://example.com/test-image.jpg");
        event.setTags("test,event");
        event.setUser(organizerUser);
        event.setEstablishment(establishment);
        event.setEventType(eventType);
        return event;
    }
}
