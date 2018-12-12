package io.pivotal.reactive;

import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
@SpringBootTest(webEnvironment=WebEnvironment.MOCK)
public class ReactiveMovieControllerTests {

	private WebTestClient webTestClient;

	@Autowired
	private ReactiveMovieController reactiveMovieController;

	private AtomicInteger atomicInteger = new AtomicInteger(10);

	// DONE-reactive-webflux-controller-test-01: Do the following
	// - Run the test without "reactive-webflux-controller" running
	
	// DONE-reactive-webflux-controller-test-02: Do the following
	// - Start "reactive-webflux-controller" running
	// - Use "bindToServer" instead of "bindToController" and run the
	//   test again
	// - Change it back to "bindToController"
	
	@BeforeEach
	public void setup() {
		webTestClient = WebTestClient	.bindToController(reactiveMovieController)
										.build();
	}

	@Test
	public void testGetAllMovies() {
		
		webTestClient	.get()
						.uri("/movies")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.exchange()
						.expectStatus()
						.isOk()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBodyList(Movie.class);
	}

	@Test
	public void testCreateMovie() {

		Movie movie = new Movie("This is a Test Movie", new Integer(atomicInteger.incrementAndGet()).toString());
		webTestClient	.post()
						.uri("/movies")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(movie), Movie.class)
						.exchange()
						.expectStatus()
						.isCreated()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.title")
						.isEqualTo("This is a Test Movie");
	}

	@Test
	public void testGetSingleMovie() {

		webTestClient	.get()
						.uri("/movies/4")
						.exchange()
						.expectStatus()
						.isOk()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.title")
						.isEqualTo("justice league");
	}

	@Test
	public void testGetNonExistingMovieReturns404() {
		webTestClient	.get()
						.uri("/movies/999")
						.exchange()
						.expectStatus()
						.isNotFound();
	}

	@Test
	public void testUpdateMovie() {

		Movie newMovie = new Movie("Updated Movie", "2");

		webTestClient	.put()
						.uri("/movies/{id}", Collections.singletonMap("id", newMovie.getId()))
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(newMovie), Movie.class)
						.exchange()
						.expectStatus()
						.isOk()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.title")
						.isEqualTo("Updated Movie");
	}

	@Test
	public void testDeleteMovie() {
		webTestClient	.delete()
						.uri("/movies/{id}", Collections.singletonMap("id", 1))
						.exchange()
						.expectStatus()
						.isOk();
	}

}

// DONE-reactive-webflux-controller-test-10: Do the following
// - Write CRUD test code for performing CRUD operations against Person
