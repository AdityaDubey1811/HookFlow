package com.hookflow.service;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import com.hookflow.entity.Delivery;
import com.hookflow.repository.DeliveryRepository;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.transaction.annotation.Transactional;
import com.hookflow.service.DeliveryPersistenceService;
import com.hookflow.config.RetryConfig;
import java.time.OffsetDateTime;
@Service
public class WebhookDeliveryService {
    private final RestClient restClient;
    private final DeliveryRepository deliveryRepository;
    private final DeliveryPersistenceService deliveryPersistenceService;
    private final RetryConfig retryConfig;
    public WebhookDeliveryService(RestClient restClient,
                                  DeliveryRepository deliveryRepository,
                                  DeliveryPersistenceService deliveryPersistenceService,
                                  RetryConfig retryConfig){
        this.restClient = restClient;
        this.deliveryRepository = deliveryRepository;
        this.deliveryPersistenceService = deliveryPersistenceService;
        this.retryConfig = retryConfig;
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
        try {
            int httpStatus = deliver(delivery);
            delivery.markAsDelivered();
            delivery.setLastHttpStatus(httpStatus);
        } catch (RestClientResponseException ex) {
            handleFailure(delivery, ex.getStatusCode().value(), ex.getMessage());
        } catch (RestClientException ex) {
            handleFailure(delivery, null, ex.getMessage());
        }
        deliveryPersistenceService.saveDeliveryResult(delivery);
    }
    private void handleFailure(
            Delivery delivery,
            Integer httpStatus,
            String error) {
        delivery.markAsFailed(httpStatus, error);
        if (delivery.getAttemptCount() < RetryConfig.MAX_ATTEMPTS) {
            long delay = retryConfig.calculateRetryDelaySeconds(
                    delivery.getAttemptCount()
            );
            delivery.scheduleRetry(
                    OffsetDateTime.now().plusSeconds(delay),
                    error
            );
        }
    }
}
