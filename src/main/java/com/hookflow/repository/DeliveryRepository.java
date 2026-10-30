package com.hookflow.repository;
import com.hookflow.entity.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface DeliveryRepository extends JpaRepository<Delivery,UUID>{

}
