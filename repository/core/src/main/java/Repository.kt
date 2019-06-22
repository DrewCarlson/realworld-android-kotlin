package realworld.repository

import android.util.Log
import io.ktor.client.features.ResponseException
import kotlinx.coroutines.*

/**
 * Provides facilities for loading data from multiple sources
 * with a predictable API surface.
 */
interface Repository {

  companion object {
    private val TAG = Repository::class.java.simpleName

    /** The initial attempt count for network operations. */
    private const val NET_INITIAL_ATTEMPT = 1
    /** The maximum number of retry attempts for network errors. */
    private const val NET_RETRY_ATTEMPTS = 3
    /** The last index for repeat([NET_RETRY_ATTEMPTS]). */
    private const val NET_RETRY_LIMIT_INDEX = NET_RETRY_ATTEMPTS - 1
    /** Delay in ms to wait before retrying a network call. */
    private const val NET_RETRY_DELAY_MS: Long = 2 * 1000
    /** Max duration in ms that network calls can run. */
    private const val NET_TIMEOUT_MS: Long = 8 * 1000
  }

  /**
   * Calls the [block] suspend function and returns its value.
   *
   * If [block] throws a temporary error, it will be retried
   * up to [NET_RETRY_ATTEMPTS] times.
   *
   * If [block] does not return in less than [NET_TIMEOUT_MS]
   * it will be cancelled and a retry attempt will occur if
   * not passed the retry limit.
   *
   * Retry attempts are delayed by [NET_RETRY_DELAY_MS].
   */
  suspend fun <R> netRetry(block: suspend CoroutineScope.() -> R): R {
    repeat(NET_INITIAL_ATTEMPT + NET_RETRY_ATTEMPTS) { retryIndex ->
      Log.d(TAG, "Retry attempt $retryIndex")
      try {
        return withTimeout(NET_TIMEOUT_MS, block)
      } catch (e: ResponseException) {
        if (retryIndex == NET_RETRY_LIMIT_INDEX || e.isTerminal()) {
          // Limit reached, bubble error
          throw e
        }
        Log.e(TAG, "Network response error.", e)
      } catch (e: TimeoutCancellationException) {
        Log.e(TAG, "Network timed out.", e)
        if (retryIndex == NET_RETRY_LIMIT_INDEX) {
          // Limit reached, bubble error
          throw e
        }
      } catch (e: CancellationException) {
        Log.d(TAG, "Network request cancelled.", e)
        return@repeat
      } catch (e: Exception) {
        Log.e(TAG, "Unhandled exception", e)
        if (retryIndex == NET_RETRY_LIMIT_INDEX) {
          // Limit reached, bubble error
          throw e
        }
      }
      delay(NET_RETRY_DELAY_MS * (retryIndex + 1))
    }
    error("Retry limit hit, exception should bubbled.")
  }

  /**
   * Returns true if the HTTP status code is a
   * client error or true if it's a server error.
   */
  fun ResponseException.isTerminal(): Boolean {
    val status = response.status
    return when (status.value) {
      in 500..599 -> {
        Log.d(TAG, "Retryable HTTP Status: $status")
        false
      }
      else -> {
        Log.d(TAG, "Terminal HTTP Status: $status")
        true
      }
    }
  }
}

/**
 *
 */
sealed class RepositoryResult<out T> {
  /**
   *
   */
  data class Success<T>(val data: T) : RepositoryResult<T>()

  /**
   *
   */
  data class Fail<T>(val error: Exception) : RepositoryResult<T>()
}
