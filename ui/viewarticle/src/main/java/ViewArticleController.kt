package realworld.ui.articleview

import kotlinx.android.synthetic.main.controller_view_article.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.functions.Consumer
import realworld.base.BaseController
import realworld.base.setUrl
import realworld.model.Article

@Serializable
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

  override val modelSerializer = object : ModelSerializer<ViewArticleModel> {
    override fun deserialize(model: String): ViewArticleModel {
      return Json.nonstrict.parse(ViewArticleModel.serializer(), model)
    }

    override fun serialize(model: ViewArticleModel): String {
      return Json.nonstrict.stringify(ViewArticleModel.serializer(), model)
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
