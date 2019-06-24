package realworld.ui.signin

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import knit.navigation.conductor.ConductorNavigator
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import realworld.di.ServiceModule
import realworld.ui.navigation.SigninNavigator
import kotlin.reflect.KClass

val SigninModule = Kodein.Module("Signin") {
  importOnce(ServiceModule)

  bind<SigninNavigator>() with factory { router: Router ->
    object : ConductorNavigator<SigninNavigator.Effect>(router), SigninNavigator {
      override fun controllerKClassFor(effect: SigninNavigator.Effect): KClass<*> = SigninController::class
      override fun createController(effect: SigninNavigator.Effect) = SigninController()

      override fun pushChangeHandler(effect: SigninNavigator.Effect) =
        if (effect.navigationData.coldLaunch) {
          FadeChangeHandler()
        } else {
          HorizontalChangeHandler()
        }

      override fun popChangeHandler(effect: SigninNavigator.Effect) =
        pushChangeHandler(effect)
    }
  }
}
