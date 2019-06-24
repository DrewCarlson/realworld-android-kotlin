package realworld.ui.feed

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import knit.feature.paging.PagingModel
import knit.feature.paging.recyclerview.BaseViewHolder
import knit.ui.core.setUrl
import kotlinx.android.synthetic.main.view_article.*
import kt.mobius.functions.Consumer
import realworld.model.Article

/** Displays a list of [Article]s and paging states. */
class FeedAdapter(
  articles: List<Article>,
  pagingModel: PagingModel,
  private val output: Consumer<Any>
) : RecyclerView.Adapter<FeedAdapter.ViewHolder>() {

  /**
   * The list of articles to be displayed.
   * Calls [notifyDataSetChanged] when set and ignores duplicate lists.
   */
  var articles: List<Article> = articles
    set(value) {
      if (field == value) return
      field = value
      notifyDataSetChanged()
    }

  /**
   * The paging state of our list.
   * Calls [notifyDataSetChanged] when set and ignores duplicate lists.
   */
  var pagingModel: PagingModel = pagingModel
    set(value) {
      if (field == value) return
      field = value
      notifyDataSetChanged()
    }

  /**
   * Returns the size of [articles].
   *
   * Each not list item type that is displayed increments the count.
   */
  override fun getItemCount(): Int {
    val extras = listOf(
      pagingModel.isLoadingPage
      // TODO: hasError
    )
    return articles.size + extras.filter { it }.count()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
    val inflater = LayoutInflater.from(parent.context)
    return when (viewType) {
      R.id.view_type_feed_article -> {
        val view = inflater.inflate(R.layout.view_article, parent, false)
        ViewHolder.ArticleViewHolder(view).apply {
          itemView.setOnClickListener {
            output.accept(FeedEvent.OnArticleClicked(articles[adapterPosition]))
          }
        }
      }
      R.id.view_type_feed_loading -> {
        val view = inflater.inflate(R.layout.view_loading, parent, false)
        ViewHolder.LoadingViewHolder(view)
      }
      else -> error("Unknown viewType $viewType")
    }
  }

  override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    when (holder) {
      is ViewHolder.ArticleViewHolder -> {
        holder.renderArticle(articles[position])
      }
      is ViewHolder.LoadingViewHolder -> {
      }
      is ViewHolder.ErrorViewHolder -> {

      }
    }
  }

  override fun getItemViewType(position: Int): Int {
    return if (articles.isNotEmpty() && position <= articles.lastIndex) {
      R.id.view_type_feed_article
    } else if (pagingModel.isLoadingPage) {
      R.id.view_type_feed_loading
    } else error("Unknown view type for position $position")
  }

  sealed class ViewHolder(view: View) : BaseViewHolder(view) {
    class ArticleViewHolder(view: View) : ViewHolder(view) {
      fun renderArticle(article: Article) {
        labelTitle.text = article.title
        labelCreatedAt.text = article.createdAt
        labelUsername.text = article.author.username
        imageProfile.setUrl(article.author.imageOrDefault())
      }
    }

    class LoadingViewHolder(view: View) : ViewHolder(view)
    class ErrorViewHolder(view: View) : ViewHolder(view)
  }
}
