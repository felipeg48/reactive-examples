package io.pivotal.reactive;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitData {
	
	AtomicInteger movieIdCounter = new AtomicInteger(1);

	@Bean
	CommandLineRunner createTestData(ReactiveMovieRepository movieRepository) {
		return args -> {
			movieRepository	.deleteAll()
							.subscribe(null, null, () -> {
								Stream	.of("thor", "avengers", "iron man", "justice league")
										.forEach(title -> {
											movieRepository	.save(new Movie(title, new Integer(movieIdCounter.getAndIncrement()).toString()))
															.subscribe(System.out::println);
										});

							});

		};
	}

}
