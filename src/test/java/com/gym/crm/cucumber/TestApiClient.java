package com.gym.crm.cucumber;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class TestApiClient {

    private final TestRestTemplate restTemplate;

    public TestApiClient(TestRestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> get(String path, String bearerToken) {
        return exchange(HttpMethod.GET, path, null, bearerToken);
    }

    public ResponseEntity<String> post(String path, Object body, String bearerToken) {
        return exchange(HttpMethod.POST, path, body, bearerToken);
    }

    public ResponseEntity<String> put(String path, Object body, String bearerToken) {
        return exchange(HttpMethod.PUT, path, body, bearerToken);
    }

    public ResponseEntity<String> patch(String path, Object body, String bearerToken) {
        return exchange(HttpMethod.PATCH, path, body, bearerToken);
    }

    public ResponseEntity<String> delete(String path, String bearerToken) {
        return exchange(HttpMethod.DELETE, path, null, bearerToken);
    }

    private ResponseEntity<String> exchange(HttpMethod method, String path, Object body, String bearerToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (bearerToken != null) {
            headers.setBearerAuth(bearerToken);
        }
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);
        return restTemplate.exchange(path, method, entity, String.class);
    }
}