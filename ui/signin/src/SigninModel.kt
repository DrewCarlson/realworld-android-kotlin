package realworld.ui.signin

import kotlinx.serialization.Serializable

@Serializable
data class SigninModel(
  val email: String = "",
  val password: String = "",
  val canSubmit: Boolean = true,
  val isLoading: Boolean = false,
  val errorMessage: String = ""
) {
  companion object {
    fun default() = SigninModel()
  }

  override fun toString(): String {
    return "SigninModel(email='***', " +
        "password='***', " +
        "canSubmit=$canSubmit, " +
        "isLoading=$isLoading, " +
        "errorMessage='$errorMessage')"
  }
}
