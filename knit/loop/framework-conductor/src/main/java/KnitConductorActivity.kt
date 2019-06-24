package knit.loop.conductor

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import knit.navigation.launch.LaunchScreen
import org.kodein.di.KodeinAware
import org.kodein.di.android.closestKodein
import org.kodein.di.erased.instance

/**
 * As the only activity in the app, here we configure
 * Conductor and drive the app's [LaunchScreen].
 */
class KnitConductorActivity : AppCompatActivity(), KodeinAware {

  /** The [Router] managed by this activity. */
  private lateinit var router: Router

  /** The [LaunchScreen] used for cold app start. */
  private val launchScreen by instance<Router, LaunchScreen>(fArg = { router })

  override val kodein by closestKodein()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(ChangeHandlerFrameLayout(this).also { view ->
      router = Conductor.attachRouter(this, view, savedInstanceState)
    })
    if (!router.hasRootController()) {
      // Conductor initialized with empty stack, cold launch
      launchScreen.launch()
    }
  }

  /**
   * Delegate back press handling to [router] until
   * the last controller is removed.
   */
  override fun onBackPressed() {
    if (!router.handleBack()) {
      super.onBackPressed()
    }
  }
}
