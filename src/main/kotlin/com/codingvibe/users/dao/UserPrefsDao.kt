package com.codingvibe.users.dao

import com.codingvibe.users.model.Details
import com.codingvibe.users.model.UserDetails
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.datetime
import org.jetbrains.exposed.sql.json.json
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.ZoneOffset
import java.util.*

@Component
class UserPrefsDao(private val database: Database) {
    companion object {
        val daoObjectMapper: ObjectMapper = ObjectMapper()
            .registerModule(KotlinModule.Builder().build())
            .registerModule(JavaTimeModule())
            .registerModule(ParameterNamesModule())
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
    }

    object Users : Table("users") {
        val id = uuid("id")
        val name = varchar("name", 256)
        val details = json<Details>("details", { daoObjectMapper.writeValueAsString(it) }, { daoObjectMapper.readValue<Details>(it) })
        val createdAt = datetime("created_at")
        val updatedAt = datetime("updated_at")
        override val primaryKey = PrimaryKey(id)
    }

    fun getUserById(id: UUID): UserDetails? {
        database.run {
            return transaction {
                Users.selectAll().where { Users.id eq id }.singleOrNull()?.toUserPrefs()
            }
        }
    }

    fun getUsersByName(name: String): List<UserDetails> {
        database.run {
            return transaction {
                Users.selectAll().where { Users.name eq name }.map { it.toUserPrefs() }
            }
        }
    }

    fun setUserInfo(name: String, details: Details, id: UUID? = null): UserDetails {
        database.run {
            id?.let {
                val userDetails = getUserById(it) ?: throw IOException ("Unable to find user with ID $it")
                transaction {
                    Users.update({ Users.id eq it }) { row ->
                        row[Users.name] = name
                        row[Users.details] = details
                    }
                }
                return userDetails.copy(name = name, details = details)
            }

            return transaction {
                Users.insertReturning {
                    it[Users.name] = name
                    it[Users.details] = details
                }.single().toUserPrefs()
            }
        }
    }

    fun deleteUserById(id: UUID): Boolean {
        database.run {
            return transaction {
                Users.deleteWhere { Users.id eq id } > 0
            }
        }
    }

    private fun ResultRow.toUserPrefs(): UserDetails {
        return UserDetails(
            id = this[Users.id],
            name = this[Users.name],
            details = this[Users.details],
            createdAt = this[Users.createdAt].toInstant(ZoneOffset.UTC),
            updatedAt = this[Users.updatedAt].toInstant(ZoneOffset.UTC)
        )
    }
}