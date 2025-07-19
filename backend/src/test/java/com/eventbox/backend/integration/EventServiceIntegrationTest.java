package com.eventbox.backend.integration;

import com.eventbox.backend.model.*;
import com.eventbox.backend.repository.*;
import com.eventbox.backend.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para EventService que validan el comportamiento real
 * con base de datos y verifican las relaciones entre eventos y otras entidades.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class EventServiceIntegrationTest {

    @Autowired
    private EventService eventService;

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
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CommentRepository commentRepository;

    private Role organizerRole;
    private Role userRole;
    private User organizer;
    private User regularUser;
    private EventType concertType;
    private EventType theaterType;
    private Province testProvince;
    private Locality testLocality;
    private Establishment testEstablishment;

    @BeforeEach
    void setUp() {
        // Crear roles
        organizerRole = new Role();
        organizerRole.setName("ORGANIZER");
        organizerRole = roleRepository.save(organizerRole);

        userRole = new Role();
        userRole.setName("USER");
        userRole = roleRepository.save(userRole);

        // Crear usuarios
        organizer = new User();
        organizer.setEmail("organizer@test.com");
        organizer.setName("Test Organizer");
        organizer.setPassword("password123");
        organizer.setRole(organizerRole);
        organizer = userRepository.save(organizer);

        regularUser = new User();
        regularUser.setEmail("user@test.com");
        regularUser.setName("Regular User");
        regularUser.setPassword("password123");
        regularUser.setRole(userRole);
        regularUser = userRepository.save(regularUser);

        // Crear tipos de evento
        concertType = new EventType();
        concertType.setName("Concert");
        concertType = eventTypeRepository.save(concertType);

        theaterType = new EventType();
        theaterType.setName("Theater");
        theaterType = eventTypeRepository.save(theaterType);

        // Crear datos geográficos
        testProvince = new Province();
        testProvince.setName("Test Province");
        testProvince = provinceRepository.save(testProvince);

        testLocality = new Locality();
        testLocality.setName("Test City");
        testLocality.setProvince(testProvince);
        testLocality = localityRepository.save(testLocality);

        // Crear establecimiento
        testEstablishment = new Establishment();
        testEstablishment.setName("Test Venue");
        testEstablishment.setAddress("Test Address");
        testEstablishment.setEmail("venue@test.com");
        testEstablishment.setPhone("+1234567890");
        testEstablishment.setLocality(testLocality);
        testEstablishment.setUser(organizer);
        testEstablishment = establishmentRepository.save(testEstablishment);
    }

    @Test
    void getAllOrderedByDate_ShouldReturnEventsInCorrectChronologicalOrder() {
        // Given - Crear eventos en orden aleatorio
        Event futureEvent = createEvent("Future Event", LocalDate.now().plusDays(10), LocalTime.of(20, 0));
        Event todayEvent = createEvent("Today Event", LocalDate.now(), LocalTime.of(18, 0));
        Event nearFutureEvent = createEvent("Near Future Event", LocalDate.now().plusDays(5), LocalTime.of(19, 0));
        Event laterTodayEvent = createEvent("Later Today Event", LocalDate.now(), LocalTime.of(22, 0));

        eventRepository.save(futureEvent);
        eventRepository.save(todayEvent);
        eventRepository.save(nearFutureEvent);
        eventRepository.save(laterTodayEvent);

        // When - Obtener eventos ordenados
        List<Event> orderedEvents = eventService.getAllOrderedByDate();

        // Then - Verificar orden correcto (fecha ASC, luego tiempo ASC)
        assertEquals(4, orderedEvents.size());
        
        // Verificar orden cronológico
        assertEquals("Today Event", orderedEvents.get(0).getTitle());
        assertEquals("Later Today Event", orderedEvents.get(1).getTitle());
        assertEquals("Near Future Event", orderedEvents.get(2).getTitle());
        assertEquals("Future Event", orderedEvents.get(3).getTitle());

        // Verificar orden de tiempo para el mismo día
        assertEquals(LocalTime.of(18, 0), orderedEvents.get(0).getTime());
        assertEquals(LocalTime.of(22, 0), orderedEvents.get(1).getTime());
    }

    @Test
    void saveEvent_ShouldPersistWithAllRelationshipsCorrectly() {
        // Given - Evento nuevo con todas las relaciones
        Event newEvent = new Event();
        newEvent.setTitle("Integration Test Event");
        newEvent.setDescription("Event created for integration testing");
        newEvent.setDate(LocalDate.now().plusDays(7));
        newEvent.setTime(LocalTime.of(19, 30));
        newEvent.setPrice(new BigDecimal("35.50"));
        newEvent.setMinimumAge(21);
        newEvent.setImageUrl("https://example.com/event-image.jpg");
        newEvent.setTags("integration,testing,concert");
        newEvent.setEventType(concertType);
        newEvent.setEstablishment(testEstablishment);
        newEvent.setUser(organizer);

        // When - Guardar evento
        Event savedEvent = eventService.save(newEvent);

        // Then - Verificar persistencia correcta
        assertNotNull(savedEvent.getId());
        assertEquals("Integration Test Event", savedEvent.getTitle());
        assertEquals("Event created for integration testing", savedEvent.getDescription());
        assertEquals(new BigDecimal("35.50"), savedEvent.getPrice());
        assertEquals(21, savedEvent.getMinimumAge());

        // Verificar relaciones
        assertNotNull(savedEvent.getEventType());
        assertEquals("Concert", savedEvent.getEventType().getName());
        assertEquals(concertType.getId(), savedEvent.getEventType().getId());

        assertNotNull(savedEvent.getEstablishment());
        assertEquals("Test Venue", savedEvent.getEstablishment().getName());
        assertEquals(testEstablishment.getId(), savedEvent.getEstablishment().getId());

        assertNotNull(savedEvent.getUser());
        assertEquals("Test Organizer", savedEvent.getUser().getName());
        assertEquals(organizer.getId(), savedEvent.getUser().getId());

        // Verificar que se guardó en base de datos
        Optional<Event> retrievedEvent = eventRepository.findById(savedEvent.getId());
        assertTrue(retrievedEvent.isPresent());
        assertEquals(savedEvent.getTitle(), retrievedEvent.get().getTitle());
    }

    @Test
    void deleteEvent_ShouldRemoveEventAndRelatedDataProperly() {
        // Given - Evento con datos relacionados
        Event event = createEvent("Event to Delete", LocalDate.now().plusDays(15), LocalTime.of(20, 0));
        event = eventRepository.save(event);

        // Crear favorito
        Favorite favorite = new Favorite();
        favorite.setUser(regularUser);
        favorite.setEvent(event);
        favorite = favoriteRepository.save(favorite);

        // Crear comentario
        Comment comment = new Comment();
        comment.setDescription("Looking forward to this event!");
        comment.setDate(java.time.LocalDateTime.now());
        comment.setUser(regularUser);
        comment.setEvent(event);
        comment = commentRepository.save(comment);

        final Long eventId = event.getId();

        // Verificar que existen antes de eliminar
        assertTrue(eventRepository.findById(eventId).isPresent());
        assertEquals(1, favoriteRepository.count());
        assertEquals(1, commentRepository.count());

        // When - Eliminar evento
        eventService.delete(eventId);

        // Then - Verificar eliminación completa
        assertFalse(eventRepository.findById(eventId).isPresent());

        // Verificar que favoritos y comentarios relacionados se eliminaron
        // (dependiendo de la configuración de cascada o eliminación manual)
        List<Favorite> remainingFavorites = favoriteRepository.findAll().stream()
                .filter(f -> f.getEvent().getId().equals(eventId))
                .toList();
        assertEquals(0, remainingFavorites.size());

        List<Comment> remainingComments = commentRepository.findAll().stream()
                .filter(c -> c.getEvent().getId().equals(eventId))
                .toList();
        assertEquals(0, remainingComments.size());
    }

    @Test
    void getEventsByEstablishment_ShouldReturnOnlyEventsFromSpecificEstablishment() {
        // Given - Eventos en diferentes establecimientos
        Event event1 = createEvent("Event at Test Venue", LocalDate.now().plusDays(5), LocalTime.of(19, 0));
        event1.setEstablishment(testEstablishment);
        event1 = eventRepository.save(event1);

        // Crear otro establecimiento
        Establishment anotherEstablishment = new Establishment();
        anotherEstablishment.setName("Another Venue");
        anotherEstablishment.setAddress("Another Address");
        anotherEstablishment.setEmail("another@test.com");
        anotherEstablishment.setPhone("+0987654321");
        anotherEstablishment.setLocality(testLocality);
        anotherEstablishment.setUser(organizer);
        anotherEstablishment = establishmentRepository.save(anotherEstablishment);

        Event event2 = createEvent("Event at Another Venue", LocalDate.now().plusDays(8), LocalTime.of(20, 0));
        event2.setEstablishment(anotherEstablishment);
        event2 = eventRepository.save(event2);

        // When - Buscar eventos por establecimiento
        List<Event> eventsAtTestVenue = eventService.findByEstablishmentId(testEstablishment.getId());

        // Then - Verificar que solo retorna eventos del establecimiento correcto
        assertEquals(1, eventsAtTestVenue.size());
        assertEquals("Event at Test Venue", eventsAtTestVenue.get(0).getTitle());
        assertEquals(testEstablishment.getId(), eventsAtTestVenue.get(0).getEstablishment().getId());
    }

    @Test
    void getAllEvents_ShouldReturnAllEventsWithCompleteInformation() {
        // Given - Múltiples eventos con diferentes características
        Event concertEvent = createEvent("Rock Concert", LocalDate.now().plusDays(3), LocalTime.of(21, 0));
        concertEvent.setEventType(concertType);
        concertEvent.setPrice(new BigDecimal("45.00"));
        concertEvent.setMinimumAge(18);
        concertEvent = eventRepository.save(concertEvent);

        Event theaterEvent = createEvent("Theater Play", LocalDate.now().plusDays(7), LocalTime.of(19, 0));
        theaterEvent.setEventType(theaterType);
        theaterEvent.setPrice(new BigDecimal("25.00"));
        theaterEvent.setMinimumAge(12);
        theaterEvent = eventRepository.save(theaterEvent);

        // When - Obtener todos los eventos
        List<Event> allEvents = eventService.getAll();

        // Then - Verificar que se obtuvieron todos con información completa
        assertEquals(2, allEvents.size());

        // Verificar que tienen toda la información cargada (no lazy loading issues)
        for (Event event : allEvents) {
            assertNotNull(event.getEventType());
            assertNotNull(event.getEstablishment());
            assertNotNull(event.getUser());
            assertNotNull(event.getEstablishment().getLocality());
            assertNotNull(event.getEstablishment().getLocality().getProvince());
        }

        // Verificar eventos específicos
        Event retrievedConcert = allEvents.stream()
                .filter(e -> e.getTitle().equals("Rock Concert"))
                .findFirst().orElseThrow();
        assertEquals("Concert", retrievedConcert.getEventType().getName());
        assertEquals(new BigDecimal("45.00"), retrievedConcert.getPrice());
        assertEquals(18, retrievedConcert.getMinimumAge());

        Event retrievedTheater = allEvents.stream()
                .filter(e -> e.getTitle().equals("Theater Play"))
                .findFirst().orElseThrow();
        assertEquals("Theater", retrievedTheater.getEventType().getName());
        assertEquals(new BigDecimal("25.00"), retrievedTheater.getPrice());
        assertEquals(12, retrievedTheater.getMinimumAge());
    }

    @Test
    void getEventById_ShouldReturnEventWithAllRelationshipsLoaded() {
        // Given - Evento guardado
        Event event = createEvent("Complete Event", LocalDate.now().plusDays(10), LocalTime.of(20, 30));
        event = eventRepository.save(event);

        // When - Buscar por ID
        Optional<Event> foundEvent = eventService.getById(event.getId());

        // Then - Verificar que se encontró con todas las relaciones
        assertTrue(foundEvent.isPresent());
        Event retrievedEvent = foundEvent.get();

        assertEquals("Complete Event", retrievedEvent.getTitle());
        
        // Verificar que todas las relaciones están cargadas
        assertNotNull(retrievedEvent.getEventType());
        assertEquals("Concert", retrievedEvent.getEventType().getName());

        assertNotNull(retrievedEvent.getEstablishment());
        assertEquals("Test Venue", retrievedEvent.getEstablishment().getName());
        assertNotNull(retrievedEvent.getEstablishment().getLocality());
        assertEquals("Test City", retrievedEvent.getEstablishment().getLocality().getName());

        assertNotNull(retrievedEvent.getUser());
        assertEquals("Test Organizer", retrievedEvent.getUser().getName());
        assertNotNull(retrievedEvent.getUser().getRole());
        assertEquals("ORGANIZER", retrievedEvent.getUser().getRole().getName());
    }

    /**
     * Método helper para crear eventos de prueba
     */
    private Event createEvent(String title, LocalDate date, LocalTime time) {
        Event event = new Event();
        event.setTitle(title);
        event.setDescription("Test event description for " + title);
        event.setDate(date);
        event.setTime(time);
        event.setPrice(new BigDecimal("30.00"));
        event.setMinimumAge(16);
        event.setImageUrl("https://example.com/image.jpg");
        event.setTags("test,integration");
        event.setEventType(concertType);
        event.setEstablishment(testEstablishment);
        event.setUser(organizer);
        return event;
    }
}
