package com.eventbox.backend.model.elasticsearch;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Document(indexName = "events")
public class EventDocument {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "title")
    private String title;

    @Field(type = FieldType.Text, name = "description")
    private String description;

    @Field(type = FieldType.Date, name = "date")
    private LocalDate date;

    @Field(type = FieldType.Text, name = "time")
    private String time;

    @Field(type = FieldType.Double, name = "price")
    private BigDecimal price;

    @Field(type = FieldType.Integer, name = "minimum_age")
    private int minimumAge;

    @Field(type = FieldType.Text, name = "image_url")
    private String imageUrl;

    @Field(type = FieldType.Text, name = "tags")
    private String tags;

    @Field(type = FieldType.Keyword, name = "event_type")
    private String eventType;

    @Field(type = FieldType.Text, name = "establishment_name")
    private String establishmentName;

    @Field(type = FieldType.Text, name = "establishment_location")
    private String establishmentLocation;

    @Field(type = FieldType.Text, name = "organizer_name")
    private String organizerName;

    // Constructor vacío necesario para Elasticsearch
    public EventDocument() {}

    // Constructor completo
    public EventDocument(String id, String title, String description, LocalDate date, String time, 
                        BigDecimal price, int minimumAge, String imageUrl, String tags,
                        String eventType, String establishmentName, String establishmentLocation, String organizerName) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.price = price;
        this.minimumAge = minimumAge;
        this.imageUrl = imageUrl;
        this.tags = tags;
        this.eventType = eventType;
        this.establishmentName = establishmentName;
        this.establishmentLocation = establishmentLocation;
        this.organizerName = organizerName;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEstablishmentName() {
        return establishmentName;
    }

    public void setEstablishmentName(String establishmentName) {
        this.establishmentName = establishmentName;
    }

    public String getEstablishmentLocation() {
        return establishmentLocation;
    }

    public void setEstablishmentLocation(String establishmentLocation) {
        this.establishmentLocation = establishmentLocation;
    }

    public String getOrganizerName() {
        return organizerName;
    }

    public void setOrganizerName(String organizerName) {
        this.organizerName = organizerName;
    }
}
