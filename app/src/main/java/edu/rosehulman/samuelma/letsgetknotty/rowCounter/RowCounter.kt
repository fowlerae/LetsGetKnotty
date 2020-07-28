package edu.rosehulman.samuelma.letsgetknotty.rowCounter

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import edu.rosehulman.samuelma.letsgetknotty.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RowCounter(
    var name : String = "",
    var currentRow : Int = 1,
    var repetitionCount : Int  = 1) : Parcelable {
        @get:Exclude var id = ""
        @ServerTimestamp var lastTouched: Timestamp? = null

        companion object {
            const val LAST_TOUCHED_KEY = "lastTouched"

            fun fromSnapshot(snapshot: DocumentSnapshot): RowCounter {
                val rowCounter = snapshot.toObject(RowCounter::class.java)!!
                rowCounter.id = snapshot.id
                Log.d(Constants.TAG, "Row Counter: $rowCounter")
                return rowCounter
            }

        }

    fun increaseRow() {
        currentRow++
    }

    fun decreaseRow() {
        currentRow--
    }

}