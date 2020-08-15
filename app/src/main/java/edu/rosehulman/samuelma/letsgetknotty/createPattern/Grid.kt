package edu.rosehulman.samuelma.letsgetknotty.createPattern

import android.graphics.Color
import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Grid(var color: Int = Color.WHITE, val index: Int =0, var image : Int? = null) : Parcelable {
    @get:Exclude
    var id = ""
    @ServerTimestamp
    var created: Timestamp? = null

    companion object {
        const val CREATED_KEY = "index"

        fun fromSnapshot(snapshot: DocumentSnapshot): Grid {
            val grid = snapshot.toObject(Grid::class.java)!!
            grid.id = snapshot.id
            return grid
        }
    }
}
