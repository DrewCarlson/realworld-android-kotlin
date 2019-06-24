package realworld.ui.signup


sealed class SignupEffect {
  data class Signup(
    val username: String,
    val password: String,
    val email: String
  ) : SignupEffect() {
    override fun toString(): String {
      return "Signup(username='***', password='***', email='***')"
    }
  }
}
