package realworld.ui.feed

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import realworld.model.Article
import realworld.base.PagingModel

@Parcelize
data class FeedModel(
  val refreshing: Boolean = false,
  val articles: List<realworld.model.Article> = emptyList(),
  val isUserAuthenticated: Boolean = false,
  val isViewingUserFeed: Boolean = true,
  val profileImageUrl: String = "",
  override val hasMorePages: Boolean = false,
  override val isLoadingPage: Boolean = true
) : PagingModel, Parcelable {
  companion object {
    val DEFAULT = FeedModel()
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
