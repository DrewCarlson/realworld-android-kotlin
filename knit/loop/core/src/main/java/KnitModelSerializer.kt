package knit.loop.core

/**
 * Represents a class capable of turning a model of [M]
 * into a String for the purpose of disk storage.
 */
interface KnitModelSerializer<M> {
  fun serialize(model: M): String
  fun deserialize(model: String): M
}
