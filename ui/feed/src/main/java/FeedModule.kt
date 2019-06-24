package realworld.ui.feed

import com.bluelinelabs.conductor.Router
import knit.navigation.conductor.ConductorNavigator
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import realworld.repository.ArticleRepositoryModule
import realworld.ui.navigation.FeedNavigator
import kotlin.reflect.KClass

val FeedModule = Kodein.Module("Feed") {
  importOnce(ArticleRepositoryModule)

  bind<FeedNavigator>() with factory { router: Router ->
    object : ConductorNavigator<FeedNavigator.Effect>(router), FeedNavigator {
      override fun controllerKClassFor(effect: FeedNavigator.Effect): KClass<*> = FeedController::class
      override fun createController(effect: FeedNavigator.Effect) = FeedController()
    }
  }
}
