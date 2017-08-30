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
                    if(it == ' ') {
                        it
                    } else {
                        val index = alpha.indexOf(it)
                        var newIndex = index + alpha.indexOf(key)

                        newIndex %= alpha.toList().size

                        alpha.toList()[newIndex]
                    }
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
                    if(it == ' ') {
                        it
                    } else {
                        val index = alpha.indexOf(it)
                        var newIndex = index + alpha.indexOf(key)

                        newIndex %= alpha.toList().size

                        println("$it -> ${alpha.indexOf(it)}")

                        println("fn($it,$key) = ${alpha.toList()[25 - newIndex]}")

                        var foo = 26 - newIndex
                        if(foo == 26) foo = 0

                        alpha.reversed()
                            .toList()[foo]
                    }
                }.joinToString("")

                CipherResponse(text)
            }

        return createJsonResponse(HttpStatus.OK, body)
    }
}