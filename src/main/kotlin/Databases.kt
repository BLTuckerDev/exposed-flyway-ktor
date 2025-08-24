package dev.bltucker.exposedflyway

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
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
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabases() {

    val hikariDataSource = createDataSource()
    runFlyway(hikariDataSource)

    val database = Database.connect(hikariDataSource)

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


private fun createDataSource(): HikariDataSource {
    val config = HikariConfig()
    config.driverClassName = "org.h2.Driver"
    config.jdbcUrl = "jdbc:h2:file:./data/devdb;DATABASE_TO_UPPER=FALSE"
    config.username = "admin"
    config.password = "test"
    config.validate()
    return HikariDataSource(config)
}

private fun runFlyway(dataSource: HikariDataSource) {
    val flyway = Flyway.configure()
        .dataSource(dataSource)
        .load()
    try {
        flyway.migrate()
    } catch (e: Exception) {
        // Handle migration failures
        println("Flyway migration failed: ${e.message}")
        throw e
    }
}