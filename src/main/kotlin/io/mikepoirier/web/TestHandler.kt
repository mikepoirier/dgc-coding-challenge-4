package io.mikepoirier.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.atomic.AtomicLong

@Component
class TestHandler {

    private val log = LoggerFactory.getLogger(TestHandler::class.java)
    private val counter = AtomicLong()

    fun handleGet(req: ServerRequest): Mono<ServerResponse> {

        Flux.range(0, 9)
            .delayElements(Duration.ofMillis(1000))

        val response = Mono.just(counter.getAndIncrement())
            .doOnNext {
                log.info("Received Get")
            }
            .map {
                mapOf(Pair("count", it))
            }
            .doOnNext {
                Thread.sleep(5000)
                log.info("After sleep")
            }

        return createJsonStreamResponse(HttpStatus.OK, response)
    }
}