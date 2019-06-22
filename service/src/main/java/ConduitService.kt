package realworld.service

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.map
import kotlinx.serialization.serializer
import realworld.model.Article
import realworld.model.Comment
import realworld.model.Profile
import realworld.model.User

/**
 * RealWorld Api Spec: https://github.com/gothinkster/realworld/tree/master/api
 */
class ConduitService(
  private val httpClient: HttpClient
) {

  companion object {
    const val ARTICLE_LIMIT = 20
  }

  private suspend fun <R> onIO(func: suspend () -> R): R {
    return withContext(Dispatchers.IO) {
      func()
    }
  }

  suspend fun login(email: String, password: String) =
    onIO {
      httpClient.post<User>("api/users/login") {
        body = jsonBody(
          "email" to email,
          "password" to password
        )
      }
    }

  suspend fun register(username: String, email: String, password: String) =
    onIO {
      httpClient.post<User>("api/users/register") {
        body = jsonBody(
          "email" to email,
          "username" to username,
          "password" to password
        )
      }
    }

  suspend fun getUser() = onIO { httpClient.get<User>("api/user") }

  suspend fun updateUser(
    email: String? = null,
    username: String? = null,
    password: String? = null,
    image: String? = null,
    bio: String? = null
  ) = onIO {
    httpClient.put<User>("api/user") {
      this.body = jsonBody(
        "email" to email,
        "username" to username,
        "password" to password,
        "image" to image,
        "bio" to bio,
        stripNulls = true
      )
    }
  }

  suspend fun getProfile(username: String) =
    onIO { httpClient.get<Profile>("api/profiles/$username") }

  suspend fun followUser(username: String) =
    onIO { httpClient.post<Profile>("api/profiles/$username/follow") }

  suspend fun unfollowUser(username: String) =
    onIO { httpClient.delete<Profile>("api/profiles/$username/follow") }

  suspend fun getArticles(
    tag: String? = "",
    author: String? = "",
    favoritedBy: String? = "",
    limit: Int = ARTICLE_LIMIT,
    offset: Int = 0
  ) = onIO {
    httpClient.get<realworld.model.Articles>("api/articles") {
      parameter("tag", tag)
      parameter("author", author)
      parameter("favoritedBy", favoritedBy)
      parameter("limit", limit)
      parameter("offset", offset)
    }
  }

  suspend fun getArticlesFeed(limit: Int = ARTICLE_LIMIT, offset: Int = 0) =
    onIO {
      httpClient.get<realworld.model.Articles>("api/articles/feed") {
        parameter("limit", limit)
        parameter("offset", offset)
      }
    }

  suspend fun getArticle(slug: String) =
    onIO { httpClient.get<Article>("api/articles/$slug") }

  suspend fun createArticle(
    title: String,
    description: String,
    body: String,
    tagList: List<String>
  ) = onIO {
    httpClient.put<Article>("api/articles") {
      this.body = jsonBody(
        "title" to title,
        "description" to description,
        "body" to body,
        "tagList" to tagList.stringify(),
        stripNulls = true
      )
    }
  }

  suspend fun updateArticle(
    slug: String,
    title: String? = null,
    description: String? = null,
    body: String? = null
  ) = onIO {
    httpClient.put<Article>("api/articles/$slug") {
      this.body = jsonBody(
        "title" to title,
        "description" to description,
        "body" to body,
        stripNulls = true
      )
    }
  }

  suspend fun deleteArticle(slug: String) =
    onIO { httpClient.delete<Unit>("api/articles/$slug") }

  suspend fun addComment(slug: String, body: String) =
    onIO {
      httpClient.post<Comment>("api/articles/$slug/comments") {
        this.body = jsonBody(
          "comment" to mapOf("body" to body).stringify()
        )
      }
    }

  suspend fun getComments(slug: String) =
    onIO { httpClient.post<List<Comment>>("api/articles/$slug/comments") }

  suspend fun deleteComment(slug: String, id: Int) =
    onIO { httpClient.delete<Unit>("api/articles/$slug/comments/$id") }

  suspend fun favoriteArticle(slug: String) =
    onIO { httpClient.post<Article>("api/articles/$slug/favorite") }

  suspend fun unfavoriteArticle(slug: String) =
    onIO { httpClient.delete<Article>("api/articles/$slug/favorite") }

  suspend fun getTags() = onIO { httpClient.get<List<String>>("api/tags") }

  /**
   * Produces a TextContent of "application/json" from a
   * list of pairs.
   *
   * @param fields Pairs of keys and values to be turned into a json object.
   * @param stripNulls When enabled, keys with null values will be removed from the output.
   */
  @Suppress("UNCHECKED_CAST")
  private fun jsonBody(
    vararg fields: Pair<String, Any?>,
    stripNulls: Boolean = false
  ): TextContent {
    val bodyMap = fields.toList()
      .run {
        if (stripNulls)
          filter { it.second != null }
        else this
      }
      .map { pair ->
        when {
          pair.second is String -> pair
          else -> pair.copy(
            second = pair.second.toString()
          )
        } as Pair<String, String>
      }
      .toMap()
    return TextContent(bodyMap.stringify(), ContentType.Application.Json)
  }

  private fun Map<String, String>.stringify(): String {
    val stringMapSerializer = (String.serializer() to String.serializer()).map
    return Json.nonstrict.stringify(stringMapSerializer, this)
  }

  private fun <E> Iterable<E>.stringify() =
    joinToString(
      separator = "\", \"",
      prefix = "[\"",
      postfix = "\"]"
    )
}
