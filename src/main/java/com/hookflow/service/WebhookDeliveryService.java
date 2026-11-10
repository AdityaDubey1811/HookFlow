package com.hookflow.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import com.hookflow.entity.Delivery;
import com.hookflow.repository.DeliveryRepository;
@Service
public class WebhookDeliveryService {
    private final RestClient restClient;
    private final DeliveryRepository deliveryRepository;
    public WebhookDeliveryService(RestClient restClient,DeliveryRepository deliveryRepository){
        this.restClient = restClient;
        this.deliveryRepository = deliveryRepository;
    }
    public int sendWebhook(String targetUrl, String payload) {

        return restClient.post()
                .uri(targetUrl)
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity()
                .getStatusCode()
                .value();
    }
    public int deliver(Delivery delivery) {

        String targetUrl = delivery.getEndpoint().getUrl();
        String payload = delivery.getEvent().getPayload();

        return sendWebhook(targetUrl, payload);
    }
    public void deliverAndMarkSuccess(Delivery delivery) {
        delivery.incrementAttemptCount();
        int httpStatus = deliver(delivery);

        delivery.markAsDelivered();
        delivery.setLastHttpStatus(httpStatus);
        deliveryRepository.save(delivery);
    }
}
