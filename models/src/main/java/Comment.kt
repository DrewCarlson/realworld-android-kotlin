package realworld.model

import kotlinx.serialization.Serializable

@Serializable
data class Comment(
  val id: Int,
  val createdAt: String,
  val updatedAt: String,
  val body: String,
  val author: Profile
)

@Serializable
data class Comments(
  val comments: List<Comment>
)
