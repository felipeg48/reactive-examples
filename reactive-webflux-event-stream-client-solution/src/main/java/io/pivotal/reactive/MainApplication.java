package io.pivotal.reactive;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
public class MainApplication {

	private static final String SERVER_ADDRESS = "http://localhost:8090";

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}

	@Bean
	WebClient createWebClient() {
		return WebClient.create(SERVER_ADDRESS);
	}

	// DONE-reactive-webflux-event-stream-client-01: 
	// -Make sure "reactive-webflux-event-stream" server app is running
	// -Start "reactive-webflux-event-stream-client" and observe event
	//  streams in the console
	
	@Bean
	CommandLineRunner ccommandLineRunner1(WebClient webClient) {
		return args -> {
			// Use retrieve
			webClient	.get()
						.uri("http://localhost:8090/movies/1/movieevents")
						.accept(MediaType.TEXT_EVENT_STREAM)
						.retrieve()
						.bodyToFlux(MovieEvent.class)
						.subscribe(System.out::println);

			// Use exchange with flatMapMany
			webClient	.get()
						.uri("http://localhost:8090/movies/2/movieevents")
						.accept(MediaType.TEXT_EVENT_STREAM)
						.exchange()
						.flatMapMany(clientResponse -> clientResponse.bodyToFlux(MovieEvent.class))
						.subscribe(System.out::println);

			// Use exchange with subscribe
			webClient	.get()
						.uri("http://localhost:8090/movies/3/movieevents")
						.accept(MediaType.TEXT_EVENT_STREAM)
						.exchange()
						.subscribe(clientResponse -> clientResponse	.bodyToFlux(MovieEvent.class)
																	.subscribe(System.out::println));
		};
	}
	
	// DONE-reactive-webflux-event-stream-client-02:
	// -Access the NameEvent stream using "retrieve/bodyToFlux", 
	// "exchange/flatMapMany", "exchange/subscribe/bodyToFlux"
	// as shown above
	@Bean
	CommandLineRunner ccommandLineRunner3(WebClient webClient) {
		return args -> {
			// Use retrieve
			webClient	.get()
						.uri("http://localhost:8090/movies/nameevents")
						.accept(MediaType.TEXT_EVENT_STREAM)
						.retrieve()
						.bodyToFlux(MovieEvent.class)
						.subscribe(System.out::println);

			// Use exchange with flatMapMany
			webClient	.get()
						.uri("http://localhost:8090/movies/nameevents")
						.accept(MediaType.TEXT_EVENT_STREAM)
						.exchange()
						.flatMapMany(clientResponse -> clientResponse.bodyToFlux(NameEvent.class))
						.subscribe(System.out::println);

			// Use exchange with subscribe
			webClient	.get()
						.uri("http://localhost:8090/movies/nameeventss")
						.accept(MediaType.TEXT_EVENT_STREAM)
						.exchange()
						.subscribe(clientResponse -> clientResponse	.bodyToFlux(NameEvent.class)
																	.subscribe(System.out::println));
		};
	}

	// DONE-reactive-webflux-event-stream-client-03:
	// -Access the MovieEvent stream for the first movie for which 
	//  the number of characters of the movie title is less than 5 
	@Bean
	CommandLineRunner commandLineRunner2(WebClient webClient) {
		return args -> {
			// Search for a particular movie ("avengers" in the example below) and
			// retrieve events
			webClient	.get()
						.uri("/movies")
						.retrieve()
						.bodyToFlux(Movie.class)
						.filter(movie -> movie	.getTitle()
												.equals("avengers"))
						.subscribe(movie -> {
							webClient	.get()
										.uri("http://localhost:8090/movies/" + movie.getId() + "/movieevents")
										.accept(MediaType.TEXT_EVENT_STREAM)
										.retrieve()
										.bodyToFlux(MovieEvent.class)
										.subscribe(System.out::println);
						});
		};
	}
}
