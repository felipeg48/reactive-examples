package io.pivotal.reactive;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/people")
public class ReactivePersonController {

	private ReactivePersonRepository repository;

	public ReactivePersonController(ReactivePersonRepository repository) {
		super();
		this.repository = repository;
	}

	@GetMapping
	Flux<Person> getPeople() {
		return repository.findAll();
	}

	@GetMapping("/{id}")
	Mono<Person> getPerson(@PathVariable("id") String personId) {
		return repository	.findById(personId)
							.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	@GetMapping("/option2/{id}")
	Mono<ResponseEntity<Person>> getPerson2(@PathVariable("id") String personId) {
		return repository	.findById(personId)
							.map(person -> ResponseEntity.ok(person))
							.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	@PostMapping("/option2")
	Mono<Person> addPerson2(@RequestBody Person person) {
		return repository.insert(person);
	}

	@PostMapping
	Mono<ResponseEntity<Person>> addPerson(@RequestBody Person newPerson) {
		return repository	.insert(newPerson)
							.map(person -> ResponseEntity	.created(null)
															.body(newPerson));
	}

	@PostMapping("/option3")
	Mono<ResponseEntity<Person>> addPerson3(@RequestBody Person newPerson) {
		return repository	.insert(newPerson)
							.map(Person -> {
								HttpHeaders headers = new HttpHeaders();
								headers.add("my-custom-header", "my-custom-value");
								return new ResponseEntity<>(newPerson, headers, HttpStatus.CREATED);
							});
	}

	@PutMapping("/{id}")
	Mono<Person> updatePerson(@PathVariable("id") String personId, @RequestBody Person newPerson) {
		return repository	.findById(personId)
							.flatMap(existingPerson -> {
								existingPerson.setAge(newPerson.getAge());
								existingPerson.setFirstname(newPerson.getFirstname());
								existingPerson.setLastname(newPerson.getLastname());
								return repository.save(existingPerson);
							})
							.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
	}

	@DeleteMapping("/{id}")
	Mono<Void> deletePerson(@PathVariable("id") String personId) {
		return repository.deleteById(personId);
	}

}
