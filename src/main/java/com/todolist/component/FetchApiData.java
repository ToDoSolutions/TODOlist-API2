package com.todolist.component;

import com.todolist.entity.github.Issue;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FetchApiData {

    public <T> T getApiData(String url, Class<T> classType) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, classType);
    }
}
