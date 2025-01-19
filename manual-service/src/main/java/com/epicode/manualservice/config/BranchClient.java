package com.epicode.manualservice.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
public class BranchClient {
    private final RestTemplate restTemplate;

    @Value("${branch-service.url}")
    private String branchServiceUrl;

    public BranchClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Map<String, Object>> getBranchList(String jwtToken) {
        String url = branchServiceUrl + "/api/branch/list";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<List> response = restTemplate.exchange(
                url, HttpMethod.GET, requestEntity, List.class
        );
        List<Map<String, Object>> branchList = response.getBody();

        return branchList;
    }

//    public List<String> getBranchList(String jwtToken) {
//        String url = branchServiceUrl + "/api/branch/list";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + jwtToken);
//
//        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
//
//        ResponseEntity<List> response = restTemplate.exchange(
//                url, HttpMethod.GET, requestEntity, List.class
//        );
//
//        return response.getBody();
//    }
}