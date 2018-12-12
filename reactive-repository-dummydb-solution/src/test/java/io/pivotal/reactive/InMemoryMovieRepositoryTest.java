package io.pivotal.reactive;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.pivotal.reactive.InMemoryMovieRepository;
import io.pivotal.reactive.Movie;
import reactor.core.publisher.Mono;

@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
class InMemoryMovieRepositoryTest {

    @Autowired
    InMemoryMovieRepository inMemoryMovieRepository;

    @Test
    void getAllMoviesTest() {
        List<Movie> movies = inMemoryMovieRepository.allMovies()
                                                    .collectList()
                                                    .block();
        assertThat(movies).hasSize(3);
    }

    @Test
    void getMovieTest() {
        Movie movie = inMemoryMovieRepository.getMovie(2)
                                             .block();
        assertThat(movie.getTitle()).isEqualTo("jaws");
    }

    @Test
    void saveAndDeleteMovieTest() {
        inMemoryMovieRepository.saveMovie(Mono.just(new Movie("mario", "drama")))
                               .block();

        List<Movie> movies = inMemoryMovieRepository.allMovies()
                                                    .collectList()
                                                    .block();
        assertThat(movies).hasSize(4);

        inMemoryMovieRepository.deleteMovie(1)
                               .block();

        movies = inMemoryMovieRepository.allMovies()
                                        .collectList()
                                        .block();
        assertThat(movies).hasSize(3);
    }

    // DONE-reactive-repository-dummydb-03: Do the following
    // - Write testing code to verify the updateMovie method
    @Test
    void updateMovieTest() {
        Movie movie = new Movie("whatever", "musical");
        Movie updatedMovie = inMemoryMovieRepository.updateMovie(1, Mono.just(movie))
                                                    .block();
        assertThat(updatedMovie.getTitle()).isEqualTo("whatever");
        Movie retrievedMovie = inMemoryMovieRepository.getMovie(1)
                                                      .block();
        assertThat(retrievedMovie.getTitle()).isEqualTo("whatever");
    }

}
