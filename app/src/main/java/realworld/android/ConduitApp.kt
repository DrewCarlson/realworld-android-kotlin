package realworld.android

import android.app.Application
import com.bluelinelabs.conductor.Router
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance
import realworld.model.Article
import realworld.model.Profile
import realworld.ui.articleview.ViewArticleModule
import realworld.ui.feed.FeedModule
import realworld.ui.navigation.FeedNavigator
import realworld.ui.navigation.LaunchScreen
import realworld.ui.navigation.ViewArticleNavigator

/**
 *
 */
class ConduitApp : Application(), KodeinAware {
  override val kodein by Kodein.lazy {
    import(androidXModule(this@ConduitApp))

    importOnce(FeedModule)
    //importOnce(SigninModule)
    importOnce(ViewArticleModule)
    //importOnce(SignupModule)

    bind<LaunchScreen>() with factory { router: Router ->
      LaunchScreen({ FeedNavigator.Effect(it) }) {
        instance<Router, FeedNavigator>(arg = router)
      }
    }
    /*
    bind<LaunchScreen>() with factory { router: Router ->
      val article = Article("slug", "title", listOf("tag"), "description", "body", "createdat", "updatedAt", false, 0, Profile("username", "bio", "", false))
      LaunchScreen({ ViewArticleNavigator.Effect(article, it) }) {
        instance<Router, ViewArticleNavigator>(arg = router)
      }
    }
     */
  }
}
