package dev.be.async.Service;

import dev.be.async.config.TaskConfig;
import dev.be.async.repository.CoffeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@Slf4j
@SpringBootTest(classes = {CoffeeComponent.class, CoffeeRepository.class, TaskConfig.class})
class CoffeeComponentTest {

    @Autowired
    private CoffeeComponent coffeeComponent;
    @Test
    void 가격_조회_동기_블록킹_호출_테스트() {
        // Given
        int expectedPrice = 3500;

        // When
        int resultPrice = coffeeComponent.getPrice("latte");
        log.info("최종 가격 전달 받음");

        // Then
        assertEquals(expectedPrice, resultPrice);
    }

    @Test
    void 가격_조회_비동기_블록킹_호출_테스트1() {
        // Given
        int expectedPrice = 3500;

        // When
        CompletableFuture<Integer> future = coffeeComponent.getPriceAsync("latte");
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");
        int resultPrice = future.join();  // 블록킹
        log.info("최종 가격 전달 받음");

        // Then
        assertEquals(expectedPrice, resultPrice);
    }

    @Test
    void 가격_조회_비동기_블록킹_호출_테스트_Supply_Async() {
        // Given
        int expectedPrice = 3500;

        // When
        CompletableFuture<Integer> future = coffeeComponent.getPriceAsyncUsingSupplyAsync("latte");
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");
        int resultPrice = future.join();  // 블록킹
        log.info("최종 가격 전달 받음");

        // Then
        assertEquals(expectedPrice, resultPrice);
    }

    @Test
    void 가격_조회_비동기_호출_콜백_반환없음_테스트() {
        // Given
        Integer expectedPrice = 3500;

        // When & Then
        CompletableFuture<Void> future = coffeeComponent
                .getPriceAsyncUsingSupplyAsync("latte")
                .thenAccept(p -> {
                    log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지는 않음");
                    assertEquals(expectedPrice, p);
                });
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행가능, 논블록킹");

        /*
        아래 구문이 없으면, main thread 가 종료되기 때문에, thenAccept 확인하기 전에 끝나버림.
        그래서, 테스트를 위해서 메인쓰레드가 종료되지 않도록 블록킹으로 대기하기 위한 코드
        future 가 complete 가 되면 위에 작성한 thenAccept 코드가 실행이 됨
        */
        assertNull(future.join());

    }

    @Test
    void 가격_조회_비동기_호출_콜백_반환_테스트() {
        //Given
        Integer expectedPrice = 3500 + 100;

        //When & Then
        CompletableFuture<Void> future = coffeeComponent
                .getPriceAsyncUsingSupplyAsync("latte")
                .thenApply(p -> {
                    log.info("같은 쓰레드로 동작");
                    return p + 100;
                })
                .thenAccept(p -> {
                    log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지는 않음");
                    assertEquals(expectedPrice, p);
                });
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");

        assertNull(future.join());

    }

    @Test
    void 가격_조회_비동기_호출_콜백_반환_테스트_별도_쓰레드() {
        //Given
        Integer expectedPrice = 3500 + 100;
        Executor executor = Executors.newFixedThreadPool(10);
        //When & Then
        CompletableFuture<Void> future = coffeeComponent
                .getPriceAsyncUsingSupplyAsync("latte")
                .thenApplyAsync(p -> {
                    log.info("다른 쓰레드로 동작");
                    return p + 100;
                }, executor)
                .thenAcceptAsync(p -> {
                    log.info("콜백, 가격은 " + p + "원, 하지만 데이터를 반환하지는 않음");
                    assertEquals(expectedPrice, p);
                });
        log.info("아직 최종 데이터를 전달 받지는 않았지만, 다른 작업 수행 가능");

        assertNull(future.join());

    }

    /*
    thenCombine은 CompletableFuture를 2개 실행해서 결과를 조합할 때 사용. 병렬 실행을 해서 조합하는데 순차적으로 실행하지 않는다.
    만약 순차적으로 조회하면 1초 + 1초 이기 때문에 2초가 걸리겠지만, 비동기로 처리하기 때문에 동시에 두가지 조회를 같이 수행한 다음 결과를 조합할 것이고
    수행 시간은 1초가 걸릴 것이다.
     */
    @Test
    void thenCombine_test() {
        Integer expectedPrice = 3500 + 3600;

        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsyncUsingSupplyAsync("latte");
        CompletableFuture<Integer> futureB = coffeeComponent.getPriceAsyncUsingSupplyAsync("mocha");

        //futureA.thenCombine(futureB, (a, b) -> a + b);
        Integer resultPrice = futureA.thenCombine(futureB, Integer::sum).join();

        assertEquals(expectedPrice, resultPrice);
    }

    //thenCompose는 thenCombine과는 다르게  CompletableFuture를 순차적으로 실행한다.
    // 가격 조회 -> 가격 할인
    @Test
    public void thenCompose_test() {
        Integer expectedPrice = (int)(3500 * 0.9);
        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsyncUsingSupplyAsync("latte");

        Integer resultPrice = futureA.thenCompose(result ->
                coffeeComponent.getDiscountPriceAsync(result)).join();

        assertEquals(expectedPrice, resultPrice);
    }


    /*
    CompletableFuture.allOf(futureA, futureB, ...) : future가 완료되면 ComplerableFuture<Void>를 반환

    anyOf : allOf는 모든 Future가 완료되었을 때 수행한다면, anyOf라는 메서드는 아무 Future나 하나라도 완료되면 수행되는 메서드이다.

    thenApply : allOf 로부터 반환 받은 CompletableFuture<Void>를 사용하지 않지만, 무언가 데이터를 반환해야 한다.
    해당 로직에서는 상단에 선언한 completableFutureList의 스트림 구문을 선언해서 각각의 Future에서 데이터를 조회해서 리스트로 만들어준다.
    CompletableFuture<List<Integer>>가 반환될 것이다.

    join : CompletableFuture<List<Integer>>에서 List<Integer> 를 조회한다. 각각의 커피의 가격이기 때문에 [3500, 3600, 2000]이 된다.

    stream().reduce : 리스트의 데이터 합산
     */
    @Test
    void allOf_test() {
        Integer expectedPrice  = 3500 + 3600 + 2000;

        CompletableFuture<Integer> futureA = coffeeComponent.getPriceAsyncUsingSupplyAsync("latte");
        CompletableFuture<Integer> futureB = coffeeComponent.getPriceAsyncUsingSupplyAsync("mocha");
        CompletableFuture<Integer> futureC = coffeeComponent.getPriceAsyncUsingSupplyAsync("americano");

        List<CompletableFuture<Integer>> completableFutureList = Arrays.asList(futureA, futureB, futureC);

        //Integer resultPrice = CompletableFuture.allOf(completableFutureList.toArray(new CompletableFuture[3]))
        Integer resultPrice = CompletableFuture.allOf(futureA, futureB, futureC)
                .thenApply( Void -> completableFutureList.stream()
                        .map(CompletableFuture::join)
                        .collect(Collectors.toList()))
                .join()
                .stream()
                .reduce(0, Integer::sum);

        assertEquals(expectedPrice, resultPrice);
    }
}
