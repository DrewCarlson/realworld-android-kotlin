package realworld.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

actual val ServiceModule = Kodein.Module("Js Service") {
  importOnce(CoreServiceModule)

  bind<HttpClient>() with singleton {
    HttpClient(Js) {
      install(JsonFeature) {
        serializer = KotlinxSerializer(Json.nonstrict)
      }
    }
  }
}
