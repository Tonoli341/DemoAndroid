package com.demo.logistics.server.routes

import com.demo.logistics.server.model.*
import com.demo.logistics.server.repo.DataStore
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Routing.registerApi(dataStore: DataStore) {
    route("/api") {
        post("/auth/login") {
            val body = call.receive<LoginRequest>()
            val user = dataStore.authenticate(body.username, body.pin)
            if (user != null) {
                call.respond(LoginResponse(user))
            } else {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Credenziali non valide"))
            }
        }

        get("/options") {
            call.respond(dataStore.options())
        }

        get("/workdays/week") {
            val userId = call.request.queryParameters["userId"]
            if (userId.isNullOrBlank()) return@get call.respond(HttpStatusCode.BadRequest, mapOf("error" to "userId mancante"))
            call.respond(WeekHistoryResponse(dataStore.recentWeekFor(userId)))
        }

        post("/workdays") {
            val body = call.receive<ShiftSubmission>()
            val created = dataStore.saveShift(body)
            call.respond(created)
        }

        put("/workdays/{id}/confirm") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val actor = call.request.queryParameters["actorId"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val updated = dataStore.confirmShift(id, actor) ?: return@put call.respond(HttpStatusCode.NotFound)
            call.respond(updated)
        }

        put("/workdays/{id}/approve") {
            val id = call.parameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val actor = call.request.queryParameters["actorId"] ?: return@put call.respond(HttpStatusCode.BadRequest)
            val updated = dataStore.approveShift(id, actor) ?: return@put call.respond(HttpStatusCode.NotFound)
            call.respond(updated)
        }

        get("/dashboard") {
            call.respond(dataStore.dashboard())
        }

        get("/logs") {
            call.respond(dataStore.allLogs())
        }

        post("/config") {
            val body = call.receive<AppConfig>()
            val actor = call.request.queryParameters["actorId"] ?: "system"
            call.respond(dataStore.updateConfig(body, actor))
        }
    }
}
