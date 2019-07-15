package realworld.ui.articleview

import kotlinx.serialization.Serializable
import realworld.model.Article

@Serializable
data class ViewArticleModel(
  val article: Article? = null
)
