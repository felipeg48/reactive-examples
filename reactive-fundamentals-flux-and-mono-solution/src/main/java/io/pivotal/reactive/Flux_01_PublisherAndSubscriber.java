package io.pivotal.reactive;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Flux_01_PublisherAndSubscriber {

	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(Flux_01_PublisherAndSubscriber.class, args);

		// DONE-reactive-fundamentals-10: Observe Flux creation log
		// - Observe that there is one onSubscribe(), multiple onNext()
		//   one onComplete() method
		Flux.just(1, 2, 3, 4, 5, 6)
			.log()
			.subscribe();
		
		// DONE-reactive-fundamentals-11: Create Flux
		// Create a Flux object from a set of names using 
		// Flux.just ("Jon", "Mary", "Tina", "Ave")
		// and log them as shown above
		Flux.just("Jon", "Mary", "Tina", "Ave")
			.log()
			.subscribe();

		// Create Flux with Subscription
		Flux.just(11, 12, 13, 14, 15, 16)
			.log()
			.subscribe(new Subscriber<Integer>() {
				@Override
				public void onSubscribe(Subscription subscription) {
					subscription.request(3);
				}

				@Override
				public void onNext(Integer integer) {
				}

				@Override
				public void onError(Throwable t) {
				}

				@Override
				public void onComplete() {
				}
			});
		
		// DONE-reactive-fundamentals-12: Create Flux
		// Create a Flux object from a set of names using 
		// Flux.just ("Jon", "Mary", "Tina", "Ave")
		// with a Subscriber object being passed as
		// argument
		Flux.just("Jon", "Mary", "Tina", "Ave")
			.log()
			.subscribe(new Subscriber<String>() {
				@Override
				public void onSubscribe(Subscription subscription) {
					subscription.request(3);
				}

				@Override
				public void onNext(String integer) {
				}

				@Override
				public void onError(Throwable t) {
				}

				@Override
				public void onComplete() {
				}
			});

	}

}
