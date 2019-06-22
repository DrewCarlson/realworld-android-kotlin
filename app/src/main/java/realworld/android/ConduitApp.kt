package realworld.android

import android.app.Application
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.subKodein
import realworld.di.RealworldKodein

class ConduitApp : Application(), KodeinAware {
  override val kodein = subKodein(RealworldKodein) {
    import(androidXModule(this@ConduitApp))
  }
}
