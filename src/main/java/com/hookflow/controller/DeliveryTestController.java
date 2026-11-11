package com.hookflow.controller;
import com.hookflow.entity.Delivery;
import com.hookflow.service.DeliveryProcessingService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/internal/delivery-test")
public class DeliveryTestController {
    private final DeliveryProcessingService deliveryProcessingService;

    public DeliveryTestController(DeliveryProcessingService deliveryProcessingService) {
        this.deliveryProcessingService = deliveryProcessingService;
    }

    @PostMapping("/claim")
    public List<UUID> claimDeliveries(
            @RequestParam(defaultValue = "10") int batchSize) {

        return deliveryProcessingService.fetchAndMarkReadyDeliveries(batchSize)
                .stream()
                .map(Delivery::getId)
                .toList();
    }
    @PostMapping("/process")
    public void processBatch(
            @RequestParam(defaultValue = "10") int batchSize) {

        deliveryProcessingService.processBatch(batchSize);
    }
}
