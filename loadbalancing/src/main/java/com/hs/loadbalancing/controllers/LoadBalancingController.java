package com.hs.loadbalancing.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.hs.loadbalancing.services.dto.TransactionDTO;
import com.hs.loadbalancing.services.dto.TransactionResponseDTO;
import com.hs.loadbalancing.services.LoadBalancingService;

import lombok.extern.slf4j.Slf4j;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RestController
public class LoadBalancingController {

    private final LoadBalancingService loadBalancingService;

    private final RestTemplate restTemplate;

    LoadBalancingController(
            LoadBalancingService loadBalancingService,
            RestTemplate restTemplate) {
        this.loadBalancingService = loadBalancingService;
        this.restTemplate = restTemplate;
    }

    @PostMapping("/register")
    public void registerNewInstance(@RequestBody Map<String, String> request) {
        log.info("REST request to register new instance : {}", request);
        String host = request.get("host");
        String port = request.get("port");
        loadBalancingService.register(host, port);
    }

    @PostMapping("/client/transaction")
    public ResponseEntity<TransactionResponseDTO> redirect(
            @RequestHeader(value = "X-requested-latency", required = false) Long latency,
            @RequestBody TransactionDTO transactionDTO) throws Exception {
        log.info("REST request to create transaction using load balancing : {}, latency : {}", transactionDTO, latency);

        String serviceUrl = loadBalancingService.getNextServiceUrl() + "/api/transactions";
        log.debug("Redirect to : {}", serviceUrl);

        HttpHeaders headers = new HttpHeaders();
        if (latency != null) {
            headers.add("X-requested-latency", latency.toString());
        }
        HttpEntity<TransactionDTO> request = new HttpEntity<>(transactionDTO, headers);

        TransactionResponseDTO result = restTemplate.postForObject(serviceUrl, request, TransactionResponseDTO.class);

        HttpHeaders headersResponse = new HttpHeaders();
        headersResponse.setLocation(URI.create(serviceUrl));

        return ResponseEntity.ok().headers(headersResponse).body(result);
    }

    @GetMapping("/routes")
    public ResponseEntity<List<String>> getValidRoutes() {
        log.info("REST request to get valid route");
        List<String> servicesUrl = loadBalancingService.getValidRoute();
        return ResponseEntity.ok().body(servicesUrl);
    }

}
