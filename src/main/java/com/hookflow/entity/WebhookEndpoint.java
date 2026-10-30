package com.hookflow.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
@Entity
@Table(name = "webhook_endpoint")
public class WebhookEndpoint {
    @Id
    private UUID id;
    @Column(nullable = false,columnDefinition = "TEXT")
    private String url;
    @Column(length = 255)
    private String description;
    @Column(
            name = "subscribed_event_types",
            nullable = false,
            columnDefinition = "TEXT[]"
    )
    private String[] subscribedEventTypes;
    @Column(nullable = false,columnDefinition = "TEXT")
    private String secret;
    @Column(nullable = false)
    private boolean active = true;
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;
    public WebhookEndpoint(){

    }
    public UUID getId() {
        return id;
    }
    public String getUrl() {
        return url;
    }
    public String getDescription() {
        return description;
    }
    public String[] getSubscribedEventTypes() {
        return subscribedEventTypes.clone();
    }
    public boolean isActive() {
        return active;
    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public WebhookEndpoint(
            UUID id,
            String url,
            String description,
            String[] subscribedEventTypes,
            String secret
    ){
        this.id = id;
        this.url = url;
        this.description = description;
        this.subscribedEventTypes = subscribedEventTypes;
        this.secret = secret;
    }
    public void deactivate() {
        this.active = false;
    }
    public void activate() {
        this.active = true;
    }
}
