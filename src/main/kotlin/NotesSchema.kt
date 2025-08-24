package dev.bltucker.exposedflyway

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table
import java.time.Instant
import org.jetbrains.exposed.sql.javatime.timestamp

data class CreateNoteRequest(
    val title: String,
    val content: String,
)

@Serializable
data class ExposedNote(
    val id: Int,
    val userId: Int,
    val title: String,
    val content: String,
    val createdAt: String
)

object Notes : Table("Notes") {
    val id = integer("id").autoIncrement()
    // The foreign key is nullable to match the V3 migration
    val userId = integer("user_id").references(UserService.Users.id)
    val title = varchar("title", 255)
    val content = text("content")
    val createdAt = timestamp("created_at").default(Instant.now())

    override val primaryKey = PrimaryKey(id)
}