package io.pivotal.reactive;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RunWith(JUnitPlatform.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ReactiveMovieRepositoryTest {

	@Autowired
	ReactiveMovieRepository repository;
	@Autowired
	ReactiveMongoOperations operations;

	@BeforeEach
	void setUp() {

		operations	.collectionExists(Movie.class)
					.flatMap(exists -> exists ? operations.dropCollection(Movie.class) : Mono.just(exists))
					.then(operations.createCollection(Movie.class, CollectionOptions.empty()
																					.size(1024 * 1024)
																					.maxDocuments(100)))
					.block();

		repository	.saveAll(Flux.just(new Movie("jaws", "romance"), new Movie("avengers", "action"),
				                       new Movie("sound of music", "comic"), new Movie("movie4", "action")))
					.then()
					.block();

	}

	/**
	 * Note that the all object conversions are performed before the results are
	 * printed to the console.
	 */
	@Test
	void shouldPerformConversionBeforeResultProcessing() throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		repository	.findAll()
					.doOnNext(movie -> System.out.println("after findAll(): " + movie.toString()))
					.doOnComplete(countDownLatch::countDown)
					.doOnError(throwable -> countDownLatch.countDown())
					.subscribe();

		countDownLatch.await();
	}

	// DONE-reactive-repository-mongodb-00: Do the following
	// - Study all the methods that are available from ReactiveMovieRepository
	// interface
	// - Study the interface hierarchy of the ReactiveMovieRepository interface

	// DONE-reactive-repository-mongodb-01: Do the following
	// - Add filter to select only the movies whose title contains a character 'm'
	@Test
	void shouldPerformConversionBeforeResultProcessing2() throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		repository	.findAll()
					.filter(movie -> movie	.getTitle()
											.contains("m"))
					.doOnNext(movie -> System.out.println("after findAll(): " + movie.toString()))
					.doOnComplete(countDownLatch::countDown)
					.doOnError(throwable -> countDownLatch.countDown())
					.subscribe();

		countDownLatch.await();
	}

	/**
	 * Fetch data using query derivation.
	 */
	@Test
	void shouldQueryDataWithQueryDerivation() {

		List<Movie> movies = repository	.findByTitle("jaws")
										.collectList()
										.block();

		assertThat(movies).hasSize(1);
		assertThat(movies	.get(0)
							.getTitle()).isEqualTo("jaws");
	}

	// DONE-reactive-repository-mongodb-02: Do the following
	// - Use findByGenre(..) method with "action" as Genre
	// (Note that findByGenre(..) takes Mono as an argument)
	// - Verify that the number of movies returned is 2
	@Test
	void shouldQueryDataWithQueryDerivation2() {

		List<Movie> movies = repository	.findByGenre(Mono.just("action"))
										.collectList()
										.block();

		assertThat(movies).hasSize(2);
	}

	/**
	 * Fetch data using a string query.
	 */
	@Test
	void shouldQueryDataWithStringQuery() {

		Movie movie = repository.findByTitleAndGenre("avengers", "action")
								.block();

		assertThat(movie.getTitle()).isEqualTo("avengers");
	}

	// DONE-reactive-repository-mongodb-03: Do the following
	// - Add another instance of new Movie("avengers2", "action") to the
	// repository before performing the findByTitleAndGenre
	// - Write the code so that operations are chained together
	// - Hint: You can use either flatMap or then - try both
	@Test
	void shouldQueryDataWithStringQuery2() {
		Movie movie = repository.save(new Movie("avengers2", "action"))
								.flatMap(m -> repository.findByTitleAndGenre("avengers", "action"))
								.block();

		assertThat(movie.getTitle()).isEqualTo("avengers");
	}

	@Test
	void shouldQueryDataWithStringQuery3() {
		Movie movie = repository.save(new Movie("avengers2", "action"))
								.then(repository.findByTitleAndGenre("avengers", "action"))
								.block();

		assertThat(movie.getTitle()).isEqualTo("avengers");
	}

	/**
	 * Fetch data using query derivation.
	 */
	@Test
	void shouldQueryDataWithDeferredQueryDerivation() {

		List<Movie> actionMovies = repository	.findByGenre(Mono.just("action"))
												.collectList()
												.block();

		assertThat(actionMovies).hasSize(2);
	}

	/**
	 * Fetch data using query derivation and deferred parameter resolution.
	 */
	@Test
	void shouldQueryDataWithMixedDeferredQueryDerivation() {

		Movie movie = repository.findByTitleAndGenre(Mono.just("avengers"), "action")
								.block();

		assertThat(movie).isNotNull();
	}

	/**
	 * This sample performs a count, inserts data and performs a count again using
	 * reactive operator chaining.
	 */
	@Test
	void shouldInsertAndCountData() throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		repository	.count()
					.doOnNext(numberOfDocuments -> System.out.println(
							"before saveAll(): number of documents = " + numberOfDocuments))
					.thenMany(
							repository.saveAll(Flux.just(new Movie("movie5", "action"), new Movie("movie6", "comic"))))
					.last() // Wait until the last movie is published
					.flatMap(lastMovie -> repository.count())
					.doOnNext(numberOfDocuments -> System.out.println(
							"after saveAll(): number of documents = " + numberOfDocuments))
					.doOnSuccess(it -> countDownLatch.countDown())
					.doOnError(throwable -> countDownLatch.countDown())
					.subscribe();

		countDownLatch.await();
	}

	// DONE-reactive-repository-mongodb-04: Do the following
	// - Display the number of documents first
	// - Find a movie whose title is "avengers" and genre is "action" and delete it
	// - Redisplay the number of documents
	@Test
	void shouldInsertAndCountData2() throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		repository	.count()
					.doOnNext(numberOfDocuments -> System.out.println(
							"before delete(): number of documents = " + numberOfDocuments))
