package dev.bltucker.exposedflyway

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlin.text.toInt

fun Application.configureRoutes() {

    val userService = UserService()
    routing {
        // Create user
        post("/users") {
            val user = call.receive<ExposedUser>()
            val id = userService.create(user)
            call.respond(HttpStatusCode.Created, id)
        }

        get("/users"){
            val users = userService.getAll()
            call.respond(HttpStatusCode.OK, users)
        }


        route("/users/{id}") {
            // GET /users/{id} - Read a specific user
            get {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = userService.read(id)
                if (user != null) {
                    call.respond(HttpStatusCode.OK, user)
                } else {
                    call.respond(HttpStatusCode.NotFound)
                }
            }

            // PUT /users/{id} - Update a specific user
            put {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                val user = call.receive<ExposedUser>()
                userService.update(id, user)
                call.respond(HttpStatusCode.OK)
            }

            // DELETE /users/{id} - Delete a specific user
            delete {
                val id = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid ID")
                userService.delete(id)
                call.respond(HttpStatusCode.OK)
            }

            // POST /users/{id}/notes - Create a new note for this user
            post("/notes") {
                val userId = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid User ID")

                val noteInput = call.receive<CreateNoteRequest>()
                val noteId = userService.createNote(userId, noteInput.title, noteInput.content)
                call.respond(HttpStatusCode.Created, "Note created with id $noteId")
            }

            // GET /users/{id}/notes - Get all notes for this user
            get("/notes") {
                val userId = call.parameters["id"]?.toInt() ?: throw IllegalArgumentException("Invalid User ID")
                val notes: List<ExposedNote> = userService.getNotesForUser(userId)
                call.respond(HttpStatusCode.OK, notes)
            }
        }
    }
}