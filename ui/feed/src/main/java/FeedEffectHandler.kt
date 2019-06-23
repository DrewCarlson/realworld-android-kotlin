package realworld.ui.feed

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kt.mobius.Connection
import kt.mobius.functions.Consumer
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.erased.instance
import realworld.repository.ArticleRepository
import realworld.repository.RepositoryResult.Fail
import realworld.repository.RepositoryResult.Success
import realworld.ui.feed.FeedEffect.LoadArticles
import realworld.ui.feed.FeedEffect.LoadArticlesFeed
import realworld.ui.feed.FeedEvent.OnArticlesLoadFailed
import realworld.ui.feed.FeedEvent.OnArticlesLoaded
import kotlin.coroutines.CoroutineContext

class FeedEffectHandler(
  private val output: Consumer<Any>,
  override val kodein: Kodein
) : Connection<Any>, KodeinAware, CoroutineScope {

  private val job = SupervisorJob()
  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Main

  private val articleRepository by instance<ArticleRepository>()

  override fun accept(value: Any) {
    launch {
      when (value) {
        is LoadArticles -> loadArticles(value.offset)
        is LoadArticlesFeed -> loadArticlesFeed(value.offset)
      }
    }
  }

  private suspend fun loadArticles(offset: Int) {
    val result = articleRepository.getArticles(offset = offset)
    output.accept(
      when (result) {
        is Success -> OnArticlesLoaded(result.data)
        is Fail -> OnArticlesLoadFailed(result.error.message ?: "Error loading articles!")
      }
    )
  }

  private suspend fun loadArticlesFeed(offset: Int) {
    val result = articleRepository.getArticlesFeed(offset = offset)
    output.accept(
      when (result) {
        is Success -> OnArticlesLoaded(result.data)
        is Fail -> OnArticlesLoadFailed(result.error.message ?: "Error loading articles!")
      }
    )
  }

  override fun dispose() {
    job.cancel()
  }
}
