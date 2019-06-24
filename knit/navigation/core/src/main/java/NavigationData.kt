package knit.navigation.core

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
  /** True when the effect is dispatched by [LaunchScreen]. */
  val coldLaunch: Boolean = false,
  /** Navigate immediately, skipping any pending animations. */
  val animate: Boolean = true,
  /**
   * If the incoming and previous screens are identical,
   * the new screen will not be created and the current
   * one will be removed from the stack.
   */
  val popIfPrevious: Boolean = false,
  /**
   * If the incoming screen is in the stack, remove ach screen from
   * the stack until we hit the incoming screen instance.
   */
  val popToPrevious: Boolean = false
) {
  companion object {
    /** Put the incoming screen on the top of the navigation stack. */
    fun default(
      animate: Boolean = true,
      popIfPrevious: Boolean = false,
      popToPrevious: Boolean = false
    ) = NavigationData(
      animate = animate,
      popIfPrevious = popIfPrevious,
      popToPrevious = popToPrevious
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
