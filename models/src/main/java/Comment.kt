package realworld.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class Comment(
  val id: Int,
  val createdAt: String,
  val updatedAt: String,
  val body: String,
  val author: Profile
) : Parcelable

@Parcelize
@Serializable
data class Comments(
  val comments: List<Comment>
) : Parcelable
