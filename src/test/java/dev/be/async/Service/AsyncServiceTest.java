package dev.be.async.Service;

import dev.be.async.config.TaskConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.StopWatch;

@Slf4j
@SpringBootTest(classes = {AsyncService.class, TaskConfig.class})
class AsyncServiceTest {

    @Autowired
    private AsyncService asyncService;

    @Test
    void 총_10개의_태스크를_동기로_수행시_5초이상() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        asyncService.executeSequentialTask();

        stopWatch.stop();
        log.info("총 실행 시간: " + String.valueOf(stopWatch.getTotalTimeSeconds()) + "초");
    }

    // 메인 스레드가 종료되기 때문에 결과를 확인할 수 없다. 결과를 알기 위해서는 Future 구현체를 통해 구현할 수 있다.
    @Test
    void 총_10개의_태스크를_비동기로_수행시_즉시_수행_처리결과_모름() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        asyncService.executeParallelTask();

        stopWatch.stop();
        log.info("총 실행 시간: " + String.valueOf(stopWatch.getTotalTimeSeconds()) + "초");
    }

}