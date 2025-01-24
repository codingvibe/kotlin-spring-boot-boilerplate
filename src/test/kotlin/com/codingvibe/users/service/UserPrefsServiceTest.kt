package com.codingvibe.users.service

import com.codingvibe.users.dao.UserPrefsDao
import com.codingvibe.users.model.Details
import com.codingvibe.users.model.UserDetails
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.assertThrows
import java.io.IOException
import java.time.Instant
import java.util.UUID
import kotlin.test.Test
import kotlin.test.assertEquals

class UserPrefsServiceTest {
    private val dao = mockk<UserPrefsDao>()
    private val userPrefsService = UserPrefsService(dao)

    @Test
    fun testGettingUserById() {
        val returnedUser = generateUser()
        every {
            dao.getUserById(returnedUser.id)
        } returns returnedUser

        val foundUser = userPrefsService.getUserDetails(returnedUser.id)
        assertEquals(returnedUser, foundUser)
    }

    @Test
    fun testGettingUserByName() {
        val returnedUser = generateUser()
        every {
            dao.getUsersByName(returnedUser.name)
        } returns listOf(returnedUser)

        val foundUsers = userPrefsService.findUsersByName(returnedUser.name)
        assertEquals(foundUsers.count(), 1)
        assertEquals(foundUsers.first(), returnedUser)
    }

    @Test
    fun testCreateNewUser() {
        val newUser = generateUser()

        every {
            dao.setUserInfo(name = newUser.name, details = newUser.details)
        } returns newUser

        val updatedUser = userPrefsService.createNewUser(
            name = newUser.name,
            details = newUser.details
        )

        assertEquals(updatedUser, newUser)
    }

    @Test
    fun testSetUserDetails() {
        val startedUser = generateUser()
        val newName = "Shawnald Hamboni"
        val finalUser = startedUser.copy(name = newName)

        every {
            dao.setUserInfo(id = startedUser.id, name = newName, details = startedUser.details)
        } returns finalUser

        val updatedUser = userPrefsService.updateUserDetails(
            id = startedUser.id,
            name = newName,
            details = startedUser.details
        )

        assertEquals(updatedUser, finalUser)
    }

    @Test
    fun testSetUserDetailsThrows() {
        val startedUser = generateUser()
        val newName = "Shawnald Hamboni"

        every {
            dao.setUserInfo(id = startedUser.id, name = newName, details = startedUser.details)
        } throws IOException("Whoops")

        assertThrows<IOException> { userPrefsService.updateUserDetails(
            id = startedUser.id,
            name = newName,
            details = startedUser.details
        ) }
    }


    private fun generateUser(
        id: UUID = UUID.randomUUID(),
        name: String = "Jonald Hamboni",
        details: Details = Details("15483 San Marcos Ave", "Bootsy")
    ): UserDetails {
        return UserDetails(
            id = id,
            name = name,
            details = details,
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}