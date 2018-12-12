package io.pivotal.reactive;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ReactivePersonRepository extends ReactiveMongoRepository<Person, String>{

}
