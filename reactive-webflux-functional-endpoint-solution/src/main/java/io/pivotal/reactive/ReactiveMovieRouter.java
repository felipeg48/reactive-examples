package io.pivotal.reactive;

import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Configuration
public class ReactiveMovieRouter {

	@Bean
	RouterFunction<ServerResponse> createRoutes(HandlerFunctions handlerFunctions) {

		// @formatter:off
		return route(GET("/movies"), handlerFunctions::getAllMovies)
				.andRoute(GET("/movies/{id}"), handlerFunctions::getMovie)
				.andRoute(POST("/movies"),  handlerFunctions::createMovie)
				.andRoute(PUT("/movies/{id}"), handlerFunctions::updateMovie)
				.andRoute(DELETE("/movies/{id}"), handlerFunctions::deleteMovie2);
		// @formatter:on

	}

	// Typically you want to create a Handler class as shown below,
	// which group handler methods. Each handler represents
	// HandlerFunction, which takes ServerRequest as an input
	// and returns Mono<ServerResponse> as return value.
	// These handlers can then be used as Lambda method
	// references inside RouterFunction as shown above, which
	// provides higher readability of the code.
	@Component
	public static class HandlerFunctions {

		private final ReactiveMovieRepository reactiveMovieRepository;

		@Autowired // Not needed since this is the only constructor (Spring 4.3+)
		public HandlerFunctions(ReactiveMovieRepository reactiveMovieRepository) {
			this.reactiveMovieRepository = reactiveMovieRepository;
		}

		// DONE-reactive-webflux-functional-endpoint-00: Start
		// "reactive-webflux-functional-endpoint" app

		// DONE-reactive-webflux-functional-endpoint-01: Do the following
		// - Try curl -i http://localhost:8080/movies
		public Mono<ServerResponse> getAllMovies(ServerRequest request) {
			return ServerResponse	.ok()
									.contentType(MediaType.APPLICATION_JSON_UTF8)
									.body(reactiveMovieRepository.findAll(), Movie.class);
		}

		// DONE-reactive-webflux-functional-endpoint-02: Write a method
		// - Get all movies whose title contains character m
		// - Add another route in createRoutes(..) method above
		// - test it using curl or postMan
		public Mono<ServerResponse> getAllMoviesWhoseTitleContainsM(ServerRequest serverRequest) {
			// perform findAll() using repository
			// filter the movies
			// create Mono<ServerRespone> and return result
			return ServerResponse	.ok()
									.contentType(MediaType.APPLICATION_JSON_UTF8)
									.body(reactiveMovieRepository	.findAll()
																	.filter(movie -> movie	.getTitle()
																							.contains("m")),
											Movie.class);
		}

		// DONE-reactive-webflux-functional-endpoint-03: Do the following
		// - Try curl -i http://localhost:8080/movies/2
		// - Try curl -i http://localhost:8080/movies/2000 - it should return 404
		public Mono<ServerResponse> getMovie(ServerRequest serverRequest) {
			return reactiveMovieRepository	.findById(serverRequest.pathVariable("id"))
											.flatMap(movie -> ServerResponse.ok()
																			.contentType(
																					MediaType.APPLICATION_JSON_UTF8)
																			.body(BodyInserters.fromObject(movie)))
											// We need to use switchIfEmpty (as opposed to defaultIfEmpty)
											// because ServerResponse.notFound().build() returns Mono<ServerResponse>
											// not ServerResponse
											.switchIfEmpty(ServerResponse	.notFound()
																			.build());

		}

		// DONE-reactive-webflux-functional-endpoint-04: Write a method
		// - Return movie title with current date and time
		public Mono<ServerResponse> getMovie2(ServerRequest serverRequest) {
			return reactiveMovieRepository	.findById(serverRequest.pathVariable("id"))
											.flatMap(movie -> {
												movie.setTitle(movie.getTitle() + " " + new Date());
												return ServerResponse	.ok()
																		.contentType(MediaType.APPLICATION_JSON_UTF8)
																		.body(BodyInserters.fromObject(movie));
											})
											.switchIfEmpty(ServerResponse	.notFound()
																			.build());
		}

		// This scheme does not have a way to handle 404
		public Mono<ServerResponse> getMovie3(ServerRequest serverRequest) {

			// extract the movie id from the server request
			// find the requested movie from the repository
			// create server response with the content
			// handle 404 error
			return ServerResponse	.ok()
									.contentType(MediaType.APPLICATION_JSON_UTF8)
									.body(reactiveMovieRepository.findById(serverRequest.pathVariable("id")),
											Movie.class);
		}

