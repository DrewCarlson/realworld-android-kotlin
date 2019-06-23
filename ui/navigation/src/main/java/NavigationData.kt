package realworld.ui.navigation

/**
 * A common set of properties for Navigator execution.
 *
 * This default configuration of [NavigationData] will
 * add incoming screens to the top indefinitely.
 *
 * @see default Add screen to the navigation stack.
 * @see replace Replace the current screen.
 * @see clearHistory Replace the navigation stack with the new screen.
 * @see coldLaunch True for the first navigation event target.
 */
data class NavigationData(
  /** If true, all previous screen will be cleared. */
  val clearHistory: Boolean = false,
  /** If true, the current screen will be replaced with the incoming one. */
  val replace: Boolean = false,
  /** Navigate immediately, skipping any pending animations. */
  val animate: Boolean = true,
  /** True when the effect is dispatched by [LaunchScreen]. */
  val coldLaunch: Boolean = false
) {
  companion object {
    /** Put the incoming screen on the top of the navigation stack. */
    fun default(
      animate: Boolean = true
    ) = NavigationData(
      animate = animate
    )

    /** Replace the top screen of the navigation stack with the incoming screen. */
    fun replace(
      animate: Boolean = true
    ) = NavigationData(
      animate = animate,
      replace = true
    )

    /** Clear the existing navigation stack and add the incoming screen. */
    fun clearHistory(
      animate: Boolean = true
    ) = NavigationData(
      animate = animate,
      clearHistory = true
    )

    /** Adjust things like animations based on first launch (deeplinking). */
    fun coldLaunch(
      animate: Boolean = true
    ) = NavigationData(
      animate = animate,
      coldLaunch = true
    )
  }
}
