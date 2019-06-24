package realworld.ui.signup

import knit.navigation.core.NavigationData
import kt.mobius.Next
import kt.mobius.Next.Companion.dispatch
import kt.mobius.Next.Companion.next
import kt.mobius.Update
import realworld.ui.navigation.FeedNavigator
import realworld.ui.navigation.SigninNavigator

val SignupUpdate = Update<SignupModel, SignupEvent, Any> { model, event ->
  when (event) {
    is SignupEvent.OnUsernameChanged ->
      next(model.copy(username = event.username))
    is SignupEvent.OnPasswordChanged ->
      next(model.copy(password = event.password))
    is SignupEvent.OnEmailChanged ->
      next(model.copy(email = event.email))
    SignupEvent.OnHaveAccountClicked ->
      if (model.canSubmit) {
        dispatch<SignupModel, Any>(
          setOf(
            SigninNavigator.Effect(NavigationData.default(popIfPrevious = true))
          )
        )
      } else Next.noChange()
    SignupEvent.OnSignupClicked ->
      if (model.canSubmit) {
        dispatch<SignupModel, Any>(
          setOf(
            SignupEffect.Signup(
              model.username,
              model.password,
              model.email
            )
          )
        )
      } else Next.noChange()
    is SignupEvent.OnSignupComplete ->
      next(
        model.copy(isLoading = false),
        setOf<Any>(FeedNavigator.Effect())
      )
    is SignupEvent.OnSignupError ->
      next(
        model.copy(
          errorMessage = event.errorMessage,
          canSubmit = true,
          isLoading = false
        )
      )
  }
}
