package com.hookflow.controller;
import com.hookflow.dto.CreateWebhookEndpointRequest;
import com.hookflow.dto.WebhookEndpointResponse;
import com.hookflow.service.WebhookEndpointService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.UUID;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
@RestController
@RequestMapping("/api/webhook-endpoints")
public class WebhookEndpointController {
    private final WebhookEndpointService webhookEndpointService;
    public WebhookEndpointController(WebhookEndpointService webhookEndpointService){
        this.webhookEndpointService = webhookEndpointService;
    }
    @PostMapping
    public ResponseEntity<WebhookEndpointResponse> createEndpoint(
            @Valid @RequestBody CreateWebhookEndpointRequest request
    ){
        WebhookEndpointResponse response = webhookEndpointService.createEndpoint(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
    @GetMapping
    public ResponseEntity<List<WebhookEndpointResponse>> getAllEndpoints() {
        return ResponseEntity.ok(
                webhookEndpointService.getAllEndpoints()
        );
    }
    @GetMapping("/{id}")
    public ResponseEntity<WebhookEndpointResponse> getEndpointById(
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(
                webhookEndpointService.getEndpointById(id)
        );
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivateEndpoint(
            @PathVariable UUID id
    ) {
        webhookEndpointService.deactivateEndpoint(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateEndpoint(
            @PathVariable UUID id
    ) {
        webhookEndpointService.activateEndpoint(id);
        return ResponseEntity.noContent().build();
    }
}
