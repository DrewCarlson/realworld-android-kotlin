package realworld.service

import io.ktor.client.HttpClient
import io.ktor.client.engine.ios.Ios
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.singleton

actual val ServiceModule = Kodein.Module("iOS Service") {
  importOnce(CoreServiceModule)

  bind<HttpClient>() with singleton {
    HttpClient(Ios) {
      install(JsonFeature) {
        serializer = KotlinxSerializer(Json.nonstrict)
      }
    }
  }
}
