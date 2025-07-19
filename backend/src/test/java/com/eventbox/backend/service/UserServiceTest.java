package com.eventbox.backend.service;

import com.eventbox.backend.model.Role;
import com.eventbox.backend.model.User;
import com.eventbox.backend.repository.UserRepository;
import com.eventbox.backend.repository.RoleRepository;
import com.eventbox.backend.repository.EstablishmentRepository;
import com.eventbox.backend.repository.EventRepository;
import com.eventbox.backend.repository.FavoriteRepository;
import com.eventbox.backend.repository.UserFollowRepository;
import com.eventbox.backend.repository.EstablishmentFollowRepository;
import com.eventbox.backend.repository.CommentRepository;
import com.eventbox.backend.repository.OrganizerRequestRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private EstablishmentRepository establishmentRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private UserFollowRepository userFollowRepository;

    @Mock
    private EstablishmentFollowRepository establishmentFollowRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private OrganizerRequestRepository organizerRequestRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role testRole;

    @BeforeEach
    void setUp() {
        // Inject mocks into the UserService manually for @Autowired fields
        ReflectionTestUtils.setField(userService, "establishmentRepository", establishmentRepository);
        ReflectionTestUtils.setField(userService, "eventRepository", eventRepository);
        ReflectionTestUtils.setField(userService, "favoriteRepository", favoriteRepository);
        ReflectionTestUtils.setField(userService, "userFollowRepository", userFollowRepository);
        ReflectionTestUtils.setField(userService, "establishmentFollowRepository", establishmentFollowRepository);
        ReflectionTestUtils.setField(userService, "commentRepository", commentRepository);
        ReflectionTestUtils.setField(userService, "organizerRequestRepository", organizerRequestRepository);

        testRole = new Role();
        testRole.setId(1L);
        testRole.setName("USER");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");
        testUser.setName("Test User");
        testUser.setPassword("password123");
        testUser.setRole(testRole);
    }

    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Given
        List<User> expectedUsers = Arrays.asList(testUser);
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // When
        List<User> actualUsers = userService.getAllUsers();

        // Then
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnUser() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser, result.get());
        verify(userRepository).findById(userId);
    }

    @Test
    void getUserById_WhenUserNotExists_ShouldReturnEmpty() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.getUserById(userId);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
    }

    @Test
    void saveUser_ShouldReturnSavedUser() {
        // Given
        when(userRepository.save(testUser)).thenReturn(testUser);

        // When
        User savedUser = userService.saveUser(testUser);

        // Then
        assertEquals(testUser, savedUser);
        verify(userRepository).save(testUser);
    }

    @Test
    void deleteUser_ShouldDeleteUserAndRelatedData() {
        Long userId = 1L;

        userService.deleteUser(userId);

        verify(favoriteRepository).deleteByUserId(userId);
        verify(favoriteRepository).deleteByEventOrganizerId(userId);
        verify(commentRepository).deleteByEventOrganizerId(userId);
        verify(userFollowRepository).deleteByUserId(userId);
        verify(establishmentFollowRepository).deleteByUserId(userId);
        verify(establishmentFollowRepository).deleteByEstablishmentOwnerId(userId);
        verify(commentRepository).deleteByUserId(userId);
        verify(organizerRequestRepository).deleteByUserId(userId);
        verify(eventRepository).deleteByOrganizerId(userId);
        verify(establishmentRepository).deleteByUserId(userId);
        verify(userRepository).deleteById(userId);
    }

    @Test
    void updateUserRole_WhenUserExists_ShouldUpdateRole() {
        // Given
        Long userId = 1L;
        Long roleId = 2L;
        Role newRole = new Role();
        newRole.setId(roleId);
        newRole.setName("ORGANIZER");

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(newRole));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        ResponseEntity<String> response = userService.updateUserRole(userId, roleId.intValue());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Rol actualizado correctamente", response.getBody());
        verify(userRepository).findById(userId);
        verify(roleRepository).findById(roleId);
        verify(userRepository).save(testUser);
    }

    @Test
    void updateUserRole_WhenUserNotExists_ShouldReturnNotFound() {
        // Given
        Long userId = 999L;
        Integer roleId = 2;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = userService.updateUserRole(userId, roleId);

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Usuario no encontrado", response.getBody());
        verify(userRepository).findById(userId);
        verify(roleRepository, never()).findById(any(Long.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserRole_WhenRoleNotExists_ShouldReturnBadRequest() {
        // Given
        Long userId = 1L;
        Long roleId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(roleRepository.findById(roleId)).thenReturn(Optional.empty());

        // When
        ResponseEntity<String> response = userService.updateUserRole(userId, roleId.intValue());

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Rol inválido", response.getBody());
        verify(userRepository).findById(userId);
        verify(roleRepository).findById(roleId);
        verify(userRepository, never()).save(any(User.class));
    }
}
