package realworld.ui.navigation

import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.ControllerChangeHandler
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler

/**
 * A base [Navigator] that uses a Conductor [router] to handle [NavigationEffect]s.
 *
 * Operations are handled in the Main thread.
 */
abstract class ConductorNavigator<T : NavigationEffect>(
  private val router: Router
) : MainNavigator<T>() {

  /** Returns an instance of the target [Controller] configured with [effect]. */
  abstract fun createController(effect: T): Controller

  /** The push [ControllerChangeHandler] used in [createTransaction]. */
  open fun pushChangeHandler(effect: T): ControllerChangeHandler = FadeChangeHandler()

  /** The pop [ControllerChangeHandler] used in [createTransaction]. */
  open fun popChangeHandler(effect: T): ControllerChangeHandler = FadeChangeHandler()

  /** Navigate to the [Controller] returned by [createController]. */
  override fun navigate(effect: T) {
    navigateWithRouter(effect.navigationData) {
      createController(effect).createTransaction(effect, effect.navigationData.animate)
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
   * @param navigationData The [NavigationData] to navigate with.
   * @param createTransaction The [RouterTransaction] to execute.
   */
  fun navigateWithRouter(
    navigationData: NavigationData,
    createTransaction: () -> RouterTransaction
  ) {
    val transaction = createTransaction()
    when {
      navigationData.clearHistory ->
        router.setBackstack(listOf(transaction), null/* TODO: Change handler options */)
      navigationData.replace -> router.replaceTopController(transaction)
      router.hasRootController() -> router.pushController(transaction)
      else -> router.setRoot(transaction)
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
}
