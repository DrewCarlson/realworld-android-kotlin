package knit.navigation.conductor

import android.util.Log
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler
import knit.navigation.core.NavigationEffect
import knit.navigation.core.Navigator
import kotlin.reflect.KClass

/**
 * A base [Navigator] that uses a Conductor [router] to handle [NavigationEffect]s.
 *
 * Operations are handled in the Main thread.
 */
abstract class ConductorNavigator<T : NavigationEffect>(
  private val router: Router
) : Navigator<T> {

  companion object {
    private val TAG = ConductorNavigator::class.java.simpleName
  }

  /** Returns an instance of the target [Controller] configured with [effect]. */
  abstract fun createController(effect: T): Controller

  /** Returns the [KClass] of the controller for the configured [effect]. */
  abstract fun controllerKClassFor(effect: T): KClass<*>

  /** The push [ControllerChangeHandler] used in [createTransaction]. */
  open fun pushChangeHandler(effect: T): ControllerChangeHandler = FadeChangeHandler()

  /** The pop [ControllerChangeHandler] used in [createTransaction]. */
  open fun popChangeHandler(effect: T): ControllerChangeHandler = FadeChangeHandler()

  override fun accept(value: T) {
    synchronized(router) {
      checkNotNull(router.activity).runOnUiThread {
        super.accept(value)
      }
    }
  }

  /** Navigate to the [Controller] returned by [createController]. */
  override fun navigate(effect: T) {
    try {
      navigateWithRouter(effect) {
        createController(effect).createTransaction(effect, effect.navigationData.animate)
      }
    } catch (e: Exception) {
      Log.e(TAG, "Failed executing Router navigation to $effect", e)
    }
  }

  /**
   * Handles [NavigationData] options to properly execute the
   * [RouterTransaction] provided by [createTransaction].
   *
   * [NavigationData.clearHistory] replaces the backstack with
   * the incoming Controller.
   * [NavigationData.replace] uses [Router.replaceTopController].
   *
   * A default [NavigationData] will use [Router.setRoot] if no
   * root is set, otherwise [Router.pushController].
   *
   * [createTransaction] is not called if instructed to reuse
   * a [Controller] instance that is already in the stack.
   *
   * @param effect The [NavigationEffect] to navigate with.
   * @param createTransaction The [RouterTransaction] to execute.
   */
  private fun navigateWithRouter(effect: T, createTransaction: () -> RouterTransaction) {
    val navigationData = effect.navigationData
    when {
      navigationData.clearHistory ->
        router.setBackstack(listOf(createTransaction()), null/* TODO: Change handler options */)
      navigationData.replace -> router.replaceTopController(createTransaction())
      navigationData.popIfPrevious -> {
        val backstack = router.backstack
        val isPreviousInStack = backstack
          .filterIndexed { index, routerTransaction ->
            routerTransaction.controller()::class == controllerKClassFor(effect) &&
                index == backstack.lastIndex - 1
          }.size == 1
        if (isPreviousInStack) {
          router.handleBack()
        } else {
          router.pushController(createTransaction())
        }
      }
      navigationData.popToPrevious -> {
        val backstack = router.backstack
        val lastIndex = backstack.indexOfLast { routerTransaction ->
          routerTransaction.controller()::class == controllerKClassFor(effect)
        }

        when {
          lastIndex > 0 -> router.setBackstack(
            backstack.take(lastIndex),
            null/* TODO: Change handler options */
          )
          lastIndex == 0 -> router.popToRoot(null/* TODO: Change handler options */)
          else -> router.pushController(createTransaction())
        }
      }
      router.hasRootController() -> router.pushController(createTransaction())
      else -> router.setRoot(createTransaction())
    }
  }

  /** Create a default [RouterTransaction] from a [Controller]. */
  private fun Controller.createTransaction(event: T, animate: Boolean) =
    RouterTransaction.with(this)
      .let { transaction ->
        if (animate) {
          transaction
            .pushChangeHandler(pushChangeHandler(event))
            .popChangeHandler(popChangeHandler(event))
        } else transaction
      }

  override fun dispose() {

  }
}
