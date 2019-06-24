package realworld.ui.navigation

import knit.navigation.core.NavigationData
import knit.navigation.core.NavigationEffect
import knit.navigation.core.Navigator
import realworld.model.Article

interface FeedNavigator : Navigator<FeedNavigator.Effect> {

  /**
   *
   */
  data class Effect(
    override val navigationData: NavigationData =
      NavigationData.default(popToPrevious = true)
  ) : NavigationEffect
}


/** Handles navigating to the Article view screen. */
interface ViewArticleNavigator : Navigator<ViewArticleNavigator.Effect> {

  /**
   *
   */
  data class Effect(
    val article: Article,
    override val navigationData: NavigationData = NavigationData.default()
  ) : NavigationEffect
}


/** */
interface SigninNavigator : Navigator<SigninNavigator.Effect> {

  /**
   *
   */
  data class Effect(
    override val navigationData: NavigationData = NavigationData.default()
  ) : NavigationEffect
}

interface SignupNavigator : Navigator<SignupNavigator.Effect> {

  data class Effect(
    override val navigationData: NavigationData = NavigationData.default()
  ) : NavigationEffect
}
