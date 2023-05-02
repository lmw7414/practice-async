# practice-async

참고한 사이트
* https://brunch.co.kr/@springboot/267
* https://brunch.co.kr/@springboot/401
* https://mangkyu.tistory.com/263
* https://steady-coding.tistory.com/611


## 동기와 비동기. 그리고 블록킹과 논블록킹
### 동기(Synchronous)
- 작업을 수행할 때, 해당 작업이 완료될 때까지 기다리고, 결과값을 반환받은 후에 다음 작업을 수행하는 방식 -> 작업이 순서대로 실행되는 방식
- 작업 수행시간이 길어지면 다음 작업을 수행할 수 없고, 대기시간이 발생하여 성능에 문제가 발생할 수 있다.

### 비동기(Asynchronous)
- 작업을 수행할 때, 해당 작업이 완료되지 않았더라도 결과값을 기다리지 않고 다음 작업을 수행하는 방식을 의미-> 작업이 순서대로 실행되지 않고, 비동기적으로 실행되는 방식
- 작업 수행시간이 길어져도 다른 작업을 수행할 수 있으므로 대기 시간이 없고, 성능이 향상될 수 있다. 하지만 작업의 순서를 보장하지 않으므로 순서가 중요한 작업에는 적합하지 않을 수 있다.

### 블록킹(Blocking)
- 동기 상태에서 블록킹 : 호출한 함수가 작업이 완료될 때까지 대기하며, 작업이 완료될 때까지 다른 작업을 수행할 수 없다.
- 비동기 상태에서 블록킹 : 작업(1)이 완료될 때까지 대기하지만, 그동안 다른 작업(2)을 수행할 수 있다. 1이 완료되면 2를 블록킹하고 1의 결과를 가져온다.

### 논블록킹(Non-Blocking)
- 작업을 요청하고, 작업이 완료될 때까지 기다리지 않고 다른 작업을 수행하고 있는다. 요청한 작업이 끝났다면 알아서 결과를 반환해준다.

## 자바 스레드의 종류
- newFixedThreadPool : 처리할 작업이 등록되면 그에 따라 실제 작업할 스레드를 하나씩 생성. 생성할 수 있는 쓰레드의 최대 개수는 제한되어 있으며, 제한된 개수까지 쓰레드를 생성한 후 쓰레드를 유지한다.
- newCachedThreadPool : 캐시 쓰레드 풀은 현재 갖고 있는 쓰레드의 수가 처리할 작업의 수보다 많아서 쉬는 쓰레드가 많이 발생할 때 쉬는 쓰레드를 종료시켜 훨씬 유연하게 대응할 수 있다. 처리할 작업의 수가 많아지면 그만큼 쓰레드를 생성한다. 반면에 쓰레드의 수에는 제한을 두지 않는다.
- newSingledThreadExecutor : 단일 쓰레드로 동작하는 Executor로서 작업을 처리하는 쓰레드가 단 하나 뿐이다.
- newScheduledThreadPool : 일정 시간 이후에 실행하거나 주기적으로 작업을 실행할 수 있으며, 쓰레드의 수가 고정되어 있는 형태의 Executor.Timer 클래스의 기능과 유사하다.


## @Async 기본 설정
1. 스프링 부트에서 @Async를 사용하기 위해서는 @EnableAsync 어노테이션을 먼저 선언
2. 비동기 처리할 메서드에 @Async 애노테이션을 붙이고 해당 클래스는 public으로 한다.(해당 메서드는 AOP에 의해 동작)
3. 같은 클래스 내부의 메서드는 호출할 수 없으니 주의 (https://dzone.com/articles/effective-advice-on-spring-async-part-1)
4. ㄴ 프록시를 사용하기 위해서 메서드는 public이어야 하고, Self-invocation를 사용하게되면 프록시를 무시하고 바로 메서드를 호출하기 때문


## 비동기 처리에서 리턴 값이 있는 경우
Future 구현체를 통해 구현한다.

### Future
- 비동기 작업을 처리한 후 리턴 값을 받고 싶을 때 사용
- get() : 해당 메서드를 통해 리턴 값을 받아오는데 이는 현재 작업을 블록하고 받아오기 때문에 비동기 처리작업이 완료될 때까지 대기한다.
- 따라서 작업이 계속해서 끝나지 않는다면 무한대기 상태가 된다. -> 성능이 좋지 않음
- get(Long time, TimeUnit unit) : 일정 시간 내에 응답이 없으면 `TimeoutException` 예외를 발생시킨다.
- 여러 Future를 조합하는 것이 불가능 
- 작업을 외부에서 완료시킬 수 없음

### ListenableFuture
- 콜백을 통해 논블록킹 방식으로 작업 처리가 가능하다.
### CompletableFuture
- 외부에서 작업을 완료시킬 수 잇고, 콜백 등록 및 Future 조합이 가능하다.
- 비동기 작업 실행
  - runAsync : 반환값이 없는 경우
  - supplyAsync : 반환값이 있는 경우
- 작업 콜백
  - thenApply
    - 반환 값을 받아서 다른 값을 반환함
    - 함수형 인터페이스 Function을 파라미터로 받음
  - thenAccept
    - 반환 값을 받아 처리하고 값을 반환하지 않음
    - 함수형 인터페이스 Consumer를 파라미터로 받음
  - thenRun
    - 반환 값을 받지 않고 다른 작업을 실행함
    - 함수형 인터페이스 Runnable을 파라미터로 받음
- 작업 조합
  - thenCompose
    - 두 작업이 이어서 실행하도록 조합하며, 앞선 작업의 결과를 받아서 사용할 수 있음
    - 함수형 인터페이스 Function을 파라미터로 받음
  - thenCombine
    - 두 작업을 독립적으로 실행하고, 둘 다 완료되었을 때 콜백을 실행함
    - 함수형 인터페이스 Function을 파라미터로 받음
  - allOf
    - 여러 작업을 동시에 실행하고, 모든 작업 결과에 콜백을 실행함
  - anyOf
    - 여러 작업들 중에서 가장 빨리 끝난 하난의 결과에 콜백을 실행함