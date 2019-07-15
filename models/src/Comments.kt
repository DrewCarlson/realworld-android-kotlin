package realworld.model

import kotlinx.serialization.Serializable

@Serializable
data class Comments(
  val comments: List<Comment>
)
