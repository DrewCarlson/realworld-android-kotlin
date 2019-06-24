package knit.loop.conductor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bluelinelabs.conductor.Controller
import knit.feature.paging.PagingModel
import knit.feature.paging.recyclerview.PagingScrollListener
import knit.feature.paging.recyclerview.layoutManager
import knit.loop.core.DefaultKnitLoopBuilder
import knit.loop.core.KnitLoop
import knit.loop.core.KnitLoopBuilder
import knit.loop.core.KnitModelSerializer
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.*
import kt.mobius.Connectable
import kt.mobius.Connection
import kt.mobius.MobiusLoop
import kt.mobius.android.AndroidLogger
import kt.mobius.android.MobiusAndroid
import kt.mobius.disposables.Disposable
import kt.mobius.functions.Consumer
import kt.mobius.functions.Producer
import kt.mobius.runners.WorkRunners
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import java.io.File


/**
 * This controller wires together a few important components:
 *
 * 1) Exposes methods for configuring a [MobiusLoop] and
 *   constructs and manages a [MobiusLoop.Controller].
 * 2) The Kodein dependency graph from [ConduitApp].
 * 3) [LayoutContainer] for using synthetic android extensions in [bindView].
 */
abstract class KnitConductorController<M, E, F>(
  args: Bundle? = null
) : Controller(args),
  KnitLoop<M, E, F>,
  KodeinAware,
  LayoutContainer {

  /** Acquire Kodein from the parent activity. */
  override val kodein by closestKodein {
    requireNotNull(activity) {
      "Kodein cannot be called before activity is set."
    }
  }

  companion object {
    private val TAG = KnitConductorController::class.java.simpleName

    /** Key used for storing the model file path. */
    private val KEY_MODEL_FILE = "$TAG.MODEL_FILE"

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

  /** */
  private var menu: Menu? = null

  override fun createKnitLoopBuilder(): KnitLoopBuilder<M, E, F> {
    return object : DefaultLoopBuilder<M, E, F>() {
      override var logger: MobiusLoop.Logger<M, E, F> =
        AndroidLogger.tag(this@KnitConductorController.javaClass.simpleName)
    }
  }

  private open class DefaultLoopBuilder<M, E, F> : KnitLoopBuilder<M, E, F> by DefaultKnitLoopBuilder() {
    override var eventRunner = sharedEventRunner
    override var effectRunner = sharedEffectRunner
  }

  override fun modelSerializer(): KnitModelSerializer<M>? = null

  /**
   * Constructs the [MobiusLoop.Builder] using the overridable
   * properties exposed to the classes children.
   *
   * Do not call during initialization.
   */
  private val loopFactory by lazy { buildLoopFactory() }

  /**
   * Constructs the [MobiusLoop.Controller] using [loopFactory]
   * and the [restoredModel] or [defaultModel].
   *
   * Do not call during initialization.
   */
  protected val controller by lazy {
    MobiusAndroid.controller(loopFactory, restoredModel ?: defaultModel())
  }

  /**
   * Handles inflating a layout using [layoutId], setting up
   * synthetic view android extension, and connecting the view
   * with [controller].
   */
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    return inflater.inflate(layoutId, container, false).also { view ->
      containerView = view
      menu = view.findViewWithTag<Toolbar>("toolbar")?.menu
      controller.connect(Connectable {
        object : Connection<M>, Disposable by bindView(controller.model, it) {
          override fun accept(value: M) = render(value)
        }
      })
    }
  }

  override fun onDestroyView(view: View) {
    controller.disconnect()
    menu = null
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

  override fun onSaveInstanceState(outState: Bundle) {
    super.onSaveInstanceState(outState)
    Log.d(TAG, "Attempting to serialize model.")
    val serialModel = try {
      modelSerializer()?.serialize(controller.model) ?: return
    } catch (e: Exception) {
      Log.e(TAG, "Failed to serialize model.", e)
      return
    }
    Log.d(TAG, "Model serialization successful.")
    val file = try {
      Log.d(TAG, "Creating Temp file to save model.")
      createTempFile("app-model-")
    } catch (e: Exception) {
      Log.e(TAG, "Failed to create temp file.", e)
      return
    }
    // Write serialModel to temp file
    try {
      Log.d(TAG, "Writing model to temp file: ${file.absolutePath}")
      file.writeText(serialModel)
    } catch (e: Exception) {
      Log.e(TAG, "Failed to write serialModel to temp file.", e)
      return
    }
    // Success, store filepath for restoration
    outState.putString(KEY_MODEL_FILE, file.absolutePath)
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    Log.d(TAG, "Looking for model restoration file.")
    val filePath = savedInstanceState.getString(KEY_MODEL_FILE) ?: return
    if (filePath.isNotBlank()) {
      Log.d(TAG, "Found model restoration file: $filePath")
      val file = File(filePath)
      val fileText = try {
        file.readText()
      } catch (e: Exception) {
        Log.e(TAG, "Failed to read restoration file.", e)
        return
      }
      restoredModel = try {
        modelSerializer()?.deserialize(fileText)
      } catch (e: Exception) {
        Log.e(TAG, "Failed to deserialize model.", e)
        return
      }
      try {
        file.delete()
      } catch (e: Exception) {
        Log.w(TAG, "Error deleting temporary file.", e)
      }
    }
  }

  /**
   *
   */
  protected fun bindViews(
    output: Consumer<E>,
    bindings: ViewBindingScope<M, E>.() -> Unit
  ): Disposable {
    return ViewBindingScope(output, menu) { controller.model }.apply(bindings)
  }

  /**
   *
   */
  class ViewBindingScope<M, E>(
    private val output: Consumer<E>,
    private val menu: Menu?,
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

    /**  */
    fun EditText.bindTextChange(eventFactory: (String) -> E) {
      addTextChangedListener { text ->
        output.accept(eventFactory(text?.toString() ?: ""))
      }
    }

    fun bindMenuItemClick(itemId: Int, event: E) {
      checkNotNull(menu) {
        // TODO: Tag name as resource
        "Using bindMenuItemClick requires providing a menu.  Tag your Toolbar view with 'toolbar'."
      }
      menu.findItem(itemId).setOnMenuItemClickListener {
        output.accept(event)
        true
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
        kotlin.check(model is PagingModel) {
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
