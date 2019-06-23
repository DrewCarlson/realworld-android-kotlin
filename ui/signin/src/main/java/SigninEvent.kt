package realworld.ui.signin

import realworld.model.User


sealed class SigninEvent {
  /** Dispatched when the usere presses the signin button. */
  object OnSigninClicked : SigninEvent()

  /** Dispatch when the user confirms they need an account. */
  object OnNeedAccountClicked : SigninEvent()

  /** */
  data class OnEmailChanged(val email: String) : SigninEvent() {
    override fun toString() = "OnEmailChanged(email='***')"
  }

  /** */
  data class OnPasswordChanged(val password: String) : SigninEvent() {
    override fun toString() = "OnPasswordChanged(password='***')"
  }

  /** */
  data class OnSigninComplete(val user: User) : SigninEvent()

  /** */
  data class OnSigninError(val message: String) : SigninEvent()
}
