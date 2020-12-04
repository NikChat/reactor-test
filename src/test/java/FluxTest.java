import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.Random;

public class FluxTest {

    @Test
    void firstFlux() {
        Flux.just("A", "B", "C")
                .log()
                .subscribe();
    }

    @Test
    void fluxFromIterable() {
        Flux.fromIterable(Arrays.asList("A", "B", "C"))
                .log()
                .subscribe();
    }

    @Test
    void fluxFromRange() {
        Flux.range(10, 5)
                .log()
                .subscribe();
    }

    @Test
    void fluxFromInterval() throws Exception { //every second a new value will be published (starting from 0)
        Flux.interval(Duration.ofSeconds(1))
                .log()
                .take(2)
                .subscribe();
        Thread.sleep(5000);
    }

    @Test
    void fluxRequest() {
        Flux.range(1, 5)
                .log()
                .subscribe(null,
                        null,
                        null,
                        s -> s.request(3) //we use the request method of the subscription object to request 3 elements
                ); //onComplete was not called, because the publisher produces 5 elements.
    }

    @Test
    void fluxCustomSubscriber() {
        Flux.range(1, 10)
                .log()
                .subscribe(new BaseSubscriber<Integer>() {
                    int elementsToProcess = 3;
                    int counter = 0;

                    public void hookOnSubscribe(Subscription subscription) {
                        System.out.println("Subscribed!");
                        request(elementsToProcess);
                    }

                    public void hookOnNext(Integer value) {
                        counter++;
                        if(counter == elementsToProcess) {
                            counter = 0;

                            Random r = new Random();
                            elementsToProcess = r.ints(1, 4)
                                    .findFirst().getAsInt();
                            request(elementsToProcess);
                        }
                    }
                });
    }

    @Test //i want to request a fixed number of elements every time
    void fluxLimitRate() { //request(3), meta pali request(3) gia ta teleutaia 2.
        Flux.range(1, 5)
                .log()
                .limitRate(3)
                .subscribe();
    }
    
    @Test
    void shedulersTest() {
    	Scheduler s = Schedulers.newParallel("parallel-scheduler", 4); 

        final Flux<String> flux = Flux
            .range(1, 5)
            .delayElements(Duration.ofMillis(1000))
            .log()
            .map(i -> 10 + i)  
            .publishOn(s)  
            .map(i -> "value " + i);  
        
        //flux.subscribe(System.out::println);
        new Thread(() -> flux.subscribe(System.out::println)).start();
        try {Thread.sleep(10000);} catch (InterruptedException e) {e.printStackTrace();}
    }
}
