package com.marcuseisele.test.asyncrest;

import com.marcuseisele.test.asyncrest.model.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/requests")
public class AsyncController {

    private static final int NUMBER_OF_REQUESTS = 4;

    private final AsyncService asyncService;

    @Autowired
    public AsyncController(AsyncService asyncService) {
        this.asyncService = asyncService;
    }

    @GetMapping("/async")
    public List<UserDTO> getResultsAsync() throws InterruptedException {
        long start = System.currentTimeMillis();
        log.info("Received async request");
        ArrayList<CompletableFuture<UserDTO>> users = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
            users.add(this.asyncService.getAsync(i));
        }
        CompletableFuture.allOf(users.toArray(new CompletableFuture[users.size()])).join();
        List<UserDTO> collect = users.stream().map(a -> a.getNow(null)).collect(Collectors.toList());
        long end = System.currentTimeMillis();
        log.info("Exiting async request after {}ms", end - start);
        return collect;

    }

    @GetMapping("/sync")
    public List<UserDTO> getResultsSync() throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        log.info("Received sync request");
        ArrayList<UserDTO> users = new ArrayList<>();
        for (int i = 0; i < NUMBER_OF_REQUESTS; i++) {
            users.add(this.asyncService.getSync(i));
        }
        long end = System.currentTimeMillis();
        log.info("Exiting sync request after {}ms", end - start);
        return users;
    }
}
