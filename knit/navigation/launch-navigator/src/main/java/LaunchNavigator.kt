package knit.navigation.launch

import android.util.Log
import knit.navigation.core.NavigationData
import knit.navigation.core.NavigationEffect
import knit.navigation.core.Navigator

/** Handles navigating to the initial screen when booting. */
interface LaunchNavigator<F : NavigationEffect> : Navigator<F> {
  /** The NavigationEffect to launch with. */
  val launchEffect: F
}

/**
 * Encapsulates a [launchNavigator] that can be executed
 * without any additional arguments via [launch].
 */
interface LaunchScreen {

  companion object {
    private val TAG = LaunchScreen::class.java.simpleName

    inline operator fun <reified F : NavigationEffect> invoke(
      crossinline buildDefault: (@ParameterName("launchData") NavigationData) -> F,
      crossinline navigator: () -> Navigator<F>
    ): LaunchScreen {
      return object : LaunchScreen {
        override val launchNavigator: LaunchNavigator<NavigationEffect>
          get() = createLaunchNavigator(buildDefault(NavigationData.coldLaunch()), navigator())
      }
    }
  }

  /**
   * The Navigator to execute.
   *
   * @see createLaunchNavigator For creating a [LaunchNavigator].
   */
  val launchNavigator: LaunchNavigator<NavigationEffect>

  /** Dispatch [LaunchNavigator.launchEffect] to [Navigator.navigate]. */
  fun launch() {
    val effect = launchNavigator.launchEffect
    Log.d(TAG, "Executing LaunchNavigator with $effect")
    launchNavigator.accept(effect)
    Log.d(TAG, "Effect dispatched successfully.")
  }
}

/**
 *
 */
inline fun <reified F : NavigationEffect> createLaunchNavigator(
  launchEffect: F,
  delegate: Navigator<F>
): LaunchNavigator<NavigationEffect> {
  return object : LaunchNavigator<NavigationEffect> {
    override val launchEffect: NavigationEffect get() = launchEffect

    override fun navigate(effect: NavigationEffect) {
      if (effect is F) {
        delegate.navigate(effect)
      }
    }

    override fun dispose() {
      delegate.dispose()
    }
  }
}
