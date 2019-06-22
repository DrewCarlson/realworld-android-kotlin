package realworld.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
  val slug: String,
  val title: String,
  val tagList: List<String>,
  val description: String,
  val body: String,
  val createdAt: String,
  val updatedAt: String,
  val favorite: Boolean = false,
  val favoritesCount: Int,
  val author: Profile
) {
  private companion object {
    const val MAX_STR_LENGTH = 15
  }

  override fun toString(): String {
    return "Article(slug='$slug', " +
        "title='${title.take(MAX_STR_LENGTH)}', " +
        "tagList=$tagList, " +
        "description='${description.take(MAX_STR_LENGTH)}', " +
        "body='${body.take(MAX_STR_LENGTH).replace('\n', Char.MIN_VALUE)}', " +
        "createdAt='$createdAt', " +
        "updatedAt='$updatedAt', " +
        "favorite=$favorite, " +
        "favoritesCount=$favoritesCount, " +
        "author=$author)"
  }
}

@Serializable
data class Articles(
  val articles: List<Article>,
  val articlesCount: Int
)
