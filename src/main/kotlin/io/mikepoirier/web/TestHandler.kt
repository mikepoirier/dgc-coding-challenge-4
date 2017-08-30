package io.mikepoirier.web

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.core.publisher.Mono
import java.util.concurrent.atomic.AtomicLong

typealias ResponseMono = Mono<ServerResponse>

data class CipherRequest(val key: Char, val text: String)

data class CipherResponse(val text: String)

val alpha = 'A'..'Z'

@Component
class TestHandler {

    private val log = LoggerFactory.getLogger(TestHandler::class.java)
    private val counter = AtomicLong()

    fun handleEncode(req: ServerRequest): ResponseMono {

        val body = req.bodyToMono<CipherRequest>()
            .map {
                var (key, text) = it

                text = text.toUpperCase().map {
                    val index = alpha.indexOf(it)
                    val newIndex = index + alpha.indexOf(key)

                    alpha.toList()[newIndex]
                }.joinToString("")

                CipherResponse(text)
            }

        return createJsonResponse(HttpStatus.OK, body)
    }

    fun handleDecode(req: ServerRequest): ResponseMono {

        val body = req.bodyToMono<CipherRequest>()
            .map {
                var (key, text) = it

                text = text.toUpperCase().map {
                    val index = alpha.indexOf(it)
                    val newIndex = index - alpha.indexOf(key)

                    alpha.toList()[newIndex]
                }.joinToString("")

                CipherResponse(text)
            }

        return createJsonResponse(HttpStatus.OK, body)
    }

    fun handleGet(req: ServerRequest): Mono<ServerResponse> {
        val response = Mono.just(counter.getAndIncrement())
            .doOnNext {
                log.info("Received Get")
            }
            .map {
                mapOf(Pair("count", it))
            }
            .doOnNext {
                Mono.just("After sleep")
                    .doOnNext {
                        val time = System.currentTimeMillis()
                        var elapsed = 0L

                        while (elapsed < 5000) {
                            elapsed = System.currentTimeMillis() - time
                        }
                    }
                    .subscribe { log.info(it) }
            }

        return createJsonStreamResponse(HttpStatus.OK, response, { ServerResponse.noContent().build() })
    }
}