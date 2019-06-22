package realworld.android

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bluelinelabs.conductor.ChangeHandlerFrameLayout
import com.bluelinelabs.conductor.Conductor
import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import realworld.ui.feed.FeedController

/**
 * As the only activity in the app, here we configure
 * Conductor and set the root controller if needed.
 */
class MainActivity : AppCompatActivity() {

  private lateinit var router: Router

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(ChangeHandlerFrameLayout(this).also { view ->
      router = Conductor.attachRouter(this, view, savedInstanceState).apply {
        if (!hasRootController()) {
          setRoot(RouterTransaction.with(FeedController()))
        }
      }
    })
  }

  override fun onBackPressed() {
    if (!router.handleBack()) {
      super.onBackPressed()
    }
  }
}