		// DONE-reactive-webflux-functional-endpoint-05: Create a movie
		// - Try curl -i -X POST -H "Content-Type:application/json" -d
		// '{"title":"hello", "id":"21"}' http://localhost:8080/movies
		public Mono<ServerResponse> createMovie(ServerRequest serverRequest) {
			return serverRequest.bodyToMono(Movie.class)
								.flatMap(newMovie -> ServerResponse	.ok()
																	.contentType(MediaType.APPLICATION_JSON_UTF8)
																	.body(reactiveMovieRepository.insert(newMovie),
																			Movie.class))
								.doOnError(error -> System.out.println("caught error: " + error));
		}

		// Another way of creating a movie
		public Mono<ServerResponse> createMovie2(ServerRequest serverRequest) {

			// extract the new movie content from server request
			// save the new movie to the repository
			// create server response
			// return 201 response with location field set

			return ServerResponse	.created(null)
									.body(reactiveMovieRepository.insert(serverRequest.bodyToMono(Movie.class)),
											Movie.class);
		}

		// DONE-reactive-webflux-functional-endpoint-06: Update a movie
		// - Try curl -i -X PUT -H "Content-Type:application/json" -d
		// '{"title":"jaws", "id":"3"}' http://localhost:8080/movies/3
		public Mono<ServerResponse> updateMovie(ServerRequest serverRequest) {
			// We need to find the existing movie to update first.
			// We then update it with the newly provided movie and then save,
			// and we need to do all these in reactive manner.
			return reactiveMovieRepository	.findById(serverRequest.pathVariable("id"))
											.flatMap(existingMovie -> {
												return serverRequest.bodyToMono(Movie.class)
																	.flatMap(newMovie -> {
																		existingMovie.setTitle(newMovie.getTitle());
																		return ServerResponse	.ok()
																								.contentType(
																										MediaType.APPLICATION_JSON_UTF8)
																								.body(reactiveMovieRepository.save(
																										existingMovie),
																										Movie.class);
																	});

											})
											.switchIfEmpty(ServerResponse	.notFound()
																			.build());

		}

		// TODO-reactive-webflux-functional-endpoint-07: Write a new method
		// - Rewrite the above logic considering the steps mentioned below
		// 1. find an existing movie using movie id
		// - extract movie id from server request serverRequest.
		// - find from repository
		// - note that it returns Mono<Movie>
		// 2. extract the new movie content from server request
		// 3. update the existing movie with the new content
		// 4. save the updated movie
		// 5. return updated movie
		// 6. set response ok
		// 7.handle 404 error

		public Mono<ServerResponse> updateMovie2(ServerRequest serverRequest) {
			return reactiveMovieRepository	.findById(serverRequest.pathVariable("id"))
											.flatMap(existingMovie -> {
												return serverRequest.bodyToMono(Movie.class)
																	.flatMap(movie -> {
																		existingMovie.setTitle(movie.getTitle());
																		return ServerResponse	.ok()
																								.contentType(
																										MediaType.APPLICATION_JSON_UTF8)
																								.body(reactiveMovieRepository.save(
																										existingMovie),
																										Movie.class);
																	});

											})
											.switchIfEmpty(ServerResponse	.notFound()
																			.build());

		}

		// DONE-reactive-webflux-functional-endpoint-08: Delete a movie
		// - Try curl -i -X DELETE http://localhost:8080/movies/3
		// - Try curl -i -X DELETE http://localhost:8080/movies/2000
		public Mono<ServerResponse> deleteMovie(ServerRequest request) {
			return reactiveMovieRepository	.findById(request.pathVariable("id"))
											.flatMap(movie -> {
												reactiveMovieRepository	.deleteById(request.pathVariable("id"))
																		.subscribe();
												return ServerResponse	.ok()
																		.body(BodyInserters.empty());
											})
											.switchIfEmpty(ServerResponse	.notFound()
																			.build());
		}

		// If you don't care about 404 error for delete, this is cleaner
		public Mono<ServerResponse> deleteMovie2(ServerRequest serverRequest) {

			// get movie id from the server request
			// remove the movie from the repository
			// create and return server response

			return reactiveMovieRepository	.deleteById(serverRequest.pathVariable("id"))
											.then(ServerResponse.ok()
																.body(BodyInserters.empty()));
		}

	}
}

// DONE-reactive-webflux-functional-endpoint-10: Do the following
// - Create "Person" class with "id", "firstname", "lastname", and "age" fields
// (You can copy the same "Person" class if you've done the webflux-controller
// exercise)
// - Create "ReactivePersonRepository" class (same in the controller)
// (You can copy the same "ReactivePersonRepository" class if you've done the
// webflux-controller exercise)
// - Add initialization code that add a few people (same in the controller)
// (You can use the same initialization code if you've done the
// webflux-controller exercise)
// - Create "ReactivePersonRouter" class with CRUD operations
// - Test the above using "curl" or "postMan" REST client
//
// Congratulations you are done for now. We will come back to this
// project later during testing.
