package dev.bltucker.exposedflyway

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureContentNegotiation()
    configureDatabases()
}

private fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
}
