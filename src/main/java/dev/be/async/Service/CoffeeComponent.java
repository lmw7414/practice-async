package dev.be.async.Service;

import dev.be.async.repository.CoffeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class CoffeeComponent implements CoffeeUseCase{

    private final CoffeeRepository coffeeRepository;
    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    @Override
    public int getPrice(String name) {
        log.info("동기 호출 방식으로 가격 조회 시작");
        return coffeeRepository.getPriceByName(name);
    }

    @Override
    public CompletableFuture<Integer> getPriceAsync(String name) {
        log.info("비동기 호출 방식으로 가격 조회 시작");

        CompletableFuture<Integer> future = new CompletableFuture<>();
        new Thread(() -> {
            log.info("새로운 쓰레드로 작업 시작");
            Integer price = coffeeRepository.getPriceByName(name);
            future.complete(price);
        }).start();

        return future;
    }

    public CompletableFuture<Integer> getPriceAsyncUsingSupplyAsync(String name) {
        log.info("비동기 호출 방식으로 가격 조회 시작");
        return CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync");
            return coffeeRepository.getPriceByName(name);
        },
                threadPoolTaskExecutor
        );
    }

    @Override
    public CompletableFuture<Integer> getDiscountPriceAsync(Integer price) {
        log.info("비동기 호출 방식으로 가격 할인 시작");
        return CompletableFuture.supplyAsync(() -> {
            log.info("supplyAsync");
            return (int)(price * 0.9);
        },
                threadPoolTaskExecutor
        );
    }
}
