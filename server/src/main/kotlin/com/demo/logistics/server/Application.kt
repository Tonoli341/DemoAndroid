package com.demo.logistics.server

import com.demo.logistics.server.repo.DataStore
import com.demo.logistics.server.routes.registerApi
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.callloging.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.routing.*
import io.ktor.server.response.*
import org.slf4j.event.Level

fun main() {
    embeddedServer(Netty, port = 8080) {
        configureServer()
    }.start(wait = true)
}

fun Application.configureServer() {
    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
    }
    install(CallLogging) {
        level = Level.INFO
    }
    install(ContentNegotiation) {
        json()
    }

    val dataStore = DataStore()

    routing {
        get("/health") {
            call.respondText("ok")
        }
        registerApi(dataStore)
    }
}
