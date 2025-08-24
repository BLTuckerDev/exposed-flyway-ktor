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