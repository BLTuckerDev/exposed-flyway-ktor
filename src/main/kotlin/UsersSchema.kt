package dev.bltucker.exposedflyway

import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

@Serializable
data class ExposedUser(
    val name: String,
    val age: Int,
    val status: String
)

class UserService() {
    object Users : Table("Users") {
        val id = integer("id").autoIncrement()
        val name = varchar("name", length = 50)
        val age = integer("age")
        val status = varchar("status", 50).default("active")

        override val primaryKey = PrimaryKey(id)
    }

    suspend fun create(user: ExposedUser): Int = dbQuery {
        Users.insert {
            it[name] = user.name
            it[age] = user.age
            it[status] = user.status
        }[Users.id]
    }

    suspend fun read(id: Int): ExposedUser? {
        return dbQuery {
            Users.selectAll()
                .where { Users.id eq id }
                .map { ExposedUser(it[Users.name], it[Users.age], it[Users.status]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Int, user: ExposedUser) {
        dbQuery {
            Users.update({ Users.id eq id }) {
                it[name] = user.name
                it[age] = user.age
                it[status] = user.status
            }
        }
    }

    suspend fun delete(id: Int) {
        dbQuery {
            Users.deleteWhere { Users.id.eq(id) }
        }
    }

    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun getAll(): List<ExposedUser> {
        return dbQuery {
            Users.selectAll()
                .map { ExposedUser(it[Users.name], it[Users.age], it[Users.status]) }
                .toList()
        }
    }
}

