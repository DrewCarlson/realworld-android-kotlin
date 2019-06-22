package realworld.model

import kotlinx.serialization.Serializable

@Serializable
data class Profile(
  /** The user's username. */
  val username: String,
  /** An optional short text bio. */
  val bio: String? = null,
  /**
   * The profile image url.
   * @see imageOrDefault for a safe url call.
   */
  val image: String,
  /**
   * Whether or not the user who loaded this profile
   * is following it.
   */
  val following: Boolean
) {
  /** Returns the profile image url if available or a default url. */
  fun imageOrDefault(): String {
    return if (image.isNotBlank()) image
    else "https://static.productionready.io/images/smiley-cyrus.jpg"
  }
}
