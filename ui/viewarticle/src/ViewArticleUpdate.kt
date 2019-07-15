package realworld.ui.articleview

import kt.mobius.Next.Companion.noChange
import kt.mobius.Update

val ViewArticleUpdate =
  Update<ViewArticleModel, ViewArticleEvent, Any> { model, event ->
    noChange()
  }
