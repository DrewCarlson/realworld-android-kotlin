package realworld.ui.feed

import kt.mobius.Effects.effects
import kt.mobius.Next
import kt.mobius.Next.Companion.dispatch
import kt.mobius.Next.Companion.next
import kt.mobius.Next.Companion.noChange
import kt.mobius.Update
import realworld.service.ConduitService
import realworld.ui.feed.FeedEffect.LoadArticles
import realworld.ui.feed.FeedEffect.LoadArticlesFeed
import realworld.ui.feed.FeedEvent.*
import realworld.ui.navigation.SigninNavigator
import realworld.ui.navigation.ViewArticleNavigator


val FeedUpdate = Update<FeedModel, FeedEvent, Any> { model, event ->
  when (event) {
    OnLoadMore -> onLoadMore(model)
    OnArticlesClicked -> onArticlesClicked(model)
    OnArticlesFeedClicked -> onArticlesFeedClicked(model)
    is OnArticlesLoaded -> onArticlesLoaded(model, event)
    is OnArticlesLoadFailed -> onArticlesLoadFailed(model, event)
    is OnArticleClicked -> onArticleClicked(model, event)
    OnRefresh -> onRefresh(model)
    is OnUserAuthChanged -> TODO("OnUserAuthChanged")
    is OnCreateArticleClicked -> TODO("OnCreateArticleClicked")
    OnSigninClicked -> onSigninClicked()
  }
}

private fun onLoadMore(model: FeedModel) = when {
  model.isLoadingPage -> noChange()
  model.hasMorePages -> {
    if (model.isViewingUserFeed) {
      next<FeedModel, Any>(
        model.copy(isLoadingPage = true),
        setOf(LoadArticlesFeed(model.articles.size))
      )
    } else {
      next<FeedModel, Any>(
        model.copy(isLoadingPage = true),
        setOf(LoadArticles(model.articles.size))
      )
    }
  }
  else -> noChange()
}

private fun onArticlesLoaded(model: FeedModel, event: OnArticlesLoaded) = when {
  model.refreshing -> {
    next(
      model.copy(
        refreshing = false,
        articles = event.articles,
        hasMorePages = event.articles.size >= ConduitService.ARTICLE_LIMIT
      ), emptySet<Any>()
    )
  }
  else -> {
    next(
      model.copy(
        isLoadingPage = false,
        articles = model.articles + event.articles,
        hasMorePages = event.articles.size >= ConduitService.ARTICLE_LIMIT
      )
    )
  }
}

private fun onArticlesLoadFailed(
  model: FeedModel,
  event: OnArticlesLoadFailed
): Next<FeedModel, Any> = when {
  model.refreshing -> {
    next(
      model.copy(
        refreshing = false
      )
    )
  }
  else -> {
    next(
      model.copy(
        isLoadingPage = false
      )
    )
  }
}

private fun onArticleClicked(
  model: FeedModel,
  event: OnArticleClicked
): Next<FeedModel, Any> {
  return dispatch(
    effects(ViewArticleNavigator.Effect(event.article))
  )
}

private fun onArticlesClicked(model: FeedModel): Next<FeedModel, Any> {
  return next(
    model.copy(isViewingUserFeed = false),
    effects(
      LoadArticles()
    )
  )
}

private fun onArticlesFeedClicked(model: FeedModel): Next<FeedModel, Any> {
  return next(
    model.copy(isViewingUserFeed = true),
    effects(
      LoadArticlesFeed()
    )
  )
}

private fun onRefresh(model: FeedModel) = when {
  model.isLoadingPage -> noChange()
  model.refreshing -> noChange()
  else -> {
    if (model.isViewingUserFeed) {
      next<FeedModel, Any>(
        model.copy(refreshing = true),
        setOf(LoadArticlesFeed(model.articles.size))
      )
    } else {
      next<FeedModel, Any>(
        model.copy(refreshing = true),
        setOf(LoadArticles(model.articles.size))
      )
    }
  }
}

private fun onSigninClicked() =
  dispatch<FeedModel, Any>(setOf(SigninNavigator.Effect()))
