package com.eventbox.backend.controller;

import com.eventbox.backend.model.elasticsearch.EventDocument;
import com.eventbox.backend.service.EventSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final EventSearchService eventSearchService;

    @Autowired
    public SearchController(EventSearchService eventSearchService) {
        this.eventSearchService = eventSearchService;
    }

    @GetMapping("/events")
    public ResponseEntity<List<EventDocument>> searchEvents(@RequestParam String query) {
        List<EventDocument> events = eventSearchService.searchEvents(query);
        return ResponseEntity.ok(events);
    }
}
