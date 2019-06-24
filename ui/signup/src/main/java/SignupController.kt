package realworld.ui.signup

import androidx.core.view.isVisible
import com.bluelinelabs.conductor.Router
import knit.loop.conductor.KnitConductorController
import knit.loop.core.KnitModelSerializer
import knit.loop.core.innerConnection
import kotlinx.android.synthetic.main.controller_signup.*
import kotlinx.serialization.json.Json
import kt.mobius.Connectable
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.extras.CompositeEffectHandler
import kt.mobius.functions.Consumer
import org.kodein.di.direct
import org.kodein.di.erased.instance
import realworld.ui.navigation.FeedNavigator
import realworld.ui.navigation.SigninNavigator


class SignupController : KnitConductorController<SignupModel, Any, Any>() {

  override val layoutId = R.layout.controller_signup

  override fun defaultModel() = SignupModel.defaultModel()

  override fun modelSerializer() =
    object : KnitModelSerializer<SignupModel> {
      override fun deserialize(model: String): SignupModel {
        return Json.nonstrict.parse(SignupModel.serializer(), model)
      }

      override fun serialize(model: SignupModel): String {
        return Json.nonstrict.stringify(SignupModel.serializer(), model)
      }
    }
  override fun buildLoopFactory() = loopFactory {
    update = Update { model, event ->
      when (event) {
        is SignupEvent -> SignupUpdate(model, event)
        else -> noChange()
      }
    }

    effectHandler = CompositeEffectHandler.from(
      Connectable {
        innerConnection(SignupEffectHandler(it, kodein))
      },
      Connectable {
        innerConnection(direct.instance<Router, FeedNavigator>(arg = router))
      },
      Connectable {
        innerConnection(direct.instance<Router, SigninNavigator>(arg = router))
      }
    )
  }

  override fun bindView(model: SignupModel, output: Consumer<Any>) =
    bindViews(output) {
      inputEmail.bindTextChange(SignupEvent::OnEmailChanged)
      inputUsername.bindTextChange(SignupEvent::OnUsernameChanged)
      inputPassword.bindTextChange(SignupEvent::OnPasswordChanged)

      buttonSignup.bindClickEvent(SignupEvent.OnSignupClicked)
      buttonHaveAccount.bindClickEvent(SignupEvent.OnHaveAccountClicked)
    }

  override fun render(model: SignupModel) {
    inputEmail.isEnabled = model.canSubmit
    inputUsername.isEnabled = model.canSubmit
    inputPassword.isEnabled = model.canSubmit

    labelError.text = model.errorMessage

    groupSignupSubmit.isVisible = model.canSubmit
    progressLoading.isVisible = model.isLoading
  }
}
