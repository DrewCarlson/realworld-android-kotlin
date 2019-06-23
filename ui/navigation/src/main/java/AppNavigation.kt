package realworld.ui.navigation

import realworld.model.Article

interface FeedNavigator : Navigator<FeedNavigator.Effect> {

  /**
   *
   */
  data class Effect(
    override val navigationData: NavigationData = NavigationData.default()
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
