package realworld.ui.signin

import knit.navigation.core.NavigationData
import kt.mobius.Next.Companion.dispatch
import kt.mobius.Next.Companion.next
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import realworld.ui.navigation.FeedNavigator
import realworld.ui.navigation.SignupNavigator
import realworld.ui.signin.SigninEvent.*

val SigninUpdate = Update<SigninModel, SigninEvent, Any> { model, event ->
  when (event) {
    is OnEmailChanged -> onEmailChanged(model, event)
    is OnPasswordChanged -> onPasswordChanged(model, event)
    OnSigninClicked -> onSigninClicked(model)
    OnNeedAccountClicked -> onNeedAccountClicked(model)
    is OnSigninComplete -> onSigninComplete(model, event)
    is OnSigninError -> onSigninError(model, event)
  }
}

private fun onEmailChanged(model: SigninModel, event: OnEmailChanged) =
  next<SigninModel, Any>(model.copy(email = event.email))

private fun onPasswordChanged(model: SigninModel, event: OnPasswordChanged) =
  next<SigninModel, Any>(model.copy(password = event.password))

private fun onSigninClicked(model: SigninModel) =
  if (model.canSubmit) next(
    model.copy(
      canSubmit = false,
      isLoading = true,
      errorMessage = ""
    ), setOf<Any>(SigninEffect.SignIn(model.email, model.password))
  ) else noChange()

private fun onSigninComplete(model: SigninModel, event: OnSigninComplete) =
  next(
    model.copy(
      isLoading = false,
      canSubmit = false
    ),
    setOf<Any>(FeedNavigator.Effect())
  )

private fun onSigninError(model: SigninModel, event: OnSigninError) =
  next(
    model.copy(
      isLoading = false,
      canSubmit = true,
      errorMessage = event.message
    ),
    setOf<Any>()
  )

private fun onNeedAccountClicked(model: SigninModel) =
  dispatch<SigninModel, Any>(
    setOf(
      SignupNavigator.Effect(NavigationData.default(popIfPrevious = true))
    )
  )
