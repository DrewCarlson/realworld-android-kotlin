package realworld.ui.articleview

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import realworld.ui.core.ConductorNavigator
import realworld.ui.navigation.ViewArticleNavigator
import kotlin.reflect.KClass

actual val ViewArticleModule = Kodein.Module("Android View Article") {
  importOnce(CoreViewArticleModule)

  bind<ViewArticleNavigator>() with factory { router: Router ->
    object : ConductorNavigator<ViewArticleNavigator.Effect>(router), ViewArticleNavigator {
      override fun controllerKClassFor(effect: ViewArticleNavigator.Effect): KClass<*> =
        ViewArticleController::class

      override fun createController(effect: ViewArticleNavigator.Effect) =
        ViewArticleController(effect.article)

      override fun pushChangeHandler(effect: ViewArticleNavigator.Effect) =
        if (effect.navigationData.coldLaunch) {
          FadeChangeHandler()
        } else {
          HorizontalChangeHandler()
        }

      override fun popChangeHandler(effect: ViewArticleNavigator.Effect) =
        pushChangeHandler(effect)
    }
  }
}
