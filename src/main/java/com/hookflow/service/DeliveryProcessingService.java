package com.hookflow.service;
import com.hookflow.entity.Delivery;
import com.hookflow.entity.DeliveryStatus;
import com.hookflow.repository.DeliveryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.List;
import com.hookflow.service.WebhookDeliveryService;
import java.util.UUID;
@Service
public class DeliveryProcessingService {
    private final DeliveryRepository deliveryRepository;
    private final WebhookDeliveryService webhookDeliveryService;
    public DeliveryProcessingService(DeliveryRepository deliveryRepository,WebhookDeliveryService webhookDeliveryService){
        this.deliveryRepository = deliveryRepository;
        this.webhookDeliveryService = webhookDeliveryService;
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
        deliveryRepository.saveAll(deliveries);
        if (deliveries.isEmpty()) {
            return deliveries;
        }
        List<UUID> deliveryIds = deliveries.stream()
                .map(Delivery::getId)
                .toList();
        return deliveryRepository.findWithEventAndEndpointByIdIn(deliveryIds);
    }
    public void processBatch(int batchSize) {
        List<Delivery> deliveries =
                fetchAndMarkReadyDeliveries(batchSize);
        for (Delivery delivery : deliveries) {
            webhookDeliveryService.deliverAndMarkSuccess(delivery);
        }
    }
}
