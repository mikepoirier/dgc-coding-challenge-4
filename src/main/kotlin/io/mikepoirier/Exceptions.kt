package io.mikepoirier

import org.springframework.http.HttpStatus


data class WebException(val errorCode: HttpStatus, val body: Any = mapOf(Pair("message", "Something broke..."))) : Throwable("Return Error Code: $errorCode with body $body")
