package com.hookflow.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
@Entity
@Table(name = "event")
public class Event {
    @Id
    private UUID id;
    @Column(name = "event_type",nullable = false,length = 255)
    private String eventType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(nullable = false,columnDefinition = "JSONB")
    private String payload;
    @Column(name = "idempotency_key",length = 255)
    private String idempotencyKey;
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;
    protected Event(){

    }
    public Event(
            UUID id,
            String eventType,
            String payload,
            String idempotencyKey
    ) {
        this.id = id;
        this.eventType = eventType;
        this.payload = payload;
        this.idempotencyKey = idempotencyKey;
    }
    public UUID getId() {
        return id;
    }
    public String getEventType() {
        return eventType;
    }
    public String getPayload() {
        return payload;
    }
    public String getIdempotencyKey() {
        return idempotencyKey;
    }
    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
