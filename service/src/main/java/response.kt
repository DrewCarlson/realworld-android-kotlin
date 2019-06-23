package realworld.service

import kotlinx.serialization.Serializable
import realworld.model.User

@Serializable
data class SigninResponse(val user: User)
