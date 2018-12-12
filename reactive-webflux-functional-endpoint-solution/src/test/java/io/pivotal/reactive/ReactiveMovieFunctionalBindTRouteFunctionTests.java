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
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class ReactiveMovieFunctionalBindTRouteFunctionTests {

	private WebTestClient webTestClient;

	@Autowired
	private RouterFunction<ServerResponse> routerFunction;

	private AtomicInteger atomicInteger = new AtomicInteger(10);

	// TODO-reactive-webflux-functional-endpoint-test-01: You can
	// stop the "reactive-webflux-functional-endpoint" app before running
	// this test since you don't need a running server.

	@BeforeEach
	public void setup() {
		webTestClient = WebTestClient.bindToRouterFunction(routerFunction)
		                             .configureClient()
		                             .baseUrl("http://localhost:8080")
		                             .build();
	}

	@Test
	public void testGetAllMovies() {

		webTestClient.get()
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
		webTestClient.post()
		             .uri("/movies")
		             .contentType(MediaType.APPLICATION_JSON_UTF8)
		             .accept(MediaType.APPLICATION_JSON_UTF8)
		             .body(Mono.just(movie), Movie.class)
		             .exchange()
		             .expectStatus()
		             .isOk()
		             .expectHeader()
		             .contentType(MediaType.APPLICATION_JSON_UTF8)
		             .expectBody()
		             .jsonPath("$.title")
		             .isEqualTo("This is a Test Movie");
	}

	@Test
	public void testGetSingleMovie() {

		webTestClient.get()
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
		webTestClient.get()
		             .uri("/movies/999")
		             .exchange()
		             .expectStatus()
		             .isNotFound();
	}

	@Test
	public void testUpdateMovie() {

		Movie newMovie = new Movie("Updated Movie", "2");

		webTestClient.put()
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
		webTestClient.delete()
		             .uri("/movies/{id}", Collections.singletonMap("id", 1))
		             .exchange()
		             .expectStatus()
		             .isOk();
	}

}
