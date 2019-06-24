package knit.loop.core

import kt.mobius.*
import kt.mobius.First.Companion.first
import kt.mobius.Next.Companion.noChange
import kt.mobius.functions.Consumer
import kt.mobius.functions.Producer
import kt.mobius.runners.WorkRunner
import kt.mobius.runners.WorkRunners


open class DefaultKnitLoopBuilder<M, E, F> : KnitLoopBuilder<M, E, F> {
  override var update = Update<M, E, F> { _, _ -> noChange() }
  override var init: Init<M, F> = Init { first(it) }
  override var logger: MobiusLoop.Logger<M, E, F> =
    SimpleLogger("KnitLoop")
  override var eventRunner: Producer<WorkRunner> =
    Producer { WorkRunners.immediate() }
  override var effectRunner: Producer<WorkRunner> =
    Producer { WorkRunners.immediate() }
  override var effectHandler: Connectable<F, E> =
    object : Connectable<F, E> {
      override fun connect(output: Consumer<E>): Connection<F> {
        return object : Connection<F> {
          override fun accept(value: F) = Unit
          override fun dispose() = Unit
        }
      }
    }
}
