package com.eventbox.backend.config;

import com.eventbox.backend.model.Event;
import com.eventbox.backend.repository.EventRepository;
import com.eventbox.backend.service.EventSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElasticsearchInitializer implements CommandLineRunner {

    private final EventRepository eventRepository;
    private final EventSearchService eventSearchService;

    @Autowired
    public ElasticsearchInitializer(EventRepository eventRepository, EventSearchService eventSearchService) {
        this.eventRepository = eventRepository;
        this.eventSearchService = eventSearchService;
    }

    @Override
    public void run(String... args) {
        // Indexar todos los eventos existentes en Elasticsearch al iniciar la aplicación
        List<Event> allEvents = eventRepository.findAll();
        for (Event event : allEvents) {
            eventSearchService.indexEvent(event);
        }
    }
}
