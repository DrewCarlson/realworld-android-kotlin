package realworld.ui.feed

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.res.getColorOrThrow
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.LinearLayoutManager
import com.bluelinelabs.conductor.Router
import kt.mobius.Connectable
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import kt.mobius.extras.CompositeEffectHandler
import kt.mobius.functions.Consumer
import org.kodein.di.direct
import org.kodein.di.erased.instance
import realworld.android.mapConnection
import realworld.ui.core.BaseController
import realworld.ui.feed.databinding.ControllerFeedBinding
import realworld.ui.navigation.SigninNavigator
import realworld.ui.navigation.ViewArticleNavigator

/** Displays the global feed and user feed if authenticated. */
class FeedController : BaseController<FeedModel, Any, Any>() {

  companion object {
    private const val LAYOUT_MANAGER_KEY = "FeedController.LAYOUT_MANAGER"
    private const val PREFETCH_OFFSET = 6
  }

  private var restoredLayoutManagerState: Parcelable? = null

  override fun defaultModel() = FeedModel.DEFAULT
  override fun init() = FeedInit
  override fun update() = Update<FeedModel, Any, Any> { model, event ->
    when (event) {
      is FeedEvent -> FeedUpdate(model, event)
      else -> noChange()
    }
  }

  override fun bindingIds() = BindingIds(
    model = BR.model,
    output = BR.output,
    title = BR.title
  )

  override fun createBinding(inflater: LayoutInflater, container: ViewGroup) =
    ControllerFeedBinding.inflate(inflater, container, false)
      .apply {
        buttonNewArticle.setOnClickListener {
          output?.accept(FeedEvent.OnCreateArticleClicked)
        }

      }

  override fun effectHandler() =
    CompositeEffectHandler.from<Any, Any>(
      Connectable { FeedEffectHandler(it, kodein) },
      Connectable {
        mapConnection(direct.instance<Router, ViewArticleNavigator>(arg = router))
      },
      Connectable {
        mapConnection(direct.instance<Router, SigninNavigator>(arg = router))
      }
    )
  //bindMenuItemClick(R.id.item_signin, FeedEvent.OnSigninClicked)
  /*override fun bindView(model: FeedModel, output: Consumer<Any>) = bindViews(output) {
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
  }*/

  /*override fun render(model: FeedModel) {
    buttonNewArticle.isVisible = model.isUserAuthenticated
    refreshFeed.isRefreshing = model.refreshing
    recyclerFeed.adapter<FeedAdapter>().apply {
      articles = model.articles
    }
  }*/

  private fun createLayoutManager(context: Context): LinearLayoutManager {
    return LinearLayoutManager(context).apply {
      restoredLayoutManagerState?.let(::onRestoreInstanceState)
    }
  }

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    /*recyclerFeed?.layoutManager?.apply {
      outState.putParcelable(LAYOUT_MANAGER_KEY, onSaveInstanceState())
    }*/
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

