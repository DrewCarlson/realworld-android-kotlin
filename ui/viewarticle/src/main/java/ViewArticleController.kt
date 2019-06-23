package realworld.ui.articleview

import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.functions.Consumer
import realworld.base.BaseController
import realworld.model.Article


data class ViewArticleModel(
  val article: Article? = null
)

class ViewArticleController(

) : BaseController<ViewArticleModel, Any, Any>() {

  override val layoutId = R.layout.controller_view_article
  override val defaultModel = ViewArticleModel()
  override val update = Update<ViewArticleModel, Any, Any> { model, event ->
    when (event) {

      else -> noChange()
    }
  }

  override fun bindView(
    model: ViewArticleModel,
    output: Consumer<Any>
  ) = bindViews(output) {

  }

  override fun render(model: ViewArticleModel) {

  }
}
