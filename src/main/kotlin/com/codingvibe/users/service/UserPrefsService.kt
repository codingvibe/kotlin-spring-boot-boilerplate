package com.codingvibe.users.service

import com.codingvibe.users.dao.UserPrefsDao
import com.codingvibe.users.model.Details
import com.codingvibe.users.model.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class UserPrefsService (private val dao: UserPrefsDao) {
    fun getUserDetails(id: UUID): UserDetails? {
        return dao.getUserById(id)
    }

    fun findUsersByName(name: String): List<UserDetails> {
        return dao.getUsersByName(name)
    }

    fun createNewUser(name: String, details: Details): UserDetails {
        return dao.setUserInfo(name, details)
    }

    fun updateUserDetails(id: UUID, name: String, details: Details): UserDetails {
        return dao.setUserInfo(name, details, id)
    }

    fun deleteUserById(id: UUID): Boolean {
        return dao.deleteUserById(id)
    }
}