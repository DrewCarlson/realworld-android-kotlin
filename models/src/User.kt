package realworld.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
  val id: Int,
  val email: String,
  val token: String? = null,
  val username: String,
  val bio: String? = null,
  val image: String? = "https://static.productionready.io/images/smiley-cyrus.jpg"
) {
  companion object {
    private const val MAX_STR_LENGTH = 10
  }

  override fun toString(): String {
    return "User(id=$id, " +
        "email='***', " +
        "token='***', " +
        "username='***', " +
        "bio=${bio?.take(MAX_STR_LENGTH)}, " +
        "image=$image)"
  }
}
