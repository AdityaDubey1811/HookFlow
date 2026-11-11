package com.hookflow.service;
import com.hookflow.entity.Delivery;
import com.hookflow.repository.DeliveryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class DeliveryPersistenceService {
    private final DeliveryRepository deliveryRepository;
    public DeliveryPersistenceService(DeliveryRepository deliveryRepository){
        this.deliveryRepository = deliveryRepository;
    }
    @Transactional
    public void saveDeliveryResult(Delivery delivery){
        deliveryRepository.save(delivery);
    }
}
