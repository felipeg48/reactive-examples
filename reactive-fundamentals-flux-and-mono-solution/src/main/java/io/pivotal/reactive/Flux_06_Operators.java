package io.pivotal.reactive;

import java.util.Arrays;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Flux_06_Operators {

	public static void main(String[] args) {
		SpringApplication.run(Flux_06_Operators.class, args);
		
		// Use map to convert words into uppercase
		Flux.just("one", "two", "three")
			.map(word -> word.toUpperCase())
			.subscribe(System.out::println);
		
		// DONE-reactive-fundamentals-61: Create Flux from an 
		// ArrayList of some names, for example, "jon", "peter", "john"
		// and capitalize the names, for example, "Jon", "Peter", "John"
		Flux.fromIterable(Arrays.asList("jon", "peter", "john"))
			.map(name -> name.substring(0, 1).toUpperCase()
					   + name.substring(1).toLowerCase())
			.subscribe(System.out::println);

		// Use flatMap to covert words into uppercase
		Flux.just("four", "five", "six")
			.flatMap(word -> Flux.just(word.toUpperCase())) 
			.subscribe(System.out::println);
		
		// DONE-reactive-fundamentals-62: Create Flux from an 
		// ArrayList of some names, for example, "jon", "peter", "john"
		// with the length of the name appended at the end, for 
		// example, to "jon3", "peter5", "john4" using flatMap
		Flux.fromIterable(Arrays.asList("jon", "peter", "john"))
		    .flatMap(name -> Flux.just(name+name.length()))
		    .subscribe(System.out::println);

		
		// Use chained operators
		Flux.fromIterable(Arrays.asList("one", "two", "three"))
			.flatMap(word -> Flux.fromArray(word.split("")))
			.distinct()
			.sort()
			.zipWith(Flux.range(1, Integer.MAX_VALUE), (string, count) -> String.format("%2d. %s", count, string))
			.subscribe(System.out::println);

		// Convert Flux to Iterable
		Flux.just(1, 2, 3, 4)
			.toIterable()
			.forEach(System.out::println);
		
		// DONE-reactive-fundamentals-63: Create Flux from an 
		// ArrayList of some names, for example, "jon", "peter", "john"
		// and convert them into Iterable
		Flux.fromIterable(Arrays.asList("jon", "peter", "john"))
			.toIterable()
			.forEach(System.out::println);
		
		// Convert Flux to Mono<List<String>> and then to List<String>
		Flux.just("red", "white", "blue")
			.collectList()    // Mono<List<String>>
			.block()          // List<String>
			.forEach(System.out::println);
		
		// DONE-reactive-fundamentals-64: Create Flux from a range of
		// Integer between 10 to 20 and convert them to Mono<List<Integer>>
		// and then to List<Integer>
		Flux.range(10, 10)
			.collectList() // Mono<List<String>>
			.block() // List<String>
			.forEach(System.out::println);
	}

}
