package io.pivotal.reactive;

import java.time.Duration;
import java.util.Date;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class Flux_05_DoOperations {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Flux_05_DoOperations.class, args);

		// Take action for each item emitted using doOnNext and doOnComplete
		Flux.just("four", "five", "six")
			.doOnNext(word -> System.out.println(word.toUpperCase() + " via doOnNext"))
			.doOnComplete(() -> System.out.println("Completed via doOnComplete"))
			.subscribe();

		// DONE-reactive-fundamentals-51: Generate a stream of numbers in the range of 5
		// to 10
		// and for each number, compute the square value of it using "doOnNext".
		// Display "No more numbers" when complete.
		Flux.range(5, 5)
			.doOnNext(number -> System.out.println(number * number + " via doOnNext"))
			.doOnComplete(() -> System.out.println("No more numbers"))
			.subscribe();

		// Take action for each item emitted using subscribe
		Flux.just("four", "five", "six")
			.subscribe(word -> System.out.println(word.toUpperCase() + " via subscribe"), 
					null,
					() -> System.out.println("Completed via subscribe"));

		// DONE-reactive-fundamentals-51: Generate a stream of numbers in the range
		// of 5 to 10 and for each number, compute the square value of it using
		// "subscribe".
		// Display "No more numbers" when complete.
		Flux.range(5, 5)
			.subscribe(number -> System.out.println(number * number + " via doOnNext"), 
					null,
					() -> System.out.println("No more numbers"));

		// Catch an exception using doOnError
		Flux.error(new IllegalStateException("some state error"))
			.doOnError(e -> System.out.println("Caught Flux error using doOnError"))
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-52: Generate a stream of numbers in the range
		// of 5 to 10 and for each number, when it reaches number 9, generate
		// an exception and display error message saying "Number 9 cannot be used".
		Flux.range(5, 5)
		    .filter(number-> number==9)
			.flatMap(number -> {
				return Flux.error(new IllegalStateException("some state error"));
			})
			.doOnError(e -> System.out.println("Number 9 cannot be used"))
			.subscribe(System.out::println);

		// Catch an exception using subscribe
		Flux.error(new IllegalStateException("some state error"))
			.subscribe(null, e -> System.out.println("Caught Flux error using subscribe"), System.out::println);

		// Switch to another flux if current flux completes without any data
		Flux.empty()
			.switchIfEmpty(Flux.just("Empty flux via switchIfEmpty"))
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-53: Handle empty flux via a new Flux
		// with current Date() object (instead of string as shown above)
		Flux.empty()
			.switchIfEmpty(Flux.just(new Date()))
			.subscribe(System.out::println);

		// Provide default value if current flux completes without any data
		Flux.empty()
			.defaultIfEmpty("Empty flux via defaultIfEmpty")
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-54: Handle empty flux via current
		// Date() object
		Flux.empty()
			.defaultIfEmpty(Flux.just(new Date()))
			.subscribe(System.out::println);

		// Generate a continuous stream of values using "interval"
		Flux.interval(Duration.ofSeconds(1))
			.map(item -> "tick: " + item)
			.take(5)
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-55: Generate a continuous stream
		// of values and display the square value of it
		Flux.interval(Duration.ofSeconds(1))
			.map(number -> "square number: " + number*number)
			.take(5)
			.subscribe(System.out::println);

		// Generate a continuous stream of values from a Java 8 stream
		Flux.fromStream(Stream.generate(() -> new Date()))
			.take(6)
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-56: Generate a continuous stream
		// of random numbers from a Java 8 stream and display the
		// square value of it
		Flux.fromStream(Stream.generate(() -> new Random().nextInt(100)))
			.take(6)
			.subscribe(number->System.out.println("random number: " + number));

		// Dispose a stream
		Disposable disposable = Flux.fromStream(Stream.generate(() -> new Date()))
									.take(5)
									.subscribe(date -> System.out.println("To be disposed: " + date));

		disposable.dispose();

		// Wait 5 seconds to give enough time for the above to finish
		Thread.sleep(5000);

	}

	static Flux<Void> methodThatReturnsVoid() {
		// Do something ...
		// Then return Flux<Void>
		return Flux.empty();
	}

}
