package com.todolist.component;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FetchApiData {

    // Methods ----------------------------------------------------------------
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
}
