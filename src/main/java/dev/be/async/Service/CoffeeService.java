package dev.be.async.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Slf4j
@Component
@EnableAsync
@RequiredArgsConstructor
public class CoffeeService {

    /*  AsyncExecutionAspectSupport의 doSubmit 메서드

    @Nullable
	protected Object doSubmit(Callable<Object> task, AsyncTaskExecutor executor, Class<?> returnType) {
		if (CompletableFuture.class.isAssignableFrom(returnType)) {
			return CompletableFuture.supplyAsync(() -> {
				try {
					return task.call();
				}
				catch (Throwable ex) {
					throw new CompletionException(ex);
				}
			}, executor);
		}
		else if (ListenableFuture.class.isAssignableFrom(returnType)) {
			return ((AsyncListenableTaskExecutor) executor).submitListenable(task);
		}
		else if (Future.class.isAssignableFrom(returnType)) {
			return executor.submit(task);
		}
		else {
			executor.submit(task);
			return null;
		}
	}
     */

    // 리턴을 하지 않는 경우
    @Async
    public void order(String name) {
        try {
            log.info("start order..." + name);
            Thread.sleep(3000);
            log.info("end order..." + name);
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
    }

    //리턴타입이 future 인 경우
    @Async
    public Future<Integer> getPriceAsyncWithFuture(String name) throws InterruptedException {
        log.info("start get..." + name);

        Thread.sleep(3000);
        return new AsyncResult<>(3500);
    }

    //리턴타입이 ListenableFuture 인 경우
    @Async
    public ListenableFuture<Integer> getPriceAsyncWithListenableFuture(String name) throws InterruptedException {
        log.info("start get... :" + name);
        Thread.sleep(3000);
        return new AsyncResult<>(3500);
    }

    //리턴타입이 CompletableFuture 인 경우
    @Async
    public CompletableFuture<Integer> getPriceAsyncWithCompletableFuture(String name) {
        try {
            log.info("start getPrice..." + name);
            Thread.sleep(3000);
            log.info("end getPrice..." + name);
        } catch (InterruptedException e) {
            log.info(e.getMessage());
        }
        return new AsyncResult<>(3500).completable();
    }

}
