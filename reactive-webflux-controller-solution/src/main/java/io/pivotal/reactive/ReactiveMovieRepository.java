package io.pivotal.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReactiveMovieRepository extends ReactiveMongoRepository<Movie, String> {
}
