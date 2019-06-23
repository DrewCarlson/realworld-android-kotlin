package realworld.ui.navigation

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
    inline operator fun <reified F : NavigationEffect> invoke(
      default: F,
      crossinline navigator: () -> Navigator<F>
    ): LaunchScreen {
      return object : LaunchScreen {
        override val launchNavigator: LaunchNavigator<NavigationEffect>
          get() = createLaunchNavigator(default, navigator())
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
    launchNavigator.accept(launchNavigator.launchEffect)
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
