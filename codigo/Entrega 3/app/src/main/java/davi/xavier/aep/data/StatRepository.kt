package davi.xavier.aep.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.util.Constants
import davi.xavier.aep.util.FirebaseLiveData
import davi.xavier.aep.util.builders.StatsBuilder
import kotlinx.coroutines.tasks.await

class StatRepository {
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val database: DatabaseReference by lazy { Firebase.database.reference }
    private var currentRef: DatabaseReference? = null
    
    private val stats: FirebaseLiveData<List<StatEntry>> by lazy {
        return@lazy FirebaseLiveData(null, StatsBuilder())
    }
    
    init {
        updateInfoQuery(false)
        firebaseAuth.addAuthStateListener {
            updateInfoQuery()
        }
    }

    private fun updateInfoQuery(updateLiveDataQuery: Boolean = true) {
        firebaseAuth.currentUser?.let {
            Log.i("Stats", "Updating stats userId reference.")
            
            val ref = database
                .child(Constants.STATS_PATH)
                .child(it.uid)
            
            currentRef = ref
            if (updateLiveDataQuery) stats.updateQuery(ref.orderByKey())
        }
    }
    
    fun getStats(): LiveData<List<StatEntry>> {
        return stats
    }
    
    suspend fun insert(): String? {
        val ref = currentQuery().push()

        val map = mapOf(
            "startTime" to ServerValue.TIMESTAMP,
            "uid" to ref.key
        )

        ref.setValue(map).await()
        return ref.key
    }
    
    suspend fun finishPendingStats() {
        val snapshot = currentQuery()
            .orderByChild("endTime")
            .equalTo(null).get().await()

        snapshot.ref.updateChildren(
            snapshot.children.map { dataSnapshot -> dataSnapshot.key }
                .associateBy({ "$it/endTime" }, {ServerValue.TIMESTAMP})
        )
    }
    
    suspend fun updateStat(stat: StatEntry) {
        currentQuery()
            .child(stat.uid!!)
            .updateChildren(stat.toMap().apply {
                remove("startTime")
                remove("endTime")
                remove("uid")
            }).await()
    }

    suspend fun deleteStat(uid: String) {
        currentQuery()
            .child(uid)
            .removeValue().await()
    }

    private fun currentQuery(): DatabaseReference {
        return currentRef ?: throw IllegalStateException("User has not been authenticated or is invalid.")
    }
}
