package com.hookflow.service;
import com.hookflow.entity.Delivery;
import com.hookflow.entity.DeliveryStatus;
import com.hookflow.repository.DeliveryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
@Service
public class DeliveryProcessingService {
    private final DeliveryRepository deliveryRepository;
    public DeliveryProcessingService(DeliveryRepository deliveryRepository){
        this.deliveryRepository = deliveryRepository;
    }
    @Transactional
    public List<Delivery> fetchReadyDeliveries(int batchSize){
        return deliveryRepository.findReadyDeliveries(
                OffsetDateTime.now(),
                batchSize
        );
    }
    @Transactional
    public void markAsProcessing(Delivery delivery) {
        delivery.markAsProcessing();
        deliveryRepository.save(delivery);
    }
    @Transactional
    public List<Delivery> fetchAndMarkReadyDeliveries(int batchSize) {
        if (batchSize <= 0) {
            throw new IllegalArgumentException("Batch size must be greater than zero");
        }
        List<Delivery> deliveries = deliveryRepository.findReadyDeliveries(
                OffsetDateTime.now(),
                batchSize
        );
        for (Delivery delivery : deliveries) {
            delivery.markAsProcessing();
        }
        return deliveryRepository.saveAll(deliveries);
    }
}
