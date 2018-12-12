package io.pivotal.reactive;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
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
											movieRepository	.save(new Movie(title,
													new Integer(movieIdCounter.getAndIncrement()).toString()))
															.subscribe(System.out::println);
										});

							});

		};
	}

	@Autowired
	ReactivePersonRepository reactivePersonRepository;

	@Bean
	CommandLineRunner commandLineRunner() {
		return args -> {
			reactivePersonRepository.deleteAll()
									.then(reactivePersonRepository.save(new Person("1", "mary", "mac", 33)))
									.then(reactivePersonRepository.save(new Person("2", "jon", "polla", 44)))
									.then(reactivePersonRepository.save(new Person("3", "paul", "chapman", 55)))
									.then(reactivePersonRepository.save(new Person("4", "sang", "shin", 66)))
									.subscribe(System.out::println);
		};

	}

}
