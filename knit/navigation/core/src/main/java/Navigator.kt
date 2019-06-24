package knit.navigation.core

import android.util.Log
import kt.mobius.Connection


/**
 * Defines a component capable of handling a [NavigationEffect]
 * by executing the necessary platform operation to change
 * screens.
 */
interface Navigator<F : NavigationEffect> : Connection<F> {

  companion object {
    private val TAG = Navigator::class.java.simpleName
  }

  /**
   * Called on the Main thread, the function is expected
   * to execute all the necessary tasks to display the
   * screen defined by [F].
   *
   * If this operation can happen on any thread, you must
   * override [accept] and call [navigate] directly.
   */
  fun navigate(effect: F)

  /**
   * Dispatches [value] to [navigate], catching and logging
   * and errors.
   *
   * If overriding, you must handle errors yourself.
   */
  override fun accept(value: F) {
    Log.d(TAG, "Navigating to $value")
    try {
      navigate(value)
      Log.d(TAG, "Navigation successful")
    } catch (e: Exception) {
      Log.e(TAG, "Navigation failed $value", e)
    }
  }
}
