package io.pivotal.reactive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Attributes.Name;
import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Flux_03_Creation {

	public static void main(String[] args) {
		SpringApplication.run(Flux_03_Creation.class, args);

		// Create Flux from values
		Flux.just("red", "white", "pink")
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-30: Check Flux Javadoc from the link below
		// https://projectreactor.io/docs/core/release/api/reactor/core/publisher/Flux.html

		// DONE-reactive-fundamentals-31: Create Flux from fruits,
		// "apple", "orange","grape" and display them (to System output)
		Flux.just("apple", "orange", "grape")
			.subscribe(System.out::println);

		// Create Flux from an array
		Flux.fromArray("1,2,3,4,5,6,7".split(","))
			.map(Integer::parseInt)
			.filter(i -> i % 2 == 0)
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-32: Create Flux from an array
		// of some names, for example, "jon", "peter", "john", "mary"
		// and display only the ones that starts with "j"
		Flux.just("jon", "peter", "john", "mary")
			.filter(name -> name.startsWith("j"))
			.subscribe(System.out::println);

		// Create Flux from Iterable
		List<Integer> items = new ArrayList<>();
		Flux.just(10, 20, 30, 40)
			.subscribe(items::add);
		Flux.fromIterable(items)
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-33: Create Flux from an ArrayList of some names,
		// for example, "jon", "peter", "john", "mary"
		// and display only the ones whose length is 4
		Flux.fromIterable(Arrays.asList("jon", "peter", "john", "mary"))
			.filter(name -> name.length() == 4)
			.subscribe(System.out::println);

		// Create Flux from a Stream
		Flux.fromStream(Stream.of(100, 200, 300, 400, 500))
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-34: Create Flux from a stream of some names,
		// for example, "jon", "peter", "john", "mary"
		// and display only the ones whose length is 4
		// after converting them to upper case
		Flux.fromStream(Stream.of("jon", "peter", "john", "mary"))
			.map(name -> name.toUpperCase())
			.filter(name -> name.length() == 4)
			.subscribe(System.out::println);

		// Create Flux from two streams using zipWith(..)
		Flux.range(10, 20)
			.zipWith(Flux.just("one", "two", "three", "four"))
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-35: Create Flux from a range of 100 to 110
		// and zipWith them with another range of 1000 to 1010
		Flux.range(100, 10)
			.zipWith(Flux.range(1000, 10))
			.subscribe(System.out::println);

		// Create Flux from two streams using zip(..)
		Flux.zip(Flux.just(21, 22, 23, 24), Flux.just("one", "two", "three", "four"))
			.subscribe(System.out::println);

		// DONE-reactive-fundamentals-36: Create Flux from a range of 100 to 110
		// and zip them with another range of 1000 to 1010
		Flux.zip(Flux.range(100, 10), Flux.range(1000, 10))
		.subscribe(System.out::println);
	}

}
