package realworld.ui.articleview

import org.kodein.di.Kodein
import realworld.service.ServiceModule

expect val ViewArticleModule: Kodein.Module

internal val CoreViewArticleModule = Kodein.Module("View Article") {
  importOnce(ServiceModule)
}
