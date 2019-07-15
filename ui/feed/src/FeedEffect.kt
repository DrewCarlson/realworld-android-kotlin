package realworld.ui.feed

/** Effects dispatched by [FeedUpdate]. */
sealed class FeedEffect {

  /**
   * Call when more global feed articles are required.
   *
   * @see FeedEvent.OnArticlesLoaded Dispatched on success.
   * @see FeedEvent.OnArticlesLoadFailed Dispatched on failure.
   */
  data class LoadArticles(
    /** The size of the already loaded list of articles. */
    val offset: Int = 0
  ) : FeedEffect()

  /**
   * Call when more user feed articles are required.
   *
   * @see FeedEvent.OnArticlesLoaded Dispatched on success.
   * @see FeedEvent.OnArticlesLoadFailed Dispatched on failure.
   */
  data class LoadArticlesFeed(
    /** The size of the already loaded list of articles. */
    val offset: Int = 0
  ) : FeedEffect()
}
