package io.pivotal.reactive;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//DONE-reactive-repository-mongodb-00: Start MongoDB

//DONE-reactive-repository-mongodb-01: Do the following
//- Study all the methods that are available from ReactiveMovieRepository
//interface and its parent interfaces (using Outline view of IDE)

public interface ReactiveMovieRepository extends ReactiveMongoRepository<Movie, String> {
	Flux<Movie> findByTitle(String title);

	@Query("{ 'title': ?0, 'genre': ?1}")
	Mono<Movie> findByTitleAndGenre(String Title, String Genre);
	
	Flux<Movie> findByGenre(Mono<String> Genre);
	Mono<Movie> findByTitleAndGenre(Mono<String> Title, String Genre);
	
	/**
	 * Use a tailable cursor to emit a stream of entities as new entities 
	 * are written to the capped collection.
	 */
	@Tailable
	Flux<Movie> findWithTailableCursorBy();
}