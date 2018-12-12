package io.pivotal.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Mono;

@SpringBootApplication
public class MainApplication {
	
	private static MovieRepository movieRepository;

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(MainApplication.class, args);
		
		movieRepository = new InMemoryMovieRepository();
		
		movieRepository.allMovies().subscribe(System.out::println);
		movieRepository.getMovie(1).subscribe(System.out::println);
		movieRepository.saveMovie(Mono.just(new Movie("new movie", "romance"))).block();
		movieRepository.getMovie(4).subscribe(System.out::println);
		
		movieRepository.allMovies().subscribe(System.out::println);
		movieRepository.deleteMovie(4);
		Thread.sleep(3000);
	}
}


