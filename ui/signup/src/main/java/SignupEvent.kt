package realworld.ui.signup

import realworld.model.User


sealed class SignupEvent {
  data class OnUsernameChanged(val username: String) : SignupEvent() {
    override fun toString() = "OnUsernameChanged(username='***')"
  }

  data class OnPasswordChanged(val password: String) : SignupEvent() {
    override fun toString() = "OnPasswordChanged(password='***')"
  }

  data class OnEmailChanged(val email: String) : SignupEvent() {
    override fun toString() = "OnEmailChanged(email='***')"
  }

  data class OnSignupComplete(val user: User) : SignupEvent()

  data class OnSignupError(val errorMessage: String) : SignupEvent()

  object OnSignupClicked : SignupEvent()

  object OnHaveAccountClicked : SignupEvent()
}
