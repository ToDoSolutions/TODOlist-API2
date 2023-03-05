package com.todolist.component;

import org.javatuples.Pair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FetchApiData {

    public <T> T getApiData(String url, Class<T> classType) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, classType);
    }

    public <T> T getApiDataWithToken(String url, Class<T> classType, Pair<String, String> token) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(token.getValue0(), token.getValue1());
            return execution.execute(request, body);
        });
        return restTemplate.getForObject(url, classType);
    }

    public <T, S> T postApiDataWithToken(String url, Class<T> classType, Pair<String, String> token, S object) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(token.getValue0(), token.getValue1());
            return execution.execute(request, body);
        });
        return restTemplate.postForObject(url, object, classType);
    }

    public <T, S> T putApiDataWithToken(String url, Class<T> classType, Pair<String, String> token, S object) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(token.getValue0(), token.getValue1());
            return execution.execute(request, body);
        });
        return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(object), classType).getBody();
    }

    public <T, S> T deleteApiDataWithToken(String url, Class<T> classType, Pair<String, String> token, S object) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().add(token.getValue0(), token.getValue1());
            return execution.execute(request, body);
        });
        return restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(object), classType).getBody();
    }


}
