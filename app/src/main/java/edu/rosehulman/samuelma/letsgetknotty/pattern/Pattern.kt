package edu.rosehulman.samuelma.letsgetknotty.pattern

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import edu.rosehulman.samuelma.letsgetknotty.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Pattern(
    var name: String = "",
    var imageUrl: String = "",
    var showDark: Boolean = false) : Parcelable {
    @get:Exclude
    var id = ""
    @ServerTimestamp
    var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(snapshot: DocumentSnapshot): Pattern {
            val pattern = snapshot.toObject(Pattern::class.java)!!
            Log.d(Constants.TAG, "Pattern: $pattern")
            pattern.id = snapshot.id
            return pattern
        }
    }
}