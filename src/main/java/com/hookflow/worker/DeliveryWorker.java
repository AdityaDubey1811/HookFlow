package com.hookflow.worker;
import com.hookflow.service.DeliveryProcessingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
public class DeliveryWorker {
    private final DeliveryProcessingService deliveryProcessingService;
    public DeliveryWorker(DeliveryProcessingService deliveryProcessingService){
        this.deliveryProcessingService = deliveryProcessingService;
    }
    @Scheduled(fixedDelay = 5000)
    public void processPendingDeliveries(){
        deliveryProcessingService.processBatch(10);
    }
}
