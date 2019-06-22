package realworld.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
  val email: String,
  val token: String? = null,
  val username: String,
  val bio: String? = null,
  val image: String = "https://static.productionready.io/images/smiley-cyrus.jpg"
)
