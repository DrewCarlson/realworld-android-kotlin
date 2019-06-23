package realworld.android

import android.app.Application
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.subKodein
import realworld.di.RealworldKodein
import realworld.ui.articleview.ViewArticleController
import realworld.ui.feed.FeedController
import realworld.ui.navigation.*
import realworld.ui.signin.SigninController
import kotlin.reflect.KClass

/**
 *
 */
class ConduitApp : Application(), KodeinAware {
  override val kodein by subKodein(RealworldKodein) {
    import(androidXModule(this@ConduitApp))
    import(NavigationModule)

    bind<FeedNavigator>() with factory { router: Router ->
      object : ConductorNavigator<FeedNavigator.Effect>(router), FeedNavigator {
        override fun getKClassForEffect(effect: FeedNavigator.Effect): KClass<*> = FeedController::class
        override fun createController(effect: FeedNavigator.Effect) = FeedController()
      }
    }

    bind<SigninNavigator>() with factory { router: Router ->
      object : ConductorNavigator<SigninNavigator.Effect>(router), SigninNavigator {
        override fun getKClassForEffect(effect: SigninNavigator.Effect): KClass<*> = SigninController::class
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

    bind<ViewArticleNavigator>() with factory { router: Router ->
      object : ConductorNavigator<ViewArticleNavigator.Effect>(router), ViewArticleNavigator {
        override fun getKClassForEffect(effect: ViewArticleNavigator.Effect): KClass<*> = ViewArticleController::class
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
}
