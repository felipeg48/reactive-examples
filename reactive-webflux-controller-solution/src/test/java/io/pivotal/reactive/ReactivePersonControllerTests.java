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
public class ReactivePersonControllerTests {

	private WebTestClient webTestClient;

	@Autowired
	private ReactivePersonController reactivePersonController;

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
		webTestClient = WebTestClient	.bindToController(reactivePersonController)
										.build();
	}

	@Test
	public void testGetAllPersons() {
		
		webTestClient	.get()
						.uri("/people")
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.exchange()
						.expectStatus()
						.isOk()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBodyList(Person.class);
	}

	@Test
	public void testCreatePerson() {

		Person person = new Person(new Integer(atomicInteger.incrementAndGet()).toString(), "myfirstname", "mylastname", 55);
		webTestClient	.post()
						.uri("/people")
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(person), Person.class)
						.exchange()
						.expectStatus()
						.isCreated()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.firstname")
						.isEqualTo("myfirstname");
	}

	@Test
	public void testGetSinglePerson() {

		webTestClient	.get()
						.uri("/people/4")
						.exchange()
						.expectStatus()
						.isOk()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.firstname")
						.isEqualTo("sang");
	}

	@Test
	public void testGetNonExistingPersonReturns404() {
		webTestClient	.get()
						.uri("/people/999")
						.exchange()
						.expectStatus()
						.isNotFound();
	}

	@Test
	public void testUpdatePerson() {

		Person newPerson = new Person("2", "kim", "jones", 66);

		webTestClient	.put()
						.uri("/people/{id}", Collections.singletonMap("id", newPerson.getId()))
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.accept(MediaType.APPLICATION_JSON_UTF8)
						.body(Mono.just(newPerson), Person.class)
						.exchange()
						.expectStatus()
						.isOk()
						.expectHeader()
						.contentType(MediaType.APPLICATION_JSON_UTF8)
						.expectBody()
						.jsonPath("$.lastname")
						.isEqualTo("jones");
	}

	@Test
	public void testDeletePerson() {
		webTestClient	.delete()
						.uri("/people/{id}", Collections.singletonMap("id", 1))
						.exchange()
						.expectStatus()
						.isOk();
	}

}

// DONE-reactive-webflux-controller-test-10: Do the following
// - Write CRUD test code for performing CRUD operations against Person
