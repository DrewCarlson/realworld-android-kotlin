package realworld.ui.signin

import android.os.Build
import android.view.View
import androidx.core.view.isVisible
import com.bluelinelabs.conductor.Router
import knit.loop.conductor.KnitConductorController
import knit.loop.core.KnitModelSerializer
import knit.loop.core.innerConnection
import kotlinx.android.synthetic.main.controller_signin.*
import kotlinx.serialization.json.Json
import kt.mobius.Connectable
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.extras.CompositeEffectHandler
import kt.mobius.functions.Consumer
import org.kodein.di.direct
import org.kodein.di.erased.instance
import realworld.ui.navigation.FeedNavigator
import realworld.ui.navigation.SignupNavigator

/** */
class SigninController : KnitConductorController<SigninModel, Any, Any>(null) {

  override val layoutId = R.layout.controller_signin

  override fun defaultModel() = SigninModel.default()
  override fun modelSerializer() =
    object : KnitModelSerializer<SigninModel> {
      override fun deserialize(model: String): SigninModel {
        return Json.nonstrict.parse(SigninModel.serializer(), model)
      }

      override fun serialize(model: SigninModel): String {
        return Json.nonstrict.stringify(SigninModel.serializer(), model)
      }
    }

  override fun buildLoopFactory() = loopFactory {
    update = Update { model, event ->
      when (event) {
        is SigninEvent -> SigninUpdate(model, event)
        else -> noChange()
      }
    }
    effectHandler = CompositeEffectHandler.from<Any, Any>(
      Connectable {
        innerConnection(SigninEffectHandler(it, kodein))
      },
      Connectable {
        innerConnection(direct.instance<Router, FeedNavigator>(arg = router))
      },
      Connectable {
        innerConnection(direct.instance<Router, SignupNavigator>(arg = router))
      }
    )
  }

  override fun bindView(model: SigninModel, output: Consumer<Any>) =
    bindViews(output) {
      buttonSignin.bindClickEvent(SigninEvent.OnSigninClicked)
      buttonNeedAccount.bindClickEvent(SigninEvent.OnNeedAccountClicked)

      inputEmail.bindTextChange(SigninEvent::OnEmailChanged)
      inputPassword.bindTextChange(SigninEvent::OnPasswordChanged)
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
