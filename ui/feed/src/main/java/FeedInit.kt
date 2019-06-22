package realworld.ui.feed

import kt.mobius.First
import kt.mobius.First.Companion.first
import kt.mobius.Init
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

class FeedInit(
  override val kodein: Kodein
) : Init<FeedModel, Any>, KodeinAware {

  override fun init(model: FeedModel): First<FeedModel, Any> {
    val newModel = model//.copy(isUserAuthenticated = sessionManager.isInitialized())
    return if (model.isLoadingPage) {
      if (model.isViewingUserFeed) {
        if (model.isUserAuthenticated) {
          first(newModel, setOf<Any>(FeedEffect.LoadArticlesFeed(model.articles.size)))
        } else {
          first(
            newModel.copy(isViewingUserFeed = false),
            setOf<Any>(FeedEffect.LoadArticles(model.articles.size))
          )
        }
      } else {
        first(newModel, setOf<Any>(FeedEffect.LoadArticles(model.articles.size)))
      }
    } else {
      first(newModel)
    }
  }
}
