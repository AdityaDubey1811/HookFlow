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
public interface DeliveryRepository extends JpaRepository<Delivery,UUID>{
    List<Delivery> findByStatusAndNextAttemptAtLessThanEqual(
            DeliveryStatus status,
            OffsetDateTime currentTime
    );
    List<Delivery> findByStatusAndNextAttemptAtIsNull(
            DeliveryStatus status
    );
    @Query("""
        SELECT d FROM Delivery d
        WHERE d.status = :status
        AND (
            d.nextAttemptAt IS NULL
            OR d.nextAttemptAt <= :currentTime
        )
        """)
    List<Delivery> findReadyDeliveries(
            @Param("status") DeliveryStatus status,
            @Param("currentTime") OffsetDateTime currentTime,
            Pageable pageable
    );

}
