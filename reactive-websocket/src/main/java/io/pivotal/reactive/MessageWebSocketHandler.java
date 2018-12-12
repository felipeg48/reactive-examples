package io.pivotal.reactive;

import java.time.Duration;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class MessageWebSocketHandler implements WebSocketHandler {

    private Flux<Long> messageFlux;

    /**
     * Here we prepare a Flux that will emit a message every second
     */
    @PostConstruct
    private void init() {
        messageFlux = Flux.interval(Duration.ofSeconds(1)).take(10);
    }

    /**
     * On each new client session, send the message flux to the client.
     * Spring subscribes to the flux and send every new flux event to the WebSocketSession object
     * @param session
     * @return Mono<Void>
     */
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        return session.send(
                messageFlux
                        .map(longNUmber -> String.format("{ \"value\": %d }", longNUmber)) //transform to json
                        .map(session::textMessage)); // map to Spring WebSocketMessage of type text
    }

}

// TODO-reactive-websocket-10: Do the following
// - Create MessageWebSocketHandler2 class, in which message gets
//   created from a Java 8 stream, for example, 
//   Flux.fromStream(Stream.of(100, 200, 300, 400, 500))
// - Write appropriate handle(..) method
// - Configure MessageWebSocketHandler2