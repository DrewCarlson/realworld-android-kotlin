package realworld.base

import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bluelinelabs.conductor.Controller
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import kt.mobius.*
import kt.mobius.android.AndroidLogger
import kt.mobius.android.MobiusAndroid
import kt.mobius.disposables.Disposable
import kt.mobius.functions.Consumer
import kt.mobius.functions.Producer
import kt.mobius.runners.WorkRunner
import kt.mobius.runners.WorkRunners
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein

/**
 * This controller wires together a few important components:
 *
 * 1) Exposes methods for configuring a [MobiusLoop] and
 *   constructs and manages a [MobiusLoop.Controller].
 * 2) The Kodein dependency graph from [ConduitApp].
 * 3) [LayoutContainer] for using synthetic android extensions
 *   in [bindView].
 */
abstract class BaseController<M : Parcelable, E, F>(
  args: Bundle? = null
) : Controller(args), KodeinAware, LayoutContainer {

  /** Acquire Kodein from the [ConduitApp]. */
  override val kodein by kodein {
      requireNotNull(applicationContext) {
        "Kodein cannot be called before applicationContext is set."
      }
  }

  companion object {
    /** The default WorkRunner for handling events. */
    private val sharedEventRunner = Producer {
      WorkRunners.cachedThreadPool()
    }

    /** The default WorkRunner for handling effects. */
    private val sharedEffectRunner = Producer {
      WorkRunners.cachedThreadPool()
    }
  }

  /** The previous model pulled in [onRestoreInstanceState]. */
  private var restoredModel: M? = null

  /** Used by [LayoutContainer] for synthetic views accessors. */
  override var containerView: View? = null

  /** Provides the layout ID used in [onCreateView]. */
  protected abstract val layoutId: Int

  /** The model used when first constructing [loopFactory]. */
  protected abstract val defaultModel: M
  /** The update function used when constructing [loopFactory]. */
  protected abstract val update: Update<M, E, F>
  /** The optional effect handler used when constructing [loopFactory]. */
  protected open val effectHandler: Connectable<F, E> =
    Connectable {
      object : Connection<F> {
        override fun accept(value: F) = Unit
        override fun dispose() = Unit
      }
    }
  /** The optional init function used when constructing [loopFactory]. */
  protected open val init: Init<M, F> =
    Init { First.first(it) }
  /** The optional logger used when constructing [loopFactory]. */
  protected open val logger: MobiusLoop.Logger<M, E, F> =
    AndroidLogger.tag(this::class.java.simpleName)
  /** The optional WorkRunner used when constructing [loopFactory]. */
  protected open val eventRunner: Producer<WorkRunner> = sharedEventRunner
  /** The optional WorkRunner used when constructing [loopFactory]. */
  protected open val effectRunner: Producer<WorkRunner> = sharedEffectRunner

  /**
   * Constructs the [MobiusLoop.Builder] using the overridable
   * properties exposed to the classes children.
   *
   * Do not call during initialization.
   */
  private val loopFactory by lazy {
    Mobius.loop(update, effectHandler)
      .eventRunner(eventRunner)
      .effectRunner(effectRunner)
      .logger(logger)
      .init(init)
  }

  /**
   * Constructs the [MobiusLoop.Controller] using [loopFactory]
   * and the [restoredModel] or [defaultModel].
   *
   * Do not call during initialization.
   */
  protected val controller by lazy {
    MobiusAndroid.controller(loopFactory, restoredModel ?: defaultModel)
  }

  /**
   * Handles inflating a layout using [layoutId], setting up
   * synthetic view android extension, and connecting the view
   * with [controller].
   */
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    return inflater.inflate(layoutId, container, false).also { view ->
      containerView = view
      controller.connect(Connectable {
        object : Connection<M>, Disposable by bindView(controller.model, it) {
          override fun accept(value: M) = render(value)
        }
      })
    }
  }

  override fun onDestroyView(view: View) {
    controller.disconnect()
    clearFindViewByIdCache()
    containerView = null
    super.onDestroyView(view)
  }

  override fun onAttach(view: View) {
    super.onAttach(view)
    controller.start()
  }

  override fun onDetach(view: View) {
    super.onDetach(view)
    controller.stop()
  }

  override fun onSaveViewState(view: View, outState: Bundle) {
    super.onSaveViewState(view, outState)
    outState.putParcelable("model", controller.model)
  }

  override fun onRestoreViewState(view: View, savedViewState: Bundle) {
    super.onRestoreViewState(view, savedViewState)
    restoredModel = savedViewState.getParcelable("model")
  }

  /**
   * In here you will configure any listeners related to
   * Android Views using the synthetic view android extension.
   *
   * If cleanup is required when the view is destroyed, use the
   * returned [Disposable], it be called when cleanup should occur.
   *
   * @param model The current model for setting initial view states.
   * @param output The [E] event consumer output.
   * @see bindViews For a simplified aproach to event dispatching
   */
  abstract fun bindView(model: M, output: Consumer<E>): Disposable

  /**
   * Called every time the MobiusLoop model [M] changes.
   *
   * Here you will update the views and UI state using [model].
   */
  abstract fun render(model: M)

  /**
   *
   */
  protected fun bindViews(
    output: Consumer<E>,
    bindings: ViewBindingScope<M, E>.() -> Unit
  ): Disposable {
    return ViewBindingScope(output) { controller.model }.apply(bindings)
  }

  /**
   *
   */
  class ViewBindingScope<M, E>(
    private val output: Consumer<E>,
    private val resolveModel: () -> M
  ) : Disposable {

    private var onDisposeHandler: (() -> Unit)? = null

    /**
     * Provide additional cleanup to be executed when
     * the view scope is disposed.
     */
    fun onDispose(dispose: () -> Unit) {
      onDisposeHandler = dispose
    }

    /** Dispatch [event] using [View.setOnClickListener] */
    fun View.bindClickEvent(event: E) {
      setOnClickListener { output.accept(event) }
    }

    /** Dispatch [event] using [View.setOnLongClickListener]. */
    fun View.bindLongClickEvent(event: E) {
      setOnLongClickListener {
        output.accept(event)
        true
      }
    }

    /** Dispatch [event] using [SwipeRefreshLayout.setOnRefreshListener]. */
    fun SwipeRefreshLayout.bindRefreshEvent(event: E) {
      setOnRefreshListener {
        output.accept(event)
      }
    }

    /**
     * Dispatches [event] when the recyclerview requests another page.
     * By default it is expected that [M] inherits [PagingModel],
     * if it does not a custom implementation of [extractPagingModel]
     * must be provided.
     *
     * @param event The event type to be dispatched.
     * @param prefetchOffset Dispatch load event early by this many visible items.
     * @param extractPagingModel An optional factory for providing a custom [PagingModel].
     */
    fun RecyclerView.bindLoadPageEvent(
      event: E,
      prefetchOffset: Int = 0,
      extractPagingModel: (@ParameterName("model") M) -> PagingModel = { model ->
        check(model is PagingModel) {
          "Model must inherit PagingModel or provide extractPagingModel to bindLoadPageEvent."
        }
        model
      }
    ) {
      addOnScrollListener(PagingScrollListener(
        layoutManager(),
        { extractPagingModel(resolveModel()).isLoadingPage },
        { extractPagingModel(resolveModel()).hasMorePages },
        prefetchOffset
      ) {
        output.accept(event)
      })
    }

    override fun dispose() {
      onDisposeHandler?.invoke()
      onDisposeHandler = null
    }
  }
}
