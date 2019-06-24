package realworld.service

import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.content.TextContent
import io.ktor.http.ContentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.json
import realworld.model.*

/**
 * RealWorld Api Spec: https://github.com/gothinkster/realworld/tree/master/api
 */
class ConduitService(
  private val httpClient: HttpClient
) {

  companion object {
    /** The default amount of articles to load in a list. */
    const val ARTICLE_LIMIT = 20
  }

  suspend fun login(email: String, password: String) = onIO {
    httpClient.post<SigninResponse>("api/users/login") {
      jsonBody {
        "user" to json {
          "email" to email
          "password" to password
        }
      }
    }
  }

  suspend fun register(username: String, email: String, password: String) = onIO {
    httpClient.post<RegisterResponse>("api/users") {
      jsonBody {
        "user" to json {
          "email" to email
          "username" to username
          "password" to password
        }
      }
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
      jsonBody {
        email?.let { "email" to it }
        username?.let { "username" to it }
        password?.let { "password" to it }
        image?.let { "image" to it }
        bio?.let { "bio" to it }
      }
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
    httpClient.get<Articles>("api/articles") {
      parameter("tag", tag)
      parameter("author", author)
      parameter("favoritedBy", favoritedBy)
      parameter("limit", limit)
      parameter("offset", offset)
    }
  }

  suspend fun getArticlesFeed(limit: Int = ARTICLE_LIMIT, offset: Int = 0) =
    onIO {
      httpClient.get<Articles>("api/articles/feed") {
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
      jsonBody {
        "title" to title
        "description" to description
        "body" to body
        "tagList" to tagList
      }
    }
  }

  suspend fun updateArticle(
    slug: String,
    title: String? = null,
    description: String? = null,
    body: String? = null
  ) = onIO {
    httpClient.put<Article>("api/articles/$slug") {
      jsonBody {
        title?.let { "title" to it }
        description?.let { "description" to it }
        body?.let { "body" to it }
      }
    }
  }

  suspend fun deleteArticle(slug: String) =
    onIO { httpClient.delete<Unit>("api/articles/$slug") }

  suspend fun addComment(slug: String, body: String) =
    onIO {
      httpClient.post<Comment>("api/articles/$slug/comments") {
        jsonBody {
          "comment" to json {
            "body" to body
          }
        }
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
   *
   */
  // TODO: relocate
  private fun HttpRequestBuilder.jsonBody(builder: JsonObjectBuilder.() -> Unit) {
    this.body = TextContent(json(builder).toString(), ContentType.Application.Json)
  }

  /** Executing the suspend [block] with [Dispatchers.IO]. */
  private suspend fun <R> onIO(block: suspend () -> R): R {
    return withContext(Dispatchers.IO) { block() }
  }
}
