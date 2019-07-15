package realworld.android

import kt.mobius.Connection


/**
 * Returns a new [Connection] of [O] that only accepts values when
 * the returned [Connection] receives a value of type [I] that is also [O].
 */
inline fun <reified I, reified O> mapConnection(connection: Connection<I>): Connection<O> {
  return object : Connection<O> {
    override fun accept(value: O) {
      if (value is I) {
        connection.accept(value)
      }
    }

    override fun dispose() {
      connection.dispose()
    }
  }
}
