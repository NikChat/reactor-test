import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;

public class MonoTest {

    @Test
    void firstMono() {
        Mono.just("A")
            .log()
            .subscribe(); //-> onSubscribe(me param implementation tou Subscription interface) -> request(unbounded) -> onNext(A) -> onComplete()
    }

    @Test
    void monoWithConsumer() {
        Mono.just("A")
                .log()
                .subscribe(s -> System.out.println(s)); //we do something with the published element. Idio, alla meta to onNext(A) kanei sout to A
    }

    @Test
    void monoWithDoOn() {
        Mono.just("A")
                .log()
                .doOnSubscribe(subs -> System.out.println("Subscribed: " + subs))
                .doOnRequest(request -> System.out.println("Request: " + request)) //executed before the request() method
                .doOnSuccess(complete -> System.out.println("Complete: " + complete)) //when the mono completes succesfully
                .subscribe(System.out::println);
    }

    @Test
    void emptyMono() {
        Mono.empty()
                .log()
                .subscribe(System.out::println); //-> onSubscribe(me param implementation tou Subscription interface) -> request() -> onComplete()
    }
    

    @Test
    void emptyCompleteConsumerMono() { //we pass a consumer to the subscribe method, to sout onComplete = 3rd argument
        Mono.empty() //when we want to return nothing (void). Only signal for finished process
                .log()
                .subscribe(System.out::println,
                        null,
                        () -> System.out.println("Done")
                );
    }

    @Test
    void errorRuntimeExceptionMono() {
        Mono.error(new RuntimeException())
                .log()
                .subscribe();
    }

    @Test
    void errorExceptionMono() {
        Mono.error(new Exception())
                .log()
                .subscribe();
    }

    @Test
    void errorConsumerMono() { //we catch the exception (2nd argument sth subscribe method), and rethrow it.
        Mono.error(new Exception())
                .log()
                .subscribe(System.out::println,
                        e -> System.out.println("Error: " + e)
                );
    }

    @Test
    void errorDoOnErrorMono() { //idio me panw
        Mono.error(new Exception())
                .doOnError(e -> System.out.println("Error: " + e))
                .log()
                .subscribe();
    }

    @Test
    void errorOnErrorResumeMono() { //catch exception, rethrow, and return a new mono
        Mono.error(new Exception())
                .onErrorResume(e -> {
                    System.out.println("Caught: " + e);
                    return Mono.just("B");
                })
                .log()
                .subscribe();
    }

    @Test
    void errorOnErrorReturnMono() { //idio
        Mono.error(new Exception())
                .onErrorReturn("B")
                .log()
                .subscribe();
    }
}
