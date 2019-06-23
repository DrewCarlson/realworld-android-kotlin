package realworld.ui.navigation

import com.bluelinelabs.conductor.Router
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.factory
import org.kodein.di.erased.instance

val NavigationModule = Kodein.Module("Navigation") {
  bind<LaunchScreen>() with factory { router: Router ->
    LaunchScreen(FeedNavigator.Effect(NavigationData.coldLaunch())) {
      instance<Router, FeedNavigator>(arg = router)
    }/*
    LaunchScreen(SigninNavigator.Effect(NavigationData.coldLaunch())) {
      instance<Router, SigninNavigator>(arg = router)
    }*/
  }
}
