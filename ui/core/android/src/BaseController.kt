package realworld.ui.core

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.databinding.ViewDataBinding
import com.bluelinelabs.conductor.Controller
import kt.mobius.*
import kt.mobius.First.Companion.first
import kt.mobius.android.AndroidLogger
import kt.mobius.android.MobiusAndroid
import kt.mobius.disposables.Disposable
import kt.mobius.functions.Consumer
import kt.mobius.functions.Producer
import kt.mobius.runners.WorkRunners
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein

/**
 * This controller wires together a few important components:
 *
 * 1) Exposes methods for configuring a [MobiusLoop] and
 *   constructs and manages a [MobiusLoop.Controller].
 * 2) The Kodein dependency graph from [ConduitApp].
 * 3) [LayoutContainer] for using synthetic android extensions in [bindView].
 */
abstract class BaseController<M, E, F>(
  args: Bundle? = null
) : Controller(args),
  KodeinAware {

  /** Acquire Kodein from the parent activity. */
  override val kodein by closestKodein {
    requireNotNull(activity) {
      "Kodein cannot be called before activity is set."
    }
  }

  companion object {
    private val TAG = "BaseController"

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

  /** */
  private var viewBinding: ViewDataBinding? = null

  /** */
  private var menu: Menu? = null

  abstract fun defaultModel(): M
  abstract fun update(): Update<M, E, F>
  abstract fun effectHandler(): Connectable<F, E>

  open fun init(): Init<M, F> = Init { first(it) }
  open fun logger(): MobiusLoop.Logger<M, E, F> =
    AndroidLogger.tag(this::class.java.simpleName)

  private val loopFactory: MobiusLoop.Builder<M, E, F> by lazy {
    Mobius.loop(update(), effectHandler())
      .init(init())
      .logger(logger())
      .effectRunner(sharedEffectRunner)
      .eventRunner(sharedEventRunner)
  }

  /**
   * Constructs the [MobiusLoop.Controller] using [loopFactory]
   * and the [restoredModel] or [defaultModel].
   *
   * Do not call during initialization.
   */
  protected val controller: MobiusLoop.Controller<M, E> by lazy {
    MobiusAndroid.controller(loopFactory, restoredModel ?: defaultModel())
  }

  abstract fun bindingIds(): BindingIds

  /** */
  abstract fun createBinding(inflater: LayoutInflater, container: ViewGroup): ViewDataBinding

  /**
   * Handles inflating a layout using [layoutId], setting up
   * synthetic view android extension, and connecting the view
   * with [controller].
   */
  override fun onCreateView(inflater: LayoutInflater, container: ViewGroup): View {
    val binding = createBinding(inflater, container)
    viewBinding = binding
    return binding.root.also { view ->
      menu = view.findViewWithTag<Toolbar>("toolbar")?.menu
      controller.connect(Connectable {
        val (modelId, outputId, titleId) = bindingIds()
        binding.setVariable(titleId, "Title")
        binding.setVariable(outputId, it)
        binding.executePendingBindings()
        object : Connection<M>, Disposable {
          override fun accept(value: M) {
            binding.setVariable(modelId, value)
            binding.executePendingBindings()
          }

          override fun dispose() {
            binding.unbind()
            viewBinding = null
          }
        }
      })
    }
  }

  override fun onDestroyView(view: View) {
    controller.disconnect()
    menu = null
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
    /*Log.d(TAG, "Attempting to serialize model.")
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
    outState.putString(KEY_MODEL_FILE, file.absolutePath)*/
  }

  override fun onRestoreInstanceState(savedInstanceState: Bundle) {
    super.onRestoreInstanceState(savedInstanceState)
    /*Log.d(TAG, "Looking for model restoration file.")
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
    }*/
  }

  data class BindingIds(
    val model: Int,
    val output: Int,
    val title: Int
  )
}
