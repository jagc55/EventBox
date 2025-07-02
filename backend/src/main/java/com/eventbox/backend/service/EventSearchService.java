package com.eventbox.backend.service;

import com.eventbox.backend.model.Event;
import com.eventbox.backend.model.elasticsearch.EventDocument;
import com.eventbox.backend.repository.elasticsearch.EventSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventSearchService {

    private final EventSearchRepository eventSearchRepository;

    @Autowired
    public EventSearchService(EventSearchRepository eventSearchRepository) {
        this.eventSearchRepository = eventSearchRepository;
    }

    public void indexEvent(Event event) {
        EventDocument eventDocument = mapToEventDocument(event);
        eventSearchRepository.save(eventDocument);
    }

    public void deleteEventIndex(String id) {
        eventSearchRepository.deleteById(id);
    }

    public List<EventDocument> searchEvents(String query) {
        return eventSearchRepository.findByTitleContainingOrDescriptionContainingOrTagsContaining(
                query, query, query);
    }

    private EventDocument mapToEventDocument(Event event) {
        return new EventDocument(
                event.getId().toString(),
                event.getTitle(),
                event.getDescription(),
                event.getDate(),
                event.getTime().toString(),
                event.getPrice(),
                event.getMinimumAge(),
                event.getImageUrl(),
                event.getTags(),
                event.getEventType().getName(),
                event.getEstablishment().getName(),
                event.getEstablishment().getAddress(),
                event.getUser().getName()
        );
    }
}
