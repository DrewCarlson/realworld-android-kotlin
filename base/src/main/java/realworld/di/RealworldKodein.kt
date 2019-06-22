package realworld.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import kotlinx.serialization.json.Json
import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import realworld.repository.ArticleRepository

/**
 * Here we connect all of the Kodein modules across build
 * flavors, build types, and gradle modules.
 *
 * This Kodein will be provided to the application via
 * [realworld.android.ConduitApp].
 */
val RealworldKodein: Kodein = Kodein.lazy {

  import(ServiceModule)

  bind() from singleton {
    ArticleRepository(instance())
  }

  bind<HttpClient>() with singleton {
    HttpClient(Android) {
      install(JsonFeature) {
        serializer = KotlinxSerializer(Json.nonstrict)
      }
    }
  }
}
