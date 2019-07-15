package realworld.ui.articleview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import kt.mobius.Connectable
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import realworld.android.mapConnection
import realworld.model.Article
import realworld.ui.core.BaseController
import realworld.ui.viewarticle.BR
import realworld.ui.viewarticle.databinding.ControllerViewArticleBinding

class ViewArticleController(
  private var article: Article? = null
) : BaseController<ViewArticleModel, Any, Any>() {

  override fun defaultModel() = ViewArticleModel(article)

  override fun update(): Update<ViewArticleModel, Any, Any> =
    Update { model, event ->
      when (event) {
        is ViewArticleEvent -> ViewArticleUpdate(model, event)
        else -> noChange()
      }
    }

  override fun effectHandler() = Connectable<Any, Any> {
    mapConnection(ViewArticleEffectHandler(kodein, it))
  }

  override fun bindingIds() = BindingIds(
    model = BR.model,
    output = BR.output,
    title = BR.title
  )

  override fun createBinding(inflater: LayoutInflater, container: ViewGroup): ViewDataBinding =
    ControllerViewArticleBinding.inflate(inflater, container, false)
}
