package com.hookflow.repository;
import com.hookflow.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import com.hookflow.entity.DeliveryStatus;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
public interface DeliveryRepository extends JpaRepository<Delivery,UUID>{
    List<Delivery> findByStatusAndNextAttemptAtLessThanEqual(
            DeliveryStatus status,
            OffsetDateTime currentTime
    );
    List<Delivery> findByStatusAndNextAttemptAtIsNull(
            DeliveryStatus status
    );
    @EntityGraph(attributePaths = {"event", "endpoint"})
    @Query(value = """
    SELECT * FROM delivery
    WHERE status = 'PENDING'
    AND (
        next_attempt_at IS NULL
        OR next_attempt_at <= :currentTime
    )
    ORDER BY created_at ASC
    LIMIT :batchSize
    FOR UPDATE SKIP LOCKED
    """, nativeQuery = true)
    List<Delivery> findReadyDeliveries(
            @Param("currentTime") OffsetDateTime currentTime,
            @Param("batchSize") int batchSize
    );

}
