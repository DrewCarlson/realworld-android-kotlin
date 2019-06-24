package knit.ui.core

import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Picasso

/** Load the image from [url] into the ImageView via Picasso. */
fun ImageView.setUrl(url: String) {
  if (url.isBlank()) {
    Log.w("ImageView.setUrl", "Attempted to load blank url.")
    return
  }
  Picasso.get().load(url).fit().into(this)
}
