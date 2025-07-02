package com.eventbox.backend.repository.elasticsearch;

import com.eventbox.backend.model.elasticsearch.EventDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventSearchRepository extends ElasticsearchRepository<EventDocument, String> {
    List<EventDocument> findByTitleContainingOrDescriptionContainingOrTagsContaining(
            String title, String description, String tags);
}
