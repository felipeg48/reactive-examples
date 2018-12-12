package io.pivotal.reactive;

import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class MainApplication {

	private static final String SERVER_ADDRESS = "http://localhost:8080";

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	@Bean
	WebClient createWebClient() {
		return WebClient.create(SERVER_ADDRESS);
	}

	@Bean
	CommandLineRunner commandLineRunner1(WebClient webClient) {
		return args -> {

			// Get all movies
			webClient	.get()
						.uri("/movies")
						.retrieve()
						.bodyToFlux(Movie.class)
						.subscribe(movie->System.out.println("get all movies: " + movie));

			// Get a movie using "retrieve" operator
			webClient	.get()
						.uri("/movies/{id}", 1)
						.retrieve()  // shortcut to extract the response body
						.bodyToMono(Movie.class)
						.subscribe(movie->System.out.println("get movie: " + movie));
			
			// Get a movie using "exchange" operator
			webClient	.get()
						.uri("/movies/{id}", 1)
						.exchange() 
						.flatMap(response -> response.bodyToMono(Movie.class))
						.subscribe(movie->System.out.println("get movie: " + movie));

			// Create a new movie
			String newMovieId = new Integer(new Random().nextInt(1000)).toString();
			webClient	.post()
						.uri("/movies")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(new Movie("new movie", newMovieId)), Movie.class)
						.retrieve()  // shortcut to extract the response body
						.bodyToMono(Movie.class)
						.subscribe(movie->System.out.println("create movie: " + movie));

			// Update a movie
			webClient	.put()
						.uri("/movies/{id}", 2)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(new Movie("movie2 updated", "2")), Movie.class)
						.retrieve()  // shortcut to extract the response body
						.bodyToMono(Movie.class)
						.subscribe(movie->System.out.println("update movie: " + movie));

		};
	}
	
	// DONE-reactive-webflux-client-10: Do the following
	// - Create testing code against ReactivePersonController
	// - Run the same code against Functional endpoint
	// - Please try not to copy and paste code, instead, write
	//   each line of code yourself.
	@Bean
	CommandLineRunner commandLineRunner2(WebClient webClient) {
		return args -> {

			// Get all people
			webClient	.get()
						.uri("/people")
						.retrieve()
						.bodyToFlux(Person.class)
						.subscribe(person->System.out.println("get all people: " + person));

			// Get a person using "retrieve" operator
			webClient	.get()
						.uri("/people/{id}", 1)
						.retrieve()  // shortcut to extract the response body
						.bodyToMono(Person.class)
						.subscribe(person->System.out.println("get person: " + person));
			
			// Get a person using "exchange" operator
			webClient	.get()
						.uri("/people/{id}", 1)
						.exchange() 
						.flatMap(response -> response.bodyToMono(Person.class))
						.subscribe(person->System.out.println("get person: " + person));

			// Create a new person
			String newPersonId = new Integer(new Random().nextInt(1000)).toString();
			webClient	.post()
						.uri("/people")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(new Person(newPersonId, "sang", "shin", 99)), Person.class)
						.retrieve()  // shortcut to extract the response body
						.bodyToMono(Person.class)
						.subscribe(person->System.out.println("create person: " + person));

			// Update a person
			webClient	.put()
						.uri("/people/{id}", 2)
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(new Person("2", "paul", "chapman", 88)), Person.class)
						.retrieve()  // shortcut to extract the response body
						.bodyToMono(Person.class)
						.subscribe(person->System.out.println("update person: " + person));

		};
	}

}

