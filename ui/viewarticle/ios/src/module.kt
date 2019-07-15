package realworld.ui.articleview

import org.kodein.di.Kodein

actual val ViewArticleModule = Kodein.Module("iOS View Article") {
  importOnce(CoreViewArticleModule)
}
