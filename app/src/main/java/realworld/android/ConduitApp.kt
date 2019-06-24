package realworld.android

import android.app.Application
import com.bluelinelabs.conductor.Router
import knit.navigation.launch.LaunchScreen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance
import realworld.ui.articleview.ViewArticleModule
import realworld.ui.feed.FeedModule
import realworld.ui.navigation.FeedNavigator
import realworld.ui.signin.SigninModule
import realworld.ui.signup.SignupModule

/**
 *
 */
class ConduitApp : Application(), KodeinAware {
  override val kodein by Kodein.lazy {
    import(androidXModule(this@ConduitApp))

    importOnce(FeedModule)
    importOnce(SigninModule)
    importOnce(ViewArticleModule)
    importOnce(SignupModule)

    bind<LaunchScreen>() with factory { router: Router ->
      LaunchScreen({ FeedNavigator.Effect(it) }) {
        instance<Router, FeedNavigator>(arg = router)
      }
    }
  }
}
