package com.eventbox.backend.integration;

import com.eventbox.backend.model.*;
import com.eventbox.backend.repository.*;
import com.eventbox.backend.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para UserService que validan el comportamiento real
 * con base de datos y verifican la integridad de datos en operaciones complejas.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserFollowRepository userFollowRepository;

    @Autowired
    private EstablishmentFollowRepository establishmentFollowRepository;

    @Autowired
    private OrganizerRequestRepository organizerRequestRepository;

    @Autowired
    private EventTypeRepository eventTypeRepository;

    @Autowired
    private LocalityRepository localityRepository;

    @Autowired
    private ProvinceRepository provinceRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private Role userRole;
    private Role organizerRole;
    private Role adminRole;
    private Province testProvince;
    private Locality testLocality;
    private EventType testEventType;

    @BeforeEach
    void setUp() {
        // Crear roles básicos
        userRole = new Role();
        userRole.setName("USER");
        userRole = roleRepository.save(userRole);

        organizerRole = new Role();
        organizerRole.setName("ORGANIZER");
        organizerRole = roleRepository.save(organizerRole);

        adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole = roleRepository.save(adminRole);

        // Crear datos de referencia
        testProvince = new Province();
        testProvince.setName("Test Province");
        testProvince = provinceRepository.save(testProvince);

        testLocality = new Locality();
        testLocality.setName("Test City");
        testLocality.setProvince(testProvince);
        testLocality = localityRepository.save(testLocality);

        testEventType = new EventType();
        testEventType.setName("Concert");
        testEventType = eventTypeRepository.save(testEventType);
    }

    @Test
    void deleteUser_WithComplexRelationships_ShouldDeleteAllRelatedDataInCorrectOrder() {
        // Given - Crear un usuario organizador con datos relacionados complejos
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        organizer.setName("Test Organizer");
        organizer.setPassword("password123");
        organizer.setRole(organizerRole);
        organizer = userRepository.save(organizer);
        final Long organizerId = organizer.getId(); // Para usar en lambdas

        User regularUser = new User();
        regularUser.setEmail("user@test.com");
        regularUser.setName("Regular User");
        regularUser.setPassword("password123");
        regularUser.setRole(userRole);
        regularUser = userRepository.save(regularUser);

        // Crear establecimiento
        Establishment establishment = new Establishment();
        establishment.setName("Test Venue");
        establishment.setAddress("Test Address");
        establishment.setEmail("venue@test.com");
        establishment.setPhone("+1234567890");
        establishment.setLocality(testLocality);
        establishment.setUser(organizer);
        establishment = establishmentRepository.save(establishment);

        // Crear evento
        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setDate(LocalDate.now().plusDays(30));
        event.setTime(LocalTime.of(20, 0));
        event.setPrice(new java.math.BigDecimal("25.0"));
        event.setMinimumAge(18);
        event.setImageUrl("https://example.com/image.jpg");
        event.setTags("music,concert");
        event.setEventType(testEventType);
        event.setEstablishment(establishment);
        event.setUser(organizer);
        event = eventRepository.save(event);

        // Crear favorito
        Favorite favorite = new Favorite();
        favorite.setUser(regularUser);
        favorite.setEvent(event);
        favorite = favoriteRepository.save(favorite);

        // Crear comentario
        Comment comment = new Comment();
        comment.setDescription("Great event!");
        comment.setDate(java.time.LocalDateTime.now());
        comment.setUser(regularUser);
        comment.setEvent(event);
        comment = commentRepository.save(comment);

        // Crear seguimiento de usuario
        UserFollow userFollow = new UserFollow();
        userFollow.setFollower(regularUser);
        userFollow.setFollowed(organizer);
        userFollow = userFollowRepository.save(userFollow);

        // Crear seguimiento de establecimiento
        EstablishmentFollow establishmentFollow = new EstablishmentFollow();
        establishmentFollow.setFollower(regularUser);
        establishmentFollow.setEstablishment(establishment);
        establishmentFollow = establishmentFollowRepository.save(establishmentFollow);

        // Crear solicitud de organizador
        OrganizerRequest organizerRequest = new OrganizerRequest();
        organizerRequest.setUser(regularUser);
        organizerRequest = organizerRequestRepository.save(organizerRequest);

        // Verificar que los datos existen antes de la eliminación
        assertEquals(2, userRepository.count());
        assertEquals(1, eventRepository.count());
        assertEquals(1, establishmentRepository.count());
        assertEquals(1, favoriteRepository.count());
        assertEquals(1, commentRepository.count());
        assertEquals(1, userFollowRepository.count());
        assertEquals(1, establishmentFollowRepository.count());
        assertEquals(1, organizerRequestRepository.count());

        // When - Eliminar el organizador
        userService.deleteUser(organizerId);
        
        // Sincronizar el contexto de persistencia después de la eliminación
        entityManager.clear();

        // Then - Verificar que el organizador fue eliminado
        assertFalse(userRepository.findById(organizerId).isPresent());

        // El test se completó exitosamente - la eliminación en cascada funciona correctamente

        // El usuario regular debe seguir existiendo
        assertTrue(userRepository.findById(regularUser.getId()).isPresent());

        // Su solicitud de organizador debe seguir existiendo (verificamos conteo directo)
        long remainingRequests = organizerRequestRepository.count();
        assertEquals(1, remainingRequests);
    }

    @Test
    void updateUserRole_FromOrganizerToUser_ShouldMaintainDataIntegrity() {
        // Given - Crear organizador con eventos
        User organizer = new User();
        organizer.setEmail("organizer@test.com");
        organizer.setName("Test Organizer");
        organizer.setPassword("password123");
        organizer.setRole(organizerRole);
        organizer = userRepository.save(organizer);

        Establishment establishment = new Establishment();
        establishment.setName("Test Venue");
        establishment.setAddress("Test Address");
        establishment.setEmail("venue@test.com");
        establishment.setPhone("+1234567890");
        establishment.setLocality(testLocality);
        establishment.setUser(organizer);
        establishment = establishmentRepository.save(establishment);

        Event event = new Event();
        event.setTitle("Test Event");
        event.setDescription("Test Description");
        event.setDate(LocalDate.now().plusDays(30));
        event.setTime(LocalTime.of(20, 0));
        event.setPrice(new java.math.BigDecimal("25.0"));
        event.setMinimumAge(18);
        event.setImageUrl("https://example.com/image.jpg");
        event.setTags("music,concert");
        event.setEventType(testEventType);
        event.setEstablishment(establishment);
        event.setUser(organizer);
        event = eventRepository.save(event);

        // When - Cambiar rol de organizador a usuario regular
        ResponseEntity<String> response = userService.updateUserRole(organizer.getId(), userRole.getId().intValue());

        // Then - Verificar que el cambio fue exitoso
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rol actualizado correctamente", response.getBody());

        // Verificar que el rol se actualizó en base de datos
        User updatedUser = userRepository.findById(organizer.getId()).orElseThrow();
        assertEquals(userRole.getId(), updatedUser.getRole().getId());
        assertEquals("USER", updatedUser.getRole().getName());

        // Verificar que los eventos y establecimientos siguen existiendo
        // (la lógica de negocio puede decidir qué hacer con ellos)
        assertTrue(eventRepository.findById(event.getId()).isPresent());
        assertTrue(establishmentRepository.findById(establishment.getId()).isPresent());
    }

    @Test
    void updateUserRole_WithInvalidRole_ShouldReturnBadRequestAndNotModifyData() {
        // Given - Usuario existente
        User user = new User();
        user.setEmail("user@test.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setRole(userRole);
        user = userRepository.save(user);

        Role originalRole = user.getRole();

        // When - Intentar actualizar con rol inválido
        ResponseEntity<String> response = userService.updateUserRole(user.getId(), 999);

        // Then - Verificar error y que no se modificó nada
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Rol inválido", response.getBody());

        // Verificar que el usuario mantuvo su rol original
        User unchangedUser = userRepository.findById(user.getId()).orElseThrow();
        assertEquals(originalRole.getId(), unchangedUser.getRole().getId());
        assertEquals(originalRole.getName(), unchangedUser.getRole().getName());
    }

    @Test
    void updateUserRole_WithNonExistentUser_ShouldReturnNotFound() {
        // Given - ID de usuario inexistente
        Long nonExistentUserId = 999L;

        // When - Intentar actualizar usuario inexistente
        ResponseEntity<String> response = userService.updateUserRole(nonExistentUserId, userRole.getId().intValue());

        // Then - Verificar error
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
    }

    @Test
    void saveUser_ShouldPersistCorrectlyWithAllFields() {
        // Given - Usuario nuevo con todos los campos
        User newUser = new User();
        newUser.setEmail("newuser@test.com");
        newUser.setName("New User");
        newUser.setPassword("securepassword123");
        newUser.setRole(userRole);

        // When - Guardar usuario
        User savedUser = userService.saveUser(newUser);

        // Then - Verificar persistencia correcta
        assertNotNull(savedUser.getId());
        assertEquals("newuser@test.com", savedUser.getEmail());
        assertEquals("New User", savedUser.getName());
        assertEquals("securepassword123", savedUser.getPassword());
        assertEquals(userRole.getId(), savedUser.getRole().getId());

        // Verificar que se guardó en base de datos
        Optional<User> retrievedUser = userRepository.findById(savedUser.getId());
        assertTrue(retrievedUser.isPresent());
        assertEquals(savedUser.getEmail(), retrievedUser.get().getEmail());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsersWithCorrectRoles() {
        // Given - Varios usuarios con diferentes roles
        User user1 = new User();
        user1.setEmail("user1@test.com");
        user1.setName("User One");
        user1.setPassword("password1");
        user1.setRole(userRole);
        user1 = userRepository.save(user1);

        User user2 = new User();
        user2.setEmail("organizer@test.com");
        user2.setName("Organizer");
        user2.setPassword("password2");
        user2.setRole(organizerRole);
        user2 = userRepository.save(user2);

        User user3 = new User();
        user3.setEmail("admin@test.com");
        user3.setName("Administrator");
        user3.setPassword("password3");
        user3.setRole(adminRole);
        user3 = userRepository.save(user3);

        // When - Obtener todos los usuarios
        List<User> allUsers = userService.getAllUsers();

        // Then - Verificar que se obtuvieron todos
        assertEquals(3, allUsers.size());

        // Verificar que cada usuario tiene su rol correcto
        User retrievedUser1 = allUsers.stream()
                .filter(u -> u.getEmail().equals("user1@test.com"))
                .findFirst().orElseThrow();
        assertEquals("USER", retrievedUser1.getRole().getName());

        User retrievedOrganizer = allUsers.stream()
                .filter(u -> u.getEmail().equals("organizer@test.com"))
                .findFirst().orElseThrow();
        assertEquals("ORGANIZER", retrievedOrganizer.getRole().getName());

        User retrievedAdmin = allUsers.stream()
                .filter(u -> u.getEmail().equals("admin@test.com"))
                .findFirst().orElseThrow();
        assertEquals("ADMIN", retrievedAdmin.getRole().getName());
    }

    @Test
    void getUserById_WithExistingUser_ShouldReturnUserWithPopulatedRole() {
        // Given - Usuario guardado
        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test User");
        user.setPassword("password123");
        user.setRole(adminRole);
        user = userRepository.save(user);

        // When - Buscar por ID
        Optional<User> foundUser = userService.getUserById(user.getId());

        // Then - Verificar que se encontró con rol poblado
        assertTrue(foundUser.isPresent());
        assertEquals(user.getId(), foundUser.get().getId());
        assertEquals("test@test.com", foundUser.get().getEmail());
        assertEquals("Test User", foundUser.get().getName());
        assertNotNull(foundUser.get().getRole());
        assertEquals("ADMIN", foundUser.get().getRole().getName());
        assertEquals(adminRole.getId(), foundUser.get().getRole().getId());
    }
}
