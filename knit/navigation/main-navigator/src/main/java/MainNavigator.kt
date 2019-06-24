package knit.navigation.main

import android.util.Log
import knit.navigation.core.NavigationEffect
import knit.navigation.core.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking


/**
 * Defines a navigator that will call [navigate] on the Main thread.
 */
abstract class MainNavigator<T : NavigationEffect> : Navigator<T> {

  companion object {
    private val TAG = MainNavigator::class.java.simpleName
  }

  override fun accept(value: T) {
    runBlocking(Dispatchers.Main) {
      Log.d(TAG, "Navigating to $value")
      try {
        navigate(value)
        Log.d(TAG, "Navigation successful")
      } catch (e: Exception) {
        Log.e(TAG, "Navigation failed $value", e)
      }
    }
  }

  override fun dispose() {
  }
}
