package com.eventbox.backend.service;

import com.eventbox.backend.model.Event;
import com.eventbox.backend.model.User;
import com.eventbox.backend.model.Establishment;
import com.eventbox.backend.model.EventType;
import com.eventbox.backend.repository.EventRepository;
import com.eventbox.backend.repository.FavoriteRepository;
import com.eventbox.backend.repository.CommentRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private EventService eventService;

    private Event testEvent;
    private User testOrganizer;
    private Establishment testEstablishment;
    private EventType testEventType;

    @BeforeEach
    void setUp() {
        testOrganizer = new User();
        testOrganizer.setId(1L);
        testOrganizer.setName("Test Organizer");
        testOrganizer.setEmail("organizer@test.com");

        testEstablishment = new Establishment();
        testEstablishment.setId(1L);
        testEstablishment.setName("Test Venue");

        testEventType = new EventType();
        testEventType.setId(1L);
        testEventType.setName("Concert");

        testEvent = new Event();
        testEvent.setId(1L);
        testEvent.setTitle("Test Event");
        testEvent.setDescription("Test Description");
        testEvent.setDate(LocalDate.now().plusDays(7));
        testEvent.setTime(LocalTime.of(20, 0));
        testEvent.setPrice(new BigDecimal("25.00"));
        testEvent.setMinimumAge(18);
        testEvent.setImageUrl("https://example.com/image.jpg");
        testEvent.setTags("music,concert,rock");
        testEvent.setUser(testOrganizer);
        testEvent.setEstablishment(testEstablishment);
        testEvent.setEventType(testEventType);
    }

    @Test
    void getAll_ShouldReturnListOfEvents() {
        // Given
        List<Event> expectedEvents = Arrays.asList(testEvent);
        when(eventRepository.findAll()).thenReturn(expectedEvents);

        // When
        List<Event> actualEvents = eventService.getAll();

        // Then
        assertEquals(expectedEvents, actualEvents);
        verify(eventRepository).findAll();
    }

    @Test
    void getAllOrderedByDate_ShouldReturnEventsOrderedByDate() {
        // Given
        Event futureEvent = new Event();
        futureEvent.setId(2L);
        futureEvent.setTitle("Future Event");
        futureEvent.setDate(LocalDate.now().plusDays(14));
        futureEvent.setTime(LocalTime.of(19, 0));

        List<Event> expectedEvents = Arrays.asList(testEvent, futureEvent);
        when(eventRepository.findAllByOrderByDateAscTimeAsc()).thenReturn(expectedEvents);

        // When
        List<Event> actualEvents = eventService.getAllOrderedByDate();

        // Then
        assertEquals(expectedEvents, actualEvents);
        verify(eventRepository).findAllByOrderByDateAscTimeAsc();
    }

    @Test
    void getById_WhenEventExists_ShouldReturnEvent() {
        // Given
        Long eventId = 1L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.of(testEvent));

        // When
        Optional<Event> result = eventService.getById(eventId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testEvent, result.get());
        verify(eventRepository).findById(eventId);
    }

    @Test
    void getById_WhenEventNotExists_ShouldReturnEmpty() {
        // Given
        Long eventId = 999L;
        when(eventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When
        Optional<Event> result = eventService.getById(eventId);

        // Then
        assertFalse(result.isPresent());
        verify(eventRepository).findById(eventId);
    }

    @Test
    void save_ShouldReturnSavedEvent() {
        // Given
        when(eventRepository.save(testEvent)).thenReturn(testEvent);

        // When
        Event savedEvent = eventService.save(testEvent);

        // Then
        assertEquals(testEvent, savedEvent);
        verify(eventRepository).save(testEvent);
    }

    @Test
    void delete_ShouldDeleteEventAndRelatedData() {
        // Given
        Long eventId = 1L;

        // When
        eventService.delete(eventId);

        // Then
        verify(favoriteRepository).deleteByEventId(eventId);
        verify(commentRepository).deleteByEventId(eventId);
        verify(eventRepository).deleteById(eventId);
    }

    @Test
    void findByEstablishmentId_ShouldReturnEventsForEstablishment() {
        // Given
        Long establishmentId = 1L;
        List<Event> expectedEvents = Arrays.asList(testEvent);
        when(eventRepository.findByEstablishmentId(establishmentId)).thenReturn(expectedEvents);

        // When
        List<Event> actualEvents = eventService.findByEstablishmentId(establishmentId);

        // Then
        assertEquals(expectedEvents, actualEvents);
        verify(eventRepository).findByEstablishmentId(establishmentId);
    }

    @Test
    void save_WhenEventHasInvalidData_ShouldHandleValidation() {
        // Given
        Event invalidEvent = new Event();
        // Event without required fields
        when(eventRepository.save(invalidEvent)).thenThrow(new IllegalArgumentException("Event validation failed"));

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> eventService.save(invalidEvent));
        verify(eventRepository).save(invalidEvent);
    }

    @Test
    void delete_WhenEventNotExists_ShouldStillExecuteCleanup() {
        // Given
        Long nonExistentEventId = 999L;
        doNothing().when(favoriteRepository).deleteByEventId(nonExistentEventId);
        doNothing().when(commentRepository).deleteByEventId(nonExistentEventId);
        doNothing().when(eventRepository).deleteById(nonExistentEventId);

        // When
        eventService.delete(nonExistentEventId);

        // Then
        verify(favoriteRepository).deleteByEventId(nonExistentEventId);
        verify(commentRepository).deleteByEventId(nonExistentEventId);
        verify(eventRepository).deleteById(nonExistentEventId);
    }
}
