package realworld.ui.feed

import knit.feature.paging.PagingModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import realworld.model.Article

@Serializable
data class FeedModel(
  val refreshing: Boolean = false,
  val articles: List<Article> = emptyList(),
  val isUserAuthenticated: Boolean = false,
  val isViewingUserFeed: Boolean = true,
  val profileImageUrl: String = "",
  override val hasMorePages: Boolean = false,
  override val isLoadingPage: Boolean = true
) : PagingModel {
  companion object {
    val DEFAULT = FeedModel()

    fun deserialize(serialModel: String): FeedModel {
      return Json.nonstrict.parse(serializer(), serialModel)
    }
  }

  fun serialize(): String {
    return Json.nonstrict.stringify(serializer(), this)
  }

  override fun toString(): String {
    return "FeedModel(refreshing=$refreshing, " +
        "articles=(size:${articles.size}), " +
        "hasMorePages=$hasMorePages, " +
        "isLoading=$isLoadingPage, " +
        "isUserAuthenticated=$isUserAuthenticated, " +
        "isViewingUserFeed=$isViewingUserFeed, " +
        "profileImageUrl='$profileImageUrl')"
  }
}
