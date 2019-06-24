package knit.navigation.core


/**
 * A base Effect type which ensures availability of a
 * basic set of navigation information for navigation
 * or backstack management, animations, etc.
 *
 * @see navigationData The container type for navigation related data.
 */
interface NavigationEffect {
  val navigationData: NavigationData
}
