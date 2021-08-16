package davi.xavier.aep.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.util.Constants
import davi.xavier.aep.util.FirebaseLiveData
import davi.xavier.aep.util.builders.StatBuilder
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
            
            currentRef?.keepSynced(false)
            val ref = database
                .child(Constants.STATS_PATH)
                .child(it.uid)
            ref.keepSynced(true)
            
            currentRef = ref
            if (updateLiveDataQuery) stats.updateQuery(ref.orderByKey())
        }
    }
    
    fun getStats(): LiveData<List<StatEntry>> {
        return stats
    }
    
    fun getStat(uid: String): LiveData<StatEntry?>  {
        return FirebaseLiveData(currentQuery().child(uid), StatBuilder())
    }
    
    fun insert(): String? {
        val ref = currentQuery().push()
        val key = ref.key

        val map = mapOf(
            "distance" to 0,
            "startTime" to ServerValue.TIMESTAMP,
            "uid" to key
        )

        ref.setValue(map)
        return key
    }
    
    suspend fun finishPendingStats() {
        val snapshotDelete = currentQuery()
            .orderByChild("distance")
            .equalTo(0.0).get().await()
        
        snapshotDelete.ref.updateChildren(
            snapshotDelete.children.map { dataSnapshot -> dataSnapshot.key }.associateWith { null }
        )
        
        val snapshot = currentQuery()
            .orderByChild("endTime")
            .equalTo(null).get().await()

        snapshot.ref.updateChildren(
            snapshot.children.map { dataSnapshot -> dataSnapshot.key }
                .associateBy({ "$it/endTime" }, {ServerValue.TIMESTAMP})
        )
    }
    
    fun updateStat(stat: StatEntry) {
        currentQuery()
            .child(stat.uid!!)
            .updateChildren(stat.toMap().apply {
                remove("locations")
                remove("startTime")
                remove("endTime")
                remove("uid")
            })
    }

    fun deleteStat(uid: String) {
        currentQuery()
            .child(uid)
            .removeValue()
    }
    
    suspend fun addLocation(uid: String, loc: LatLng) {
        val query = currentQuery()
            .child(uid)
            .child(Constants.LOCATION_PATH)
        
        val ds = query.get().await()
        
        var locs = ds.value as String? ?: ""
        locs = locs + (if (locs.isEmpty()) "" else ",") + loc.latitude.toString() + "," + loc.longitude.toString()
        
        query.setValue(locs)
    }

    private fun currentQuery(): DatabaseReference {
        return currentRef ?: throw IllegalStateException("User has not been authenticated or is invalid.")
    }
}
