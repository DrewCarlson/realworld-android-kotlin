package realworld.ui.articleview

import kotlinx.android.synthetic.main.controller_view_article.*
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.functions.Consumer
import realworld.base.BaseController
import realworld.base.setUrl
import realworld.model.Article


data class ViewArticleModel(
  val article: Article? = null
)

sealed class ViewArticleEvent {

}

sealed class ViewArticleEffect {

}

class ViewArticleController(
  article: Article? = null
) : BaseController<ViewArticleModel, Any, Any>() {

  override val layoutId = R.layout.controller_view_article
  override val defaultModel = ViewArticleModel(article)
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
    labelTitle.text = model.article?.title
    labelBody.text = model.article?.body
    imageProfile.setUrl(model.article?.author?.imageOrDefault() ?: "")
    labelUsername.text = model.article?.author?.username
  }
}
