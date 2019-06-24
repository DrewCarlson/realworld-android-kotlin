package knit.loop.core

import kt.mobius.Connectable
import kt.mobius.Init
import kt.mobius.MobiusLoop
import kt.mobius.Update
import kt.mobius.functions.Producer
import kt.mobius.runners.WorkRunner


interface KnitLoopBuilder<M, E, F> {
  var update: Update<M, E, F>
  var init: Init<M, F>
  var logger: MobiusLoop.Logger<M, E, F>
  var eventRunner: Producer<WorkRunner>
  var effectRunner: Producer<WorkRunner>
  var effectHandler: Connectable<F, E>
}
