package realworld.ui.signup

import kotlinx.serialization.Serializable


@Serializable
data class SignupModel(
  val email: String = "",
  val username: String = "",
  val password: String = "",
  val canSubmit: Boolean = true,
  val isLoading: Boolean = false,
  val errorMessage: String = ""
) {
  companion object {
    fun defaultModel() = SignupModel()
  }

  override fun toString(): String {
    return "SignupModel(email='***', " +
        "username='***', " +
        "password='***', " +
        "canSubmit=$canSubmit, " +
        "isLoading=$isLoading, " +
        "errorMessage='$errorMessage')"
  }
}
