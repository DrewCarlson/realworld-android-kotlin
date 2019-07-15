package realworld.ui.signup

import kt.mobius.Next
import kt.mobius.Next.Companion.dispatch
import kt.mobius.Next.Companion.next
import kt.mobius.Update

val SignupUpdate = Update<SignupModel, SignupEvent, SignupEffect> { model, event ->
    when (event) {
        is SignupEvent.OnUsernameChanged ->
            next(model.copy(username = event.username))
        is SignupEvent.OnPasswordChanged ->
            next(model.copy(password = event.password))
        is SignupEvent.OnEmailChanged ->
            next(model.copy(email = event.email))
        SignupEvent.OnHaveAccountClicked ->
            if (model.canSubmit) {
                dispatch(setOf(SignupEffect.HasAccount))
            } else Next.noChange()
        SignupEvent.OnSignupClicked ->
            if (model.canSubmit) {
                dispatch(
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
                setOf(SignupEffect.SignupComplete)
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
