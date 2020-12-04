import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;

public class OperatorTest { //map, flatMap, concat and merge, zip

    @Test
    void map() {
        Flux.range(1, 5)
                .map(i -> i * 10)
                .subscribe(System.out::println);
    }

    @Test
    void flatMap() { 
        Flux.range(1, 5)
                .flatMap(i -> Flux.range(i*10, 2)) //transforms its element of the flux into a flux or mono
                .subscribe(System.out::println); //at the end, flatMap will flatten all the publishers into a single one
    }

    @Test
    void flatMapMany() {
        Mono.just(3)
                .flatMapMany(i -> Flux.range(1, i)) //mono operator: convert a mono into a flux
                .subscribe(System.out::println);
    }

    @Test
    void concat() throws InterruptedException {
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200)); //operator that will delay the publishing of each element
        Flux<Integer> sixToTen = Flux.range(6, 5)
                .delayElements(Duration.ofMillis(400));

        Flux.concat(oneToFive, sixToTen)
                .subscribe(System.out::println); //emfanizei 1 2 3 4 5 6 7 8 9 10

//        oneToFive.concatWith(sixToTen)
//              .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    void merge() throws InterruptedException  { //merge doesn't combine the publishers in a sequential way
        Flux<Integer> oneToFive = Flux.range(1, 5)
                .delayElements(Duration.ofMillis(200));
        Flux<Integer> sixToTen = Flux.range(6, 5)
                .delayElements(Duration.ofMillis(400));

        Flux.merge(oneToFive, sixToTen)
                .subscribe(System.out::println); // emfanizei 1 6 2 3 7 4 5 8 9 10

//        oneToFive.mergeWith(sixToTen)
//                .subscribe(System.out::println);

        Thread.sleep(4000);
    }

    @Test
    void zip()  { //combines 2 publishers by waiting for all the sources to emit one element, and combine this element into an output value
    			// according to the provided function
        Flux<Integer> oneToFive = Flux.range(1, 5);
        Flux<Integer> sixToTen = Flux.range(6, 5);

        Flux.zip(oneToFive, sixToTen,
                (item1, item2) -> item1 + ", " + + item2) //the same for all elements, until any of the sources completes
                .subscribe(System.out::println);

//        oneToFive.zipWith(sixToTen)
//                .subscribe(System.out::println);
    }
   
}
