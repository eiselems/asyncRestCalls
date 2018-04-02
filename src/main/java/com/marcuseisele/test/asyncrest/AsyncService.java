package com.marcuseisele.test.asyncrest;

import com.marcuseisele.test.asyncrest.model.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class AsyncService {

    private final RestTemplate restTemplate;
    private static final String URL_USED = "https://api.github.com/users/eiselems";
    private static final int simulatedWait = 15;

    @Autowired
    public AsyncService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Async
    public CompletableFuture<UserDTO> getAsync(int i) throws InterruptedException {
        log.info("Executing request no: {}", i);
        UserDTO result = null;
        try {
            result = restTemplate.getForObject(URL_USED, UserDTO.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                log.info("Request no: {} returned FORBIDDEN", i);
            }
        }

        //it is slower than usual **cough**
        Thread.sleep(simulatedWait);
        log.info("Finished Executing request no: {}", i);
        return CompletableFuture.completedFuture(result);
    }

    public UserDTO getSync(int i) throws InterruptedException, ExecutionException {
        return getAsync(i).get();
    }
}
