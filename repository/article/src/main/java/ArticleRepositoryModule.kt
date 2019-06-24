package realworld.repository

import org.kodein.di.Kodein
import org.kodein.di.erased.bind
import org.kodein.di.erased.instance
import org.kodein.di.erased.singleton
import realworld.di.ServiceModule

val ArticleRepositoryModule = Kodein.Module("Article Repository") {
  importOnce(ServiceModule)

  bind() from singleton {
    ArticleRepository(instance())
  }
}
