package edu.rosehulman.samuelma.letsgetknotty

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Project(
    var name: String = "",
    var imageUrl: String = "",
    var showDark: Boolean = false) : Parcelable {
    @get:Exclude var id = ""
    @ServerTimestamp var lastTouched: Timestamp? = null

    companion object {
        const val LAST_TOUCHED_KEY = "lastTouched"

        fun fromSnapshot(snapshot: DocumentSnapshot): Project {
            val project = snapshot.toObject(Project::class.java)!!
            project.id = snapshot.id
            return project
        }
    }
}
