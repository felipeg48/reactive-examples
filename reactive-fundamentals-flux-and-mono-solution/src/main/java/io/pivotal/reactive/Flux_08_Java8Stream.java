package io.pivotal.reactive;

import java.util.stream.Stream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Flux_08_Java8Stream {

	public static void main(String[] args) {
		SpringApplication.run(Flux_08_Java8Stream.class, args);

		//
		// Compare imperative code vs Java Lambda stream vs Reactive code
		//
		
		String[] colorArray = { "blue", "green", "red", "orange", "purple" };

		// Imperative - Iterable
		for (String color : colorArray) {
			if (color.contains("r")) {
				System.out.println("Iterating: " + color.toUpperCase());
			}
		}

		// Imperative - Java 8 Streams
		Stream	.of(colorArray)
				.filter(c -> c.contains("r"))
				.map(c -> c.toUpperCase())
				.forEach(color -> System.out.println("Stream: " + color));

		// Reactive
		Flux.fromArray(colorArray)
			.filter(c -> c.contains("r"))
			.map(c -> c.toUpperCase())
			.subscribe(color -> System.out.println("Flux: " + color));

	}

}
