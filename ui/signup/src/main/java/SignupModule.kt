package realworld.ui.signup

import com.bluelinelabs.conductor.Router
import knit.navigation.conductor.ConductorNavigator
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import realworld.ui.navigation.SignupNavigator
import kotlin.reflect.KClass

val SignupModule = Kodein.Module("Signup") {
  bind<SignupNavigator>() with factory { router: Router ->
    object : ConductorNavigator<SignupNavigator.Effect>(router), SignupNavigator {
      override fun controllerKClassFor(effect: SignupNavigator.Effect): KClass<*> =
        SignupController::class

      override fun createController(effect: SignupNavigator.Effect) = SignupController()
    }
  }
}
