package io.pivotal.reactive;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class InMemoryMovieRepository implements MovieRepository {

	private Map<Integer, Movie> movies = new HashMap<>();

	public InMemoryMovieRepository() {
		this.movies.put(1, new Movie("avengers", "action"));
		this.movies.put(2, new Movie("jaws", "other"));
		this.movies.put(3, new Movie("iron man", "action"));
	}

	@Override
	public Mono<Movie> getMovie(int id) {
		return Mono.justOrEmpty(this.movies.get(id));
	}

	@Override
	public Flux<Movie> allMovies() {
		return Flux.fromIterable(this.movies.values());
	}

	@Override
	public Mono<Void> saveMovie(Mono<Movie> movieMono) {
		return movieMono.doOnNext(movie -> {
			int id = movies.size() + 1;
			movies.put(id, movie);
			System.out.format("Saved %s with id %d%n", movie, id);
		})
						.then(Mono.empty());
	}

	@Override
	public Mono<Void> deleteMovie(int id) {
		movies.remove(id);
		return Mono.empty();
	}

	// DONE-reactive-repository-dummydb-02: Do the following
	// - Implement updateMovie method
	@Override
	public Mono<Movie> updateMovie(int id, Mono<Movie> movieMono) {
		return movieMono.doOnNext(movie -> {
			movies.put(id, movie);
			System.out.format("Updated %s with id %d%n", movie, id);
		});
	}
	
	// - Also try to reimplement it using map operator 
	public Mono<Movie> updateMovie2(int id, Mono<Movie> movieMono) {
		
		return movieMono.map(movie -> {
			movies.put(id, movie);
			System.out.format("Updated %s with id %d%n", movie, id);
			return movie;
		});
		
	}

}
