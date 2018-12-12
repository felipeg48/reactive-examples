package io.pivotal.reactive;

import java.util.Collections;
import java.util.Random;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Mono;

@RunWith(JUnitPlatform.class)
@SpringJUnitConfig
public class ReactiveMovieFunctionalBindToServerTests {

	private static WebTestClient webTestClient;

	// TODO-reactive-webflux-functional-endpoint-test-00: 
	// -Restart the "reactive-webflux-functional-endpoint" app 
	// before running this test
	// -Remove @Disabled annotations from the tests below

	@BeforeAll
	static void setup() {

		webTestClient = WebTestClient.bindToServer()
		                             .baseUrl("http://localhost:8080")
		                             .build();
	}

	@Test
	@Disabled
	void testGetAllMovies() {
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
	@Disabled
	void testCreateMovie() {
		Movie movie = new Movie("This is a Test Movie", new Integer(new Random().nextInt(1000)).toString());
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
	@Disabled
	void testGetSingleMovie() {

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
	@Disabled
	void testGetNonExistingMovieReturns404() {
		webTestClient.get()
		             .uri("/movies/999")
		             .exchange()
		             .expectStatus()
		             .isNotFound();
	}

	@Test
	@Disabled
	void testUpdateMovie() {

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

	// If run 2nd time, it will return 404 since it runs against the live server
	@Test
	@Disabled
	void testDeleteMovie() {
		webTestClient.delete()
		             .uri("/movies/{id}", Collections.singletonMap("id", 1))
		             .exchange();
	}

}

// TODO-reactive-webflux-functional-endpoint-test-10: Do the following
// - Write CRUD test code for performing CRUD operations against Person
