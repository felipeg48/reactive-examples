package io.pivotal.reactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieRepository {

	Mono<Movie> getMovie(int id);
	Flux<Movie> allMovies();
	Mono<Void> saveMovie(Mono<Movie> movie);
	Mono<Void> deleteMovie(int id);
	
	// DONE-reactive-repository-dummydb-01: Do the following
	// - Add updateMovie method in reactive manner, it should receive
	//   Mono<Movie> as the 2nd argument (the first argument is
	//   movie id) and should return updated Movie as Mono<Movie>
    Mono<Movie> updateMovie(int id, Mono<Movie> movie);
}

// TODO-reactive-repository-dummydb-11 (Optional): Do the following
// - Create "Person" class with "firstname", "lastname", "age" fields
// - Create "PersonRepository" interface using Reactive API
//   similar to the above
// - Implement corresponding "InMemoryPersonRepository" class
//   (similar to "InMemoryMovieRepository" class)
// - Write "InMemoryPersonRepositoryTests" class, which 
//   performs the testing (similar to "InMemoryMovieRepositoryTest")
