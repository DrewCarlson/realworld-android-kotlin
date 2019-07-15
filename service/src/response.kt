package realworld.service

import kotlinx.serialization.Serializable
import realworld.model.User

@Serializable
data class SigninResponse(val user: User)

@Serializable
data class RegisterResponse(val user: User)
