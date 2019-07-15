package realworld.ui.articleview

import org.kodein.di.Kodein

actual val ViewArticleModule = Kodein.Module("Js View Article") {
  importOnce(CoreViewArticleModule)
}
