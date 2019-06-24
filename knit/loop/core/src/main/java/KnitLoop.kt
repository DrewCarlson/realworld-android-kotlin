package knit.loop.core

import kt.mobius.Mobius
import kt.mobius.MobiusLoop
import kt.mobius.disposables.Disposable
import kt.mobius.functions.Consumer

/**
 *
 */
interface KnitLoop<M, E, F> {

  fun defaultModel(): M
  fun buildLoopFactory(): MobiusLoop.Factory<M, E, F>

  fun createKnitLoopBuilder(): KnitLoopBuilder<M, E, F> =
    DefaultKnitLoopBuilder()

  fun modelSerializer(): KnitModelSerializer<M>?

  fun loopFactory(
    build: KnitLoopBuilder<M, E, F>.() -> Unit
  ): MobiusLoop.Factory<M, E, F> {
    return createKnitLoopBuilder()
      .apply(build)
      .run {
        Mobius.loop(update, effectHandler)
          .init(init)
          .logger(logger)
          .effectRunner(effectRunner)
          .eventRunner(eventRunner)
      }
  }


  /**
   * In here you will configure any listeners related to
   * Android Views using the synthetic view android extension.
   *
   * If cleanup is required when the view is destroyed, use the
   * returned [Disposable], it be called when cleanup should occur.
   *
   * @param model The current model for setting initial view states.
   * @param output The [E] event consumer output.
   * @see bindViews For a simplified approach to event dispatching
   */
  fun bindView(model: M, output: Consumer<E>): Disposable

  /**
   * Called every time the MobiusLoop model [M] changes.
   *
   * Here you will update the views and UI state using [model].
   */
  fun render(model: M)
}
