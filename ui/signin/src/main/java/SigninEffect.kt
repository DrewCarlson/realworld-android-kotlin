package realworld.ui.signin


sealed class SigninEffect {

  /**
   *
   */
  data class SignIn(
    val email: String,
    val password: String
  ) : SigninEffect() {
    override fun toString(): String {
      return "SigninEffect(email='***', password='***')"
    }
  }
}
