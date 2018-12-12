package io.pivotal.reactive;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/movies")
public class ReactiveMovieController {

	private final ReactiveMovieRepository reactiveMovieRepository;

	@Autowired
	public ReactiveMovieController(ReactiveMovieRepository reactiveMovieRepository) {
		this.reactiveMovieRepository = reactiveMovieRepository;
	}

	// DONE-reactive-webflux-controller-00: Start "reactive-webflux-controller" app

	// DONE-reactive-webflux-controller-01: Get all movies
	// - Try curl -i http://localhost:8080/movies
	@GetMapping
	public Flux<Movie> getMovies() {
		return reactiveMovieRepository.findAll();
	}

	// Get a movie option #1
	// DONE-reactive-webflux-controller-02: Get a movie
	// - Try curl -i http://localhost:8080/movies/2
	// - Try curl -i http://localhost:8080/movies/option2/2000 - returns 404
	@GetMapping("/{id}")
	public Mono<Movie> getMovie(@PathVariable(value = "id") String movieId) {
		return reactiveMovieRepository.findById(movieId)
		                              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	// Get a movie option #2
	// - Try curl -i http://localhost:8080/movies/option2/2
	// - Try curl -i http://localhost:8080/movies/option2/2000 - returns 404
	@GetMapping("/option2/{id}")
	public Mono<ResponseEntity<Movie>> getMovieById(@PathVariable(value = "id") String movieId) {
		return reactiveMovieRepository.findById(movieId)
		                              .map(movie -> ResponseEntity.ok(movie))
		                              .defaultIfEmpty(ResponseEntity.notFound()
		                                                            .build());
	}

	// Get a movie option #3
	// - Try curl -i http://localhost:8080/movies/option2/2
	// - Try curl -i http://localhost:8080/movies/option2/2000 - returns 404
	@GetMapping("/option3/{id}")
	public Mono<ResponseEntity<Movie>> getMovieByid(@PathVariable(value = "id") String movieId) {

		return reactiveMovieRepository.findById(movieId)
		                              .map(movie -> {
			                              return new ResponseEntity<Movie>(movie, HttpStatus.OK);
		                              })
		                              .defaultIfEmpty(ResponseEntity.notFound()
		                                                            .build());
	}

	// TODO-reactive-webflux-controller-03: Add a new movie
	// - Try curl -i -X POST -H "Content-Type:application/json" -d
	// '{"title":"passion!", "id":"11"}' http://localhost:8080/movies
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Movie> addMovie1(@Valid @RequestBody Movie movie) {
		return reactiveMovieRepository.insert(movie)
		                              .doOnError(error -> System.out.println("--->caught error: " + error));
	}

	// TODO-reactive-webflux-controller-04: Add a new movie
	@PostMapping("/option2")
	public Mono<ResponseEntity<Movie>> addMovie2(@Valid @RequestBody Movie movie) {
		return reactiveMovieRepository.insert(movie)
		                              .map(addedMovie -> ResponseEntity.created(null)
		                                                               .body(addedMovie));
	}

	// You can also set custom response header.
	// Verify the customer header via "-v" curl option below
	// curl -v -X POST -H "Content-Type:application/json" -d
	// '{"title":"passion!", "id":"12"}' http://localhost:8080/movies/option3
	@PostMapping("/option3")
	public Mono<ResponseEntity<Movie>> addMovie3(@Valid @RequestBody Movie movie) {
		return reactiveMovieRepository.insert(movie)
		                              .map(addedMovie -> {
			                              HttpHeaders responseHeaders = new HttpHeaders();
			                              responseHeaders.set("MyResponseHeader", "MyValue");
			                              return new ResponseEntity<>(addedMovie, responseHeaders,
			                                      HttpStatus.CREATED);
		                              });
	}

	// DONE-reactive-webflux-controller-05: Update a movie
	// - Try curl -i -X PUT -H "Content-Type:application/json" -d
	// '{"title":"jaws", "id":"3"}' http://localhost:8080/movies/3
	@PutMapping("/{id}")
	public Mono<Movie> updateMovie(@PathVariable(value = "id") String movieId, @Valid @RequestBody Movie movie) {
		return reactiveMovieRepository.findById(movieId)
		                              // Here we need to use flatMap because we are calling save(..)
		                              // method, which returns another Flux object
		                              .flatMap(existingMovie -> {
			                              existingMovie.setTitle(movie.getTitle());
			                              return reactiveMovieRepository.save(existingMovie);
		                              })
		                              .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	// DONE-reactive-webflux-controller-06: Respond with your own custom header
	@PutMapping("/option2/{id}")
	public Mono<ResponseEntity<Movie>> updateMovie2(@PathVariable(value = "id") String movieId,
	        @Valid @RequestBody Movie movie) {
		return reactiveMovieRepository.findById(movieId)
		                              .flatMap(existingMovie -> {
			                              existingMovie.setTitle(movie.getTitle());
			                              return reactiveMovieRepository.save(existingMovie);
		                              })
		                              .map(updatedMovie -> new ResponseEntity<>(updatedMovie, HttpStatus.OK))
		                              .defaultIfEmpty(ResponseEntity.notFound()
		                                                            .build());
	}

	@PutMapping("/option3/{id}")
	public Mono<ResponseEntity<Movie>> updateMovie3(@PathVariable(value = "id") String movieId,
	        @Valid @RequestBody Movie movie) {

		return reactiveMovieRepository.findById(movieId)
		                              .flatMap(existingMovie -> {
			                              existingMovie.setTitle(movie.getTitle());
			                              return reactiveMovieRepository.save(existingMovie);
		                              })
		                              .map(ResponseEntity::ok)
		                              .defaultIfEmpty(ResponseEntity.notFound()
		                                                            .build());
	}

	@DeleteMapping("/{id}")
	public Mono<Void> deleteMovie(@PathVariable String id) {
		return reactiveMovieRepository.deleteById(id);
	}

}

// DONE-reactive-webflux-controller-10: Do the following
// - Create "Person" class with "id", "firstname", "lastname", and "age" fields
// - Create "ReactivePersonRepository" class
// - Add initialization code that add a few people in the "initData" class
// - Create "ReactivePersonController" class with CRUD operations
// - Test the above using "curl" or "postMan" REST client
//
// Congratulations you are done for now. We will come back to this
// project later during testing.
