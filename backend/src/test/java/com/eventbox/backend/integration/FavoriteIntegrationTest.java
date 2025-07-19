package com.eventbox.backend.integration;

import com.eventbox.backend.model.*;
import com.eventbox.backend.repository.*;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para el flujo completo de gestión de favoritos
 * que validan Controller + Service + Repository + Base de datos
 */
@SpringBootTest
@AutoConfigureWebMvc
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class FavoriteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FavoriteRepository favoriteRepository;

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
    private ProvinceRepository provinceRepository;

    @Autowired
    private LocalityRepository localityRepository;


    private User testUser;
    private Event testEvent;

    @BeforeEach
    void setUp() {
        // Crear rol usuario
        Role userRole = new Role();
        userRole.setName("USER");
        userRole = roleRepository.save(userRole);

        // Crear usuario
        testUser = new User();
        testUser.setEmail("user@test.com");
        testUser.setName("Test User");
        testUser.setPassword("password123");
        testUser.setRole(userRole);
        testUser = userRepository.save(testUser);

        // Crear datos necesarios para el evento
        Role organizerRole = new Role();
        organizerRole.setName("ORGANIZER");
        organizerRole = roleRepository.save(organizerRole);

        User organizerUser = new User();
        organizerUser.setEmail("organizer@test.com");
        organizerUser.setName("Test Organizer");
        organizerUser.setPassword("password123");
        organizerUser.setRole(organizerRole);
        organizerUser = userRepository.save(organizerUser);

        // Crear provincia y localidad
        Province province = new Province();
        province.setName("Test Province");
        province = provinceRepository.save(province);

        Locality locality = new Locality();
        locality.setName("Test City");
        locality.setProvince(province);
        locality = localityRepository.save(locality);

        Establishment establishment = new Establishment();
        establishment.setName("Test Venue");
        establishment.setAddress("123 Test Street");
        establishment.setPhone("+34666777888");
        establishment.setEmail("venue@test.com");
        establishment.setLocality(locality);
        establishment.setUser(organizerUser);
        establishment = establishmentRepository.save(establishment);

        EventType eventType = new EventType();
        eventType.setName("Concert");
        eventType = eventTypeRepository.save(eventType);

        // Crear evento
        testEvent = new Event();
        testEvent.setTitle("Test Event");
        testEvent.setDescription("A great test event");
        testEvent.setDate(LocalDate.now().plusDays(30));
        testEvent.setTime(LocalTime.of(20, 0));
        testEvent.setPrice(new BigDecimal("25.0"));
        testEvent.setMinimumAge(18);
        testEvent.setImageUrl("https://example.com/image.jpg");
        testEvent.setTags("music,concert");
        testEvent.setEventType(eventType);
        testEvent.setEstablishment(establishment);
        testEvent.setUser(organizerUser);
        testEvent = eventRepository.save(testEvent);
    }

    @Test
    void addToFavorites_ValidData_ShouldCreateSuccessfully() throws Exception {
        // Given - Datos válidos para agregar a favoritos
        Long userId = testUser.getId();
        Long eventId = testEvent.getId();

        // When - Crear favorito
        mockMvc.perform(post("/api/favorites/user/" + userId + "/event/" + eventId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evento añadido a favoritos correctamente"));

        // Then - Verificar que se guardó en base de datos
        FavoriteId favoriteId = new FavoriteId(userId, eventId);
        Favorite savedFavorite = favoriteRepository.findById(favoriteId).orElse(null);
        assertNotNull(savedFavorite);
        assertEquals(userId, savedFavorite.getUser().getId());
        assertEquals(eventId, savedFavorite.getEvent().getId());
    }

    @Test
    void getFavoritesByUser_ExistingUser_ShouldReturnFavorites() throws Exception {
        // Given - Usuario con favoritos
        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setEvent(testEvent);
        favoriteRepository.save(favorite);

        // When & Then - Obtener favoritos del usuario
        mockMvc.perform(get("/api/favorites/user/" + testUser.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].user.id").value(testUser.getId()))
                .andExpect(jsonPath("$[0].event.id").value(testEvent.getId()));
    }

    @Test
    void removeFavorite_ExistingId_ShouldDeleteSuccessfully() throws Exception {
        // Given - Favorito existente
        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setEvent(testEvent);
        favorite = favoriteRepository.save(favorite);
        FavoriteId favoriteId = new FavoriteId(testUser.getId(), testEvent.getId());

        // When - Eliminar favorito
        mockMvc.perform(delete("/api/favorites/user/" + testUser.getId() + "/event/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evento eliminado de favoritos correctamente"));

        // Then - Verificar que se eliminó de la base de datos
        assertFalse(favoriteRepository.findById(favoriteId).isPresent());
    }

    @Test
    void removeFavoriteByUserAndEvent_ShouldDeleteSuccessfully() throws Exception {
        // Given - Favorito existente
        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setEvent(testEvent);
        favoriteRepository.save(favorite);

        // When - Eliminar favorito por usuario y evento
        mockMvc.perform(delete("/api/favorites/user/" + testUser.getId() + "/event/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Evento eliminado de favoritos correctamente"));

        // Then - Verificar que se eliminó
        FavoriteId favoriteId = new FavoriteId(testUser.getId(), testEvent.getId());
        assertFalse(favoriteRepository.findById(favoriteId).isPresent());
    }

    @Test
    void addDuplicateFavorite_ShouldHandleGracefully() throws Exception {
        // Given - Favorito ya existente
        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setEvent(testEvent);
        favoriteRepository.save(favorite);

        // When - Intentar agregar el mismo favorito
        mockMvc.perform(post("/api/favorites/user/" + testUser.getId() + "/event/" + testEvent.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void isFavorite_ExistingFavorite_ShouldReturnTrue() throws Exception {
        // Given - Favorito existente
        Favorite favorite = new Favorite();
        favorite.setUser(testUser);
        favorite.setEvent(testEvent);
        favoriteRepository.save(favorite);

        // When & Then - Verificar si es favorito
        mockMvc.perform(get("/api/favorites/user/" + testUser.getId() + "/event/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void isFavorite_NonExistingFavorite_ShouldReturnFalse() throws Exception {
        // When & Then - Verificar si es favorito (no debería serlo)
        mockMvc.perform(get("/api/favorites/user/" + testUser.getId() + "/event/" + testEvent.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(false));
    }
}
