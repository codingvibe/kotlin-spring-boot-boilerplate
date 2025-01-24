package com.codingvibe.users.controller

import com.codingvibe.users.model.Details
import com.codingvibe.users.model.UserDetails
import com.codingvibe.users.service.UserPrefsService
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.util.*


@RestController
@RequestMapping("/api/v1")
class UserPrefsController(
    private val userPrefsService: UserPrefsService,
) {
    private val logger = KotlinLogging.logger{}

    @GetMapping("/users")
    fun getUserById(@RequestParam name: String?): List<UserDetails> {
        if (name == null) {
            logger.error { "Name not passed in to query." }
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Required to pass in name as query param")
        }
        return userPrefsService.findUsersByName(name)
    }

    @GetMapping("/users/{id}")
    fun getUserById(@PathVariable id: UUID): UserDetails {
        return userPrefsService.getUserDetails(id) ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user with ID $id.")
    }

    @PostMapping("/users")
    fun createNewUser(
        @RequestBody preferences: UserRequest
    ): UserDetails {
        return userPrefsService.createNewUser(preferences.name, preferences.details)
    }

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: UUID,
        @RequestBody preferences: UserRequest
    ): UserDetails {
        return userPrefsService.updateUserDetails(id, preferences.name, preferences.details)
    }

    @DeleteMapping("/users/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    fun deleteUpdate(
        @PathVariable id: UUID
    ) {
        if (!userPrefsService.deleteUserById(id)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Could not find user with ID $id.")
        }
    }
}

data class UserRequest (
    val name: String,
    val details: Details
)
