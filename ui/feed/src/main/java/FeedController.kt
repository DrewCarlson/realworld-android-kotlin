package realworld.ui.feed

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.res.getColorOrThrow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.Router
import kotlinx.android.synthetic.main.controller_feed.*
import kt.mobius.Connectable
import kt.mobius.extras.CompositeEffectHandler
import kt.mobius.functions.Consumer
import org.kodein.di.direct
import org.kodein.di.erased.instance
import realworld.base.BaseController
import realworld.base.adapter
import realworld.ui.navigation.ViewArticleNavigator

/** Displays the global feed and user feed if authenticated. */
class FeedController : BaseController<FeedModel, Any, Any>() {

  companion object {
    private const val LAYOUT_MANAGER_KEY = "FeedController.LAYOUT_MANAGER"
    private const val PREFETCH_OFFSET = 6
  }

  private var restoredLayoutManagerState: Parcelable? = null

  override val layoutId = R.layout.controller_feed
  override val defaultModel = FeedModel.DEFAULT
  override val update = FeedUpdate
  override val init get() = FeedInit(kodein)
  override val effectHandler = CompositeEffectHandler.from<Any, Any>(
    Connectable {
      FeedEffectHandler(it, kodein)
    },
    Connectable {
      innerConnection(direct.instance<Router, ViewArticleNavigator>(arg = router))
    }
  )

  override val modelSerializer = object : ModelSerializer<FeedModel> {
    override fun deserialize(model: String): FeedModel {
      return FeedModel.deserialize(model)
    }

    override fun serialize(model: FeedModel): String {
      return model.serialize()
    }
  }

  override fun bindView(model: FeedModel, output: Consumer<Any>) = bindViews(output) {
    buttonNewArticle.bindClickEvent(FeedEvent.OnCreateArticleClicked)
    refreshFeed.apply {
      bindRefreshEvent(FeedEvent.OnRefresh)
      setColorSchemeColors(
        context.theme
          .obtainStyledAttributes(intArrayOf(R.attr.colorAccent))
          .getColorOrThrow(0)
      )
    }
    recyclerFeed.apply {
      adapter = FeedAdapter(model.articles, model, output)
      layoutManager = createLayoutManager(context)
      bindLoadPageEvent(FeedEvent.OnLoadMore, PREFETCH_OFFSET)
    }
    onDispose {
      recyclerFeed.clearOnScrollListeners()
    }
  }

  override fun render(model: FeedModel) {
    buttonNewArticle.isVisible = model.isUserAuthenticated
    refreshFeed.isRefreshing = model.refreshing
    recyclerFeed.adapter<FeedAdapter>().apply {
      articles = model.articles
    }
  }

  private fun createLayoutManager(context: Context): LinearLayoutManager {
    return LinearLayoutManager(context).apply {
      restoredLayoutManagerState?.let(::onRestoreInstanceState)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    recyclerFeed?.layoutManager?.apply {
      outState.putParcelable(LAYOUT_MANAGER_KEY, onSaveInstanceState())
    }
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    restoredLayoutManagerState = savedInstanceState.getParcelable(LAYOUT_MANAGER_KEY)
  }
}