//					.flatMap(n -> repository.findByTitleAndGenre("avengers", "action"))
					.then(repository.findByTitleAndGenre("avengers", "action"))
					.flatMap(movie -> repository.delete(movie))
					.then(repository.count())
					.doOnNext(numberOfDocuments -> System.out.println(
							"after delete(): number of documents = " + numberOfDocuments))
					.doOnSuccess(it -> countDownLatch.countDown())
					.doOnError(throwable -> countDownLatch.countDown())
					.subscribe();

		countDownLatch.await();
	}

	// DONE-reactive-repository-mongodb-05: This is optional
	// -Do the same as reactive-repository-mongodb-04 above
	// but use findByTitle(..) not findByTileAndGenre(..)
	// (Note that findByTitle(..) returns Flux<Movie> not Mono<Movie>,
	// which let you determine whether you have to use flatMap vs
	// flatMapMany or then vs thenMany)
	@Test
	void shouldInsertAndCountData3() throws Exception {

		CountDownLatch countDownLatch = new CountDownLatch(1);

		repository	.count()
					.doOnNext(numberOfDocuments -> System.out.println(
							"before delete(): number of documents = " + numberOfDocuments))
					.thenMany(repository.findByTitle("avengers")) // Need to use thenMany
					.flatMap(movie -> repository.delete(movie))
					.then(repository.count())
					.doOnNext(numberOfDocuments -> System.out.println(
							"after delete(): number of documents = " + numberOfDocuments))
					.doOnSuccess(it -> countDownLatch.countDown())
					.doOnError(throwable -> countDownLatch.countDown())
					.subscribe();

		countDownLatch.await();
	}

	/**
	 * A tailable cursor streams data using {@link Flux} as it arrives inside the
	 * capped collection.
	 */
	@Test
	void shouldStreamDataWithTailableCursor() throws Exception {

		// We need to create capped collection for tailable cursor streams to work
		operations	.collectionExists(Movie.class)
					.flatMap(exists -> exists ? operations.dropCollection(Movie.class) : Mono.just(exists))
					.then(operations.createCollection(Movie.class, CollectionOptions.empty()
																					.size(1024 * 1024)
																					.maxDocuments(100)
																					.capped()))
					.block();

		repository	.saveAll(Flux.just(new Movie("jaws", "romance"), new Movie("avengers", "action"),
				new Movie("sound of music", "comic"), new Movie("movie4", "action")))
					.then()
					.block();

		Disposable disposable = repository	.findWithTailableCursorBy()
											.doOnNext(document -> System.out.println(
													"after findWithTailableCursorBy(): " + document.toString()))
											.doOnComplete(() -> System.out.println(
													"after findWithTailableCursorBy(): " + "Complete"))
											.doOnTerminate(() -> System.out.println(
													"after findWithTailableCursorBy(): " + "Terminated"))
											.subscribe();

		Thread.sleep(100);

		repository	.save(new Movie("movie7", "romance"))
					.subscribe();
		Thread.sleep(100);

		repository	.save(new Movie("movie8", "romance"))
					.subscribe();
		Thread.sleep(100);

		disposable.dispose();

		repository	.save(new Movie("movie9", "drama"))
					.subscribe();
		Thread.sleep(100);
	}

	// DONE-reactive-repository-mongodb-05: Do the following
	// - Save a few more movies using "saveAll(..)" as shown above
	// before the dispose() method gets called and rerun the test
}
