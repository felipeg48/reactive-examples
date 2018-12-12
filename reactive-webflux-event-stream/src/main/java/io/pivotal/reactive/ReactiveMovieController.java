package io.pivotal.reactive;

import java.time.Duration;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

@RestController
@RequestMapping("/movies")
public class ReactiveMovieController {

    private static final int TEN = 10;
    private final ReactiveMovieRepository reactiveMovieRepository;

    @Autowired
    public ReactiveMovieController(ReactiveMovieRepository reactiveMovieRepository) {
        this.reactiveMovieRepository = reactiveMovieRepository;
    }

    // DONE-reactive-webflux-event-stream-00:
    // - Start "reactive-webflux-event-stream" app

    // DONE-reactive-webflux-event-stream-01:
    // - Do curl -ihttp://localhost:8080/movies
    @GetMapping
    public Flux<Movie> getMovies() {
        return reactiveMovieRepository.findAll();
    }

    // DONE-reactive-webflux-event-stream-02:
    // Do curl http://localhost:8090/movies/numberevents
    @GetMapping(value = "/numberevents", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Long> getNumberEvents() {
        return Flux.interval(Duration.ofSeconds(1))
                   .take(TEN);
    }

    // DONE-reactive-webflux-event-stream-03: Add a method that performs the
    // following
    // -Create an array of some names
    // -When accessed (curl http://localhost:8090/movies/randomNames), it returns
    // random selection from the above names as a TEXT_EVENT_STREAM_VALUE

    // TODO-reactive-webflux-event-stream-04: Do curl
    // http://localhost:8090/movies/movieevents
    @GetMapping(value = "/movieevents", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieEvent> getMovieEvents() {
        Flux<Long> intervals = Flux.interval(Duration.ofSeconds(1))
                                   .take(TEN);
        Flux<MovieEvent> movieevents = Flux.fromStream(
                Stream.generate(() -> new MovieEvent(new Movie("test movie", "55"), new Date())));
        return Flux.zip(intervals, movieevents)
                   .map(Tuple2::getT2);
    }

    // DONE-reactive-webflux-event-stream-05: Add a method that performs the
    // following
    // -Create a method
    // -Use the same array of some names you created above
    // -When accessed (curl http://localhost:8090/movies/randomNamesWithNumbers),
    // it returns t1: <number>, t2: <random selection from the above names>
    // as a TEXT_EVENT_STREAM_VALUE

    // TODO-reactive-webflux-event-stream-06: Do curl -i
    // http://localhost:8090/movies/2/movieevents
    @GetMapping(value = "/{id}/movieevents", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<MovieEvent> getMovieEvents(@PathVariable String id) {
        Mono<Movie> movieMono = reactiveMovieRepository.findById(id);
        return movieMono.flatMapMany(movie -> {
            Flux<Long> intervals = Flux.interval(Duration.ofSeconds(1))
                                       .take(TEN);
            Flux<MovieEvent> movieEvents = Flux.fromStream(Stream.generate(() -> new MovieEvent(movie, new Date())));
            return Flux.zip(intervals, movieEvents)
                       .map(Tuple2<Long, MovieEvent>::getT2);
        });
    }

    // DONE-reactive-webflux-event-stream-07: Add a method that performs the
    // following
    // -Use the same array of some names you created above
    // -Create NameEvent class that contains "name" and "date" fields
    // -When accessed (curl http://localhost:8090/movies/nameevents),
    //  it returns NameEvent as a TEXT_EVENT_STREAM_VALUE
    //  data:{"name":"paul","date":1512180552417}
    //  data:{"name":"bill","date":1512180553423}
    //  data:{"name":"bill","date":1512180554417}
    //  ...

}

// TODO-reactive-webflux-event-stream-10: Take a look at the TO DO list in the
// "reative-webflux-event-stream-client" application
