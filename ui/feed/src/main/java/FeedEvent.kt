package realworld.ui.feed

import realworld.model.Article

sealed class FeedEvent {

  /** Dispatch when the user wants to load the latest feed. */
  object OnRefresh : FeedEvent()

  /** Dispatch when the user wants to load more feed items. */
  object OnLoadMore : FeedEvent()

  /** Dispatch when the user wants to create a new article. */
  object OnCreateArticleClicked : FeedEvent()

  /** */
  object OnSigninClicked : FeedEvent()

  /** Dispatch when the user wants to look at the global feed. */
  object OnArticlesClicked : FeedEvent()

  /** Dispatch when the user wants to look at their personal feed. */
  object OnArticlesFeedClicked : FeedEvent()

  /** Dispatch when the user authentication state changes. */
  data class OnUserAuthChanged(val isUserAuthed: Boolean) : FeedEvent()

  /** Dispatch when the user wants to read an article. */
  data class OnArticleClicked(val article: Article) : FeedEvent()

  /** Dispatch when new feed results are available. */
  data class OnArticlesLoaded(val articles: List<Article>) : FeedEvent() {
    override fun toString() = "OnArticlesLoaded(articles=(size:${articles.size}))"
  }

  /** Dispatch when new feed results failed to load. */
  data class OnArticlesLoadFailed(val reason: String) : FeedEvent()
}
