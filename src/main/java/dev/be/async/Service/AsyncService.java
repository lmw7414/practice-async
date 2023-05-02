package dev.be.async.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.stream.IntStream;

@Slf4j
@Component
@RequiredArgsConstructor
public class AsyncService {

    private final ThreadPoolTaskExecutor threadPoolTaskExecutor;

    private static void task() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static String task(String s) {
        try {
            Thread.sleep(5000);
            return "return coffee: " + s;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeSequentialTask() {
        IntStream.range(0, 10).forEach(n -> {
            String threadName = Thread.currentThread().getName();
            System.out.println(threadName + ": task start" + n);
            task();
            System.out.println(threadName + ": task completed" + n);
        });
    }

    public void executeParallelTask() {
        IntStream.range(0, 10).forEach(n ->
                threadPoolTaskExecutor.execute(() -> {
                    String threadName = Thread.currentThread().getName();
                    System.out.println(threadName + ": task start" + n);
                    task();
                    System.out.println(threadName + ": task completed" + n);
                })
        );
    }

}
