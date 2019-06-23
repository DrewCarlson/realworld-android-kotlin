package realworld.ui.signin

import android.os.Build
import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.bluelinelabs.conductor.Router
import kotlinx.android.synthetic.main.controller_signin.*
import kt.mobius.Connectable
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.extras.CompositeEffectHandler
import kt.mobius.functions.Consumer
import org.kodein.di.direct
import org.kodein.di.erased.instance
import realworld.base.BaseController
import realworld.ui.navigation.FeedNavigator

/** */
class SigninController : BaseController<SigninModel, Any, Any>(null) {

  override val layoutId = R.layout.controller_signin

  override val defaultModel = SigninModel.default()
  override val update = Update<SigninModel, Any, Any> { model, event ->
    when (event) {
      is SigninEvent -> SigninUpdate(model, event)
      else -> noChange()
    }
  }
  override val effectHandler = CompositeEffectHandler.from<Any, Any>(
    Connectable {
      innerConnection(SigninEffectHandler(it, kodein))
    },
    Connectable {
      innerConnection(direct.instance<Router, FeedNavigator>(arg = router))
    }
  )

  override fun bindView(model: SigninModel, output: Consumer<Any>) =
    bindViews(output) {
      buttonSignin.bindClickEvent(SigninEvent.OnSigninClicked)
      inputEmail.bindTextChange(SigninEvent::OnEmailChanged)
      inputPassword.bindTextChange(SigninEvent::OnPasswordChanged)

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        inputEmail.setAutofillHints(View.AUTOFILL_HINT_EMAIL_ADDRESS)
        inputPassword.setAutofillHints(View.AUTOFILL_HINT_PASSWORD)
      }
    }

  override fun render(model: SigninModel) {
    buttonSignin.isEnabled = model.canSubmit
    inputEmail.isEnabled = model.canSubmit
    inputPassword.isEnabled = model.canSubmit

    labelError.text = model.errorMessage

    progressLoading.isVisible = model.isLoading
    groupSigninSubmit.isVisible = model.canSubmit
  }
}
