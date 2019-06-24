package knit.feature.paging.recyclerview

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer


/** Returns [RecyclerView.getAdapter] as [A]. */
// TODO: Move to utility module
inline fun <reified A : RecyclerView.Adapter<*>> RecyclerView.adapter(): A {
  checkNotNull(adapter) { "Expected nonnull adapter." }
  return adapter as? A ?: error("Expected adapter to be of type ${A::class.java.simpleName}")
}


/**
 * A simple base [RecyclerView.ViewHolder] with synthetic
 * view android extension support.
 */
// TODO: Move to utility module
abstract class BaseViewHolder(
  override val containerView: View
) : RecyclerView.ViewHolder(containerView), LayoutContainer


/** Returns [RecyclerView.getLayoutManager] as [LM]. */
// TODO: Move to utility module
inline fun <reified LM : RecyclerView.LayoutManager> RecyclerView.layoutManager(): LM {
  checkNotNull(layoutManager) { "Expected nonnull layoutManager." }
  return layoutManager as? LM ?: error("Expected layoutManager to be of type ${LM::class.java.simpleName}")
}

/**
 * A [RecyclerView.OnScrollListener] that will call [callback]
 * when the bottom of the list is scrolled to, [isLoading] returns
 * false, and [hasMoreItems] is true.
 */
// TODO: Move to knit:feature:pagging-android-recyclerview
class PagingScrollListener(
  /** The item position information source. */
  private val layoutManager: LinearLayoutManager,
  /** Ignore events if a page is already loading. */
  private val isLoading: () -> Boolean,
  /** Ignore events if there are no remaining items. */
  private val hasMoreItems: () -> Boolean,
  private val prefetchOffset: Int,
  /** Called when scrolling and loading conditions are met. */
  private val callback: () -> Unit
) : RecyclerView.OnScrollListener() {

  init {
    require(prefetchOffset > 0) {
      "prefetchOffset must be > 0"
    }
  }

  override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
    super.onScrolled(recyclerView, dx, dy)

    val visibleItemCount = layoutManager.childCount
    val totalItemCount = layoutManager.itemCount
    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

    if (!isLoading() && hasMoreItems() && firstVisibleItemPosition >= 0) {
      val totalVisibleItems = visibleItemCount + firstVisibleItemPosition
      if (totalVisibleItems + prefetchOffset >= totalItemCount) {
        callback()
      }
    }
  }
}
