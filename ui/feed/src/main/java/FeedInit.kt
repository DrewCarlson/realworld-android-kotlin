package realworld.ui.feed

import kt.mobius.First.Companion.first
import kt.mobius.Init

/**
 *
 */
val FeedInit = Init<FeedModel, Any> { model ->
  if (model.isLoadingPage) {
    if (model.isViewingUserFeed) {
      if (model.isUserAuthenticated) {
        first(model, setOf<Any>(FeedEffect.LoadArticlesFeed(model.articles.size)))
      } else {
        first(
          model.copy(isViewingUserFeed = false),
          setOf<Any>(FeedEffect.LoadArticles(model.articles.size))
        )
      }
    } else {
      first(model, setOf<Any>(FeedEffect.LoadArticles(model.articles.size)))
    }
  } else {
    first(model)
  }
}
