package io.pivotal.reactive;

import java.util.ArrayList;
import java.util.List;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class Flux_02_BackPressure {

	public static void main(String[] args) {
		SpringApplication.run(Flux_02_BackPressure.class, args);

		List<Integer> items = new ArrayList<>();

		// Create Flux WITHOUT back-pressure (or back-pressure with Long.MAX_VALUE)
		// DONE-reactive-fundamentals-20: Do the following
		// - Comment out the line that request back-pressure inside the onSubscribe
		// method and observe that there is no data stream
		Flux.just(1, 2, 3, 4, 5, 6)
			.log()
			.subscribe(new Subscriber<Integer>() {
				@Override
				public void onSubscribe(Subscription s) {
					s.request(Long.MAX_VALUE);
				}

				@Override
				public void onNext(Integer integer) {
					items.add(integer);
				}

				@Override
				public void onError(Throwable t) {
				}

				@Override
				public void onComplete() {
				}
			});

		// Create flux WITH back-pressure - receive up to 2 numbers
		Flux.just(1, 2, 3, 4, 5, 6)
			.log()
			.subscribe(new Subscriber<Integer>() {
				private Subscription s;
				int onNextAmount;

				@Override
				public void onSubscribe(Subscription s) {
					this.s = s;
					s.request(2);
				}

				@Override
				public void onNext(Integer integer) {
					items.add(integer);
					onNextAmount++;
					if (onNextAmount % 2 == 0) {
						s.request(2);
					}
				}

				@Override
				public void onError(Throwable t) {
				}

				@Override
				public void onComplete() {
				}
			});

		// DONE-reactive-fundamentals-21: Create Flux with a
		// range of 10 to 100 (use Flux.range(10, 90)) with
		// back-pressure with incrementing number, in other words,
		// the first back-pressure starts with 2, then 3, then
		// 4 and so on until it reaches 10. Verify the behavior
		// through logs.
		Flux.range(10, 90)
			.log()
			.subscribe(new Subscriber<Integer>() {
				private Subscription s;
				int backPressureCounter = 2;

				@Override
				public void onSubscribe(Subscription s) {
					this.s = s;
					s.request(backPressureCounter);
				}

				@Override
				public void onNext(Integer integer) {
					items.add(integer);
					if (++backPressureCounter <= 10) {
						s.request(backPressureCounter);
					}
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
