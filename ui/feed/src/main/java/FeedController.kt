package realworld.ui.feed

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.res.getColorOrThrow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.controller_feed.*
import kt.mobius.*
import kt.mobius.functions.Consumer
import realworld.base.BaseController
import realworld.base.adapter

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
  override val effectHandler = Connectable<Any, Any> {
    FeedEffectHandler(it, kodein)
  }

  override fun bindView(model: FeedModel, output: Consumer<Any>) = bindViews(output) {
    buttonNewArticle.bindClickEvent(FeedEvent.OnCreateArticleClicked)
    refreshFeed.apply {
      bindRefreshEvent(FeedEvent.OnRefresh)
      setColorSchemeColors(
        context.theme.obtainStyledAttributes(intArrayOf(R.attr.colorAccent))
          .getColorOrThrow(0))
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

