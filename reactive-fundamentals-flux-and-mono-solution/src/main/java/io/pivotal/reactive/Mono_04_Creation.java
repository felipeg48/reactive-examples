package io.pivotal.reactive;

import java.util.Date;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class Mono_04_Creation {

	public static void main(String[] args) {
		SpringApplication.run(Mono_04_Creation.class, args);

		// Create a Mono object
		Mono.just("Pivotal")
			.map(String::toUpperCase)
			.map(word -> word.concat(" makes software design fun!"))
			.subscribe(System.out::println);

		// TODO-reactive-fundamentals-40: Check Mono Javadoc from the link below
		// https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Mono.html

		// TODO-reactive-fundamentals-41: Create Mono from a number, like 10, and
		// convert it to square value like 100 and then display
		Mono.just(10)
			.map(number -> number * number)
			.subscribe(System.out::println);

		// Catch an exception
		Mono.error(new IllegalStateException("some state error"))
			.doOnError(e -> {
				System.out.println("caught Mono error");
			})
			.subscribe(System.out::println);
 		
		// Handle empty Mono via defaultIfEmpty
		Mono.empty()
			.defaultIfEmpty("Empty Mono is handled via defaultIfEmpty")
			.subscribe(System.out::println);

		// TODO-reactive-fundamentals-42: Handle empty Mono using "defaultIfEmpty"
		// displaying "Empty mono occurs at <Current date and time>"
		Mono.empty()
			.defaultIfEmpty("Empty mono occurs at " + new Date())
			.subscribe(System.out::println);

		// Handle empty Mono via switchIfEmpty
		Mono.empty()
			.switchIfEmpty(Mono.just("Empty Mono is handled via switchIfEmpty"))
			.subscribe(System.out::println);

		// TODO-reactive-fundamentals-43: Handle empty Mono using "switchIfEmpty"
		// displaying "Empty mono occurs at <Current date and time>"
		Mono.empty()
			.switchIfEmpty(Mono.just("Empty mono occurs at " + new Date()))
			.subscribe(System.out::println);

	}

}
