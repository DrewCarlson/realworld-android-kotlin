package realworld.ui.articleview

import knit.loop.conductor.KnitConductorController
import knit.loop.core.KnitModelSerializer
import knit.ui.core.setUrl
import kotlinx.android.synthetic.main.controller_view_article.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.functions.Consumer
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
  private var article: Article? = null
) : KnitConductorController<ViewArticleModel, Any, Any>() {

  override val layoutId = R.layout.controller_view_article
  override fun defaultModel() = ViewArticleModel(article)

  override fun buildLoopFactory() = loopFactory {
    update = Update { model, event ->
      when (event) {
        else -> noChange()
      }
    }
  }

  override fun modelSerializer() =
    object : KnitModelSerializer<ViewArticleModel> {
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
