package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Grid(
    var name: String = "",
    var imageUrl: String = "",
    var showDark: Boolean = false) : Parcelable {
    @get:Exclude
    var id = ""
    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(snapshot: DocumentSnapshot): Grid {
            val grid = snapshot.toObject(Grid::class.java)!!
            grid.id = snapshot.id
            return grid
        }
    }
}
