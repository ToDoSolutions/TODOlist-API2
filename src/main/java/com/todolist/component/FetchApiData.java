package com.todolist.component;

import org.javatuples.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FetchApiData {

    public <T> T getApiDataWithToken(String url, Class<T> classType, Pair<String, String> token) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.exchange(url, HttpMethod.GET, createHttpEntityWithToken(token), classType).getBody();
    }

    private HttpEntity<Void> createHttpEntityWithToken(Pair<String, String> token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(token.getValue0(), token.getValue1());
        return new HttpEntity<>(headers);
    }
}

