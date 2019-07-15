package realworld.ui.articleview

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kt.mobius.Connection
import kt.mobius.functions.Consumer
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import kotlin.coroutines.CoroutineContext

class ViewArticleEffectHandler(
  override val kodein: Kodein,
  private val consumer: Consumer<Any>
) : Connection<ViewArticleEffect>,
  CoroutineScope,
  KodeinAware {

  private val job = SupervisorJob()

  override val coroutineContext: CoroutineContext
    get() = job + Dispatchers.Default

  override fun accept(value: ViewArticleEffect) {
    when (value) {
    }
  }

  override fun dispose() {
    job.cancel()
  }
}
