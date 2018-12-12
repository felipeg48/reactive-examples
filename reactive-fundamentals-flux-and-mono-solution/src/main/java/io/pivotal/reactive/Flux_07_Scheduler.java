package io.pivotal.reactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@SpringBootApplication
public class Flux_07_Scheduler {

	public static void main(String[] args) {
		SpringApplication.run(Flux_07_Scheduler.class, args);
		
		// Use a fixed pool of single-threaded ExecutorService-based workers
		Flux.just(1, 2, 3, 4)
			.log()
			.map(i -> i * 2)
			.subscribeOn(Schedulers.parallel())
			.subscribe(System.out::println);
		
		// TODO-reactive-fundamentals-71: Create Flux from an ArrayList of some names,
		// for example, "jon", "peter", "john", "mary"
		// and capitalize the names, for example, "Jon", "Peter",
		// "John", "Peter" using parallel scheduler

		// Use single-threaded ExecutorService-based worker
		Flux.just(1, 2, 3, 4)
			.log()
			.map(i -> i * 2)
			.subscribeOn(Schedulers.single())
			.subscribe(System.out::println);
	}

}
