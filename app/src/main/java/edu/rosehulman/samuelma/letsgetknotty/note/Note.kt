package edu.rosehulman.samuelma.letsgetknotty.note

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import edu.rosehulman.samuelma.letsgetknotty.Constants
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    var description: String = "",
    var showDark: Boolean = false) : Parcelable {
    @get:Exclude
    var id = ""
    @ServerTimestamp
    var created: Timestamp? = null

    companion object {
        const val CREATED_KEY = "created"

        fun fromSnapshot(snapshot: DocumentSnapshot): Note {
            val note = snapshot.toObject(Note::class.java)!!
            note.id = snapshot.id
            Log.d(Constants.TAG , "Note: $note")
            return note
        }
    }
}