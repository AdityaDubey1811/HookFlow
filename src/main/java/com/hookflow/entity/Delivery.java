package com.hookflow.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.OffsetDateTime;
import java.util.UUID;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
@Entity
@Table(
        name = "delivery",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_delivery_event_endpoint",
                        columnNames = {"event_id","endpoint_id"}
                )
        }
)
public class Delivery {
    @Id
    private UUID id;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "event_id",nullable = false)
    private Event event;
    @ManyToOne(fetch = FetchType.LAZY,optional = false)
    @JoinColumn(name = "endpoint_id",nullable = false)
    private WebhookEndpoint endpoint;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 30)
    private DeliveryStatus status;
    @Column(name = "attempt_count",nullable = false)
    private int attemptCount = 0;
    @Column(name = "next_attempt_at")
    private OffsetDateTime nextAttemptAt;
    @Column(name = "last_http_status")
    private Integer lastHttpStatus;
    @Column(name = "last_error",columnDefinition = "TEXT")
    private String lastError;
    @Column(
            name = "created_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime createdAt;
    @Column(
            name = "updated_at",
            nullable = false,
            insertable = false,
            updatable = false
    )
    private OffsetDateTime updatedAt;
    protected Delivery(){

    }
    public Delivery(
            UUID id,
            Event event,
            WebhookEndpoint endpoint
    ) {
        this.id = id;
        this.event = event;
        this.endpoint = endpoint;
        this.status = DeliveryStatus.PENDING;
        this.attemptCount = 0;
    }
}
