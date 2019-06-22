package realworld.repository

import io.ktor.client.features.ResponseException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import realworld.model.Article
import realworld.service.ConduitService

/** A [Repository] for managing [Article]s. */
class ArticleRepository(
  private val conduitService: ConduitService
) : Repository {

  suspend fun getArticles(
    tag: String? = "",
    author: String? = "",
    favoritedBy: String? = "",
    limit: Int = 20,
    offset: Int = 0
  ): RepositoryResult<List<Article>> {
    return withContext(Dispatchers.IO) {
      try {
        val articles = netRetry {
          conduitService.getArticles(tag, author, favoritedBy, limit, offset)
        }
        RepositoryResult.Success(articles.articles)
      } catch (exception: ResponseException) {
        RepositoryResult.Fail<List<Article>>(exception)
      } catch (error: Exception) {
        RepositoryResult.Fail<List<Article>>(error)
      }
    }
  }

  suspend fun getArticlesFeed(limit: Int = 20, offset: Int = 0): RepositoryResult<List<Article>> {
    return withContext(Dispatchers.IO) {
      try {
        val articles = netRetry {
          conduitService.getArticlesFeed(limit, offset)
        }
        RepositoryResult.Success(articles.articles)
      } catch (exception: ResponseException) {
        RepositoryResult.Fail<List<Article>>(exception)
      } catch (error: Exception) {
        RepositoryResult.Fail<List<Article>>(error)
      }
    }
  }

  suspend fun getArticle(slug: String): RepositoryResult<Article> {
    return withContext(Dispatchers.IO) {
      try {
        val article = netRetry { conduitService.getArticle(slug) }
        RepositoryResult.Success(article)
      } catch (exception: ResponseException) {
        RepositoryResult.Fail<Article>(exception)
      } catch (error: Exception) {
        RepositoryResult.Fail<Article>(error)
      }
    }
  }

  suspend fun createArticle(
    title: String,
    description: String,
    body: String,
    tagList: List<String>
  ): RepositoryResult<Article> {
    return withContext(Dispatchers.IO) {
      try {
        val article = netRetry {
          conduitService.createArticle(title, description, body, tagList)
        }
        RepositoryResult.Success(article)
      } catch (exception: ResponseException) {
        RepositoryResult.Fail<Article>(exception)
      } catch (error: Exception) {
        RepositoryResult.Fail<Article>(error)
      }
    }
  }

  suspend fun updateArticle(
    slug: String,
    title: String?,
    description: String?,
    body: String?
  ): RepositoryResult<Article> {
    return withContext(Dispatchers.IO) {
      try {
        val article = netRetry {
          conduitService.updateArticle(slug, title, description, body)
        }
        RepositoryResult.Success(article)
      } catch (exception: ResponseException) {
        RepositoryResult.Fail<Article>(exception)
      } catch (error: Exception) {
        RepositoryResult.Fail<Article>(error)
      }
    }
  }

  suspend fun deleteArticle(slug: String): RepositoryResult<Boolean> {
    return withContext(Dispatchers.IO) {
      try {
        netRetry { conduitService.deleteArticle(slug) }
        RepositoryResult.Success(true)
      } catch (exception: ResponseException) {
        RepositoryResult.Fail<Boolean>(exception)
      } catch (error: Exception) {
        RepositoryResult.Fail<Boolean>(error)
      }
    }
  }
}
