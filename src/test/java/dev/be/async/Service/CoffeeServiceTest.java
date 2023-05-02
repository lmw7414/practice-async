package dev.be.async.Service;

import dev.be.async.config.TaskConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@Slf4j
@SpringBootTest(classes = {CoffeeService.class, TaskConfig.class})
class CoffeeServiceTest {

    @Autowired
    CoffeeService coffeeService;
    @Test
    void Async를_이용한_비동기_호출_리턴_타입_없음() throws InterruptedException {
        log.info("test call..." + Thread.currentThread().getName());
        coffeeService.order("latte");
        log.info("non blocking : ...");
    }

    @Test
    void Async를_이용한_비동기_호출_리턴_타입_future() throws ExecutionException, InterruptedException {

        Future<Integer> future = coffeeService.getPriceAsyncWithFuture("latte");
        System.out.println("blocking..latte's price is : " + future.get());

        /*
        future의 get메서드는 메서드의 결과를 조회할 때까지 계속 기다린다. 즉, 메서드의 수행이 완료될 때까지 기다려야 하며, 블록킹 현상이 발생한다.
        만약 비동기 메서드를 호출하는 클라이언트가 기다리지 않고 다른 작업을 게속 수행하도록 구현하고 싶다면... 즉, 논블록킹으로 동작하게 하고 싶다면, 콜백 메서드를
        처리하는 로직을 구현해주면 된다.
         */
    }

    @Test
    void Async를_이용한_비동기_호출_리턴_타입_ListenableFuture() throws InterruptedException {

        ListenableFuture<Integer> future = coffeeService.getPriceAsyncWithListenableFuture("latte");
        future.addCallback(s -> log.info("latte's price is : " + s), e -> log.info(e.getMessage()));
        log.info("non blocking");
        Thread.sleep(3000);

        /*
        future.addCallback 메서드는 비동기 메서드의 내부 로직이 완료되면 수행되는 콜백 기능이다. Future를 사용했을 때는 future.get을 사용했을 때 메서드가 처리될 때까지
        블록킹 현상이 발생했지만, 콜백 메서드를 사용한다면 결과를 얻을 때까지 무작정 기다릴 필요가 없다.

        1. 비동기 메서드 호출
        2. 비동기 콜백 메서드 실행
        3. 다른 작업 수행(논블록킹 로그 남김)
         */
    }
    @Test
    void Async를_이용한_비동기_호출_리턴_타입_CompletableFuture() throws InterruptedException, ExecutionException {

        CompletableFuture<Integer> future = coffeeService.getPriceAsyncWithCompletableFuture("latte");
        log.info("non blocking");
        log.info("blocking: latte's price is " + future.get());
        log.info("after blocking...");

        /*
        비동기 메서드인 getPriceAsyncWithCompletableFuture는 메인쓰레드가 아니라 별도의 쓰레드에서 동작하는 것을 확인
        메인 쓰레드에서 getPrice 메서드를 실행시킨 후 바로 다른 작업을 수행. 이후 get메서드로 인해 잠시 블록킹시켜 값을 받아온다.
         */
    }
}