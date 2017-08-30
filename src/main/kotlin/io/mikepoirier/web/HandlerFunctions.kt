package io.mikepoirier.web

import io.mikepoirier.WebException
import io.mikepoirier.json
import io.mikepoirier.jsonStream
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

fun createJsonResponse(httpStatus: HttpStatus, success: Mono<out Any>, errorHandler: (Throwable) -> Mono<out ServerResponse> = defaultErrorHandler): Mono<ServerResponse> {
    return success
        .flatMap {
            ServerResponse
                .status(httpStatus)
                .json()
                .body(Mono.just(it))
        }
        .onErrorResume(errorHandler)
}

fun createJsonStreamResponse(httpStatus: HttpStatus, success: Mono<out Any>, errorHandler: (Throwable) -> Mono<out ServerResponse> = defaultErrorHandler): Mono<ServerResponse> {
    return success
        .flatMap {
            ServerResponse
                .status(httpStatus)
                .jsonStream()
                .body(Mono.just(it))
        }
        .onErrorResume(errorHandler)
}

data class ErrorResponse(val error: String?, val message: String?)

private val defaultErrorHandler: (Throwable) -> Mono<out ServerResponse> = {
    val log = LoggerFactory.getLogger("DefaultErrorHandler")

    log.error("Caught error", it)

    when(it) {
        is WebException -> ServerResponse
            .status(it.errorCode)
            .body(Mono.just(it.body))
        else -> ServerResponse
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(Mono.just(ErrorResponse(it::class.simpleName, it.message)))
    }
}
