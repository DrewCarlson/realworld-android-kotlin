package realworld.service

import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.http.URLProtocol
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton

expect val ServiceModule: Kodein.Module

/**
 * Adds a [ConduitService] singleton.
 * Expects an [HttpClient] to be available.
 */
internal val CoreServiceModule = Kodein.Module("Service") {
  bind<ConduitService>() with singleton {
    val httpClient = instance<HttpClient>().config {
      defaultRequest {
        url {
          host = "conduit.productionready.io"
          protocol = URLProtocol.HTTPS
        }
      }
    }
    ConduitService(httpClient)
  }
}
