package realworld.ui.feed

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.core.content.res.getColorOrThrow
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.Router
import knit.feature.paging.recyclerview.adapter
import knit.loop.conductor.KnitConductorController
import knit.loop.core.KnitModelSerializer
import knit.loop.core.innerConnection
import kotlinx.android.synthetic.main.controller_feed.*
import kt.mobius.Connectable
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.extras.CompositeEffectHandler
import kt.mobius.functions.Consumer
import org.kodein.di.direct
import org.kodein.di.erased.instance
import realworld.ui.navigation.SigninNavigator
import realworld.ui.navigation.ViewArticleNavigator

/** Displays the global feed and user feed if authenticated. */
class FeedController : KnitConductorController<FeedModel, Any, Any>() {

  companion object {
    private const val LAYOUT_MANAGER_KEY = "FeedController.LAYOUT_MANAGER"
    private const val PREFETCH_OFFSET = 6
  }

  private var restoredLayoutManagerState: Parcelable? = null

  override val layoutId = R.layout.controller_feed

  override fun defaultModel() = FeedModel.DEFAULT

  override fun modelSerializer() =
    object : KnitModelSerializer<FeedModel> {
      override fun deserialize(model: String): FeedModel {
        return FeedModel.deserialize(model)
      }

      override fun serialize(model: FeedModel): String {
        return model.serialize()
      }
    }

  override fun buildLoopFactory() = loopFactory {
    init = FeedInit
    update = Update { model: FeedModel, event: Any ->
      // TODO: We will map events like auth state change, to FeedEvents
      //      or map model for event's update function.
      if (event is FeedEvent) {
        FeedUpdate(model, event)
      } else noChange()
    }

    effectHandler = CompositeEffectHandler.from(
      Connectable {
        FeedEffectHandler(it, kodein)
      },
      Connectable {
        innerConnection(direct.instance<Router, ViewArticleNavigator>(arg = router))
      },
      Connectable {
        innerConnection(direct.instance<Router, SigninNavigator>(arg = router))
      }
    )
  }

  override fun bindView(model: FeedModel, output: Consumer<Any>) = bindViews(output) {
    bindMenuItemClick(R.id.item_signin, FeedEvent.OnSigninClicked)
    buttonNewArticle.bindClickEvent(FeedEvent.OnCreateArticleClicked)
    refreshFeed.apply {
      bindRefreshEvent(FeedEvent.OnRefresh)
      setColorSchemeColors(context.getColorForAttr(R.attr.colorAccent))
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

  /**
   *
   */
  // TODO: Relocate
  private fun Context.getColorForAttr(attr: Int): Int {
    return theme.obtainStyledAttributes(intArrayOf(attr))
      .getColorOrThrow(0)
  }
}

