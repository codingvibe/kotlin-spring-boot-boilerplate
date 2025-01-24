package com.codingvibe.users.model

import java.time.Instant
import java.util.UUID

data class UserDetails (
    val id: UUID,
    val name: String,
    val details: Details,
    val createdAt: Instant,
    val updatedAt: Instant,
)

data class Details (
    val address: String,
    val favoriteCat: String?,
)