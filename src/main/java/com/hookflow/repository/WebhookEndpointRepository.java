package com.hookflow.repository;
import com.hookflow.entity.WebhookEndpoint;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
public interface WebhookEndpointRepository extends JpaRepository<WebhookEndpoint,UUID>{
    @Query(value = """
    SELECT *
    FROM webhook_endpoint
    WHERE active = TRUE
      AND :eventType = ANY(subscribed_event_types)
    """, nativeQuery = true)
    List<WebhookEndpoint> findActiveSubscribedEndpoints(
            @Param("eventType") String eventType
    );
}
