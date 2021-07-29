package davi.xavier.aep.data

import android.util.Log
import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.util.Constants
import davi.xavier.aep.util.FirebaseLiveData
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class StatRepository {
    private val firebaseAuth: FirebaseAuth by lazy { Firebase.auth }
    private val database: DatabaseReference by lazy { Firebase.database.reference }
    private var currentRef: DatabaseReference? = null
    
    private val stats: FirebaseLiveData<List<StatEntry>> by lazy {
        val data = FirebaseLiveData(null, StatsBuilder())
        firebaseAuth.addAuthStateListener {
            updateInfoQuery()
        }

        return@lazy data
    }

    private fun updateInfoQuery() {
        firebaseAuth.currentUser?.let {
            Log.i("Stats", "Updating stats userId reference.")
            
            val ref = database
                .child(Constants.USER_INFO_PATH)
                .child(it.uid)
                .child(Constants.STATS_PATH)
            
            stats.updateQuery(ref)
            currentRef = ref
        }
    }
    
    fun getStats(): LiveData<List<StatEntry>> {
        return stats
    }
    
    suspend fun insert() {
        val ref = currentQuery().push()

        val map = mapOf(
            "startTime" to ServerValue.TIMESTAMP,
            "uid" to ref.key
        )

        ref.setValue(map).await()
    }

    suspend fun updateStat(stat: StatEntry) {
        currentQuery()
            .child(stat.uid!!)
            .setValue(stat).await()
    }

    suspend fun deleteStat(uid: String) {
        currentQuery()
            .child(uid)
            .removeValue().await()
    }

    private fun currentQuery(): DatabaseReference {
        return currentRef ?: throw IllegalStateException("User has not been authenticated or is invalid.")
    }

    class StatsBuilder : FirebaseLiveData.DataBuilder<List<StatEntry>> {
        override fun buildData(dataSnapshot: DataSnapshot): List<StatEntry> {
            val stats: MutableList<StatEntry> = mutableListOf()
            for (ds in dataSnapshot.children) {
                val dataValue = ds.value as Map<*, *>?
                if (dataValue != null) {
                    val millisStart = dataValue["startTime"] as Long?
                    val millisEnd = dataValue["endTime"] as Long?

                    var startTime: LocalDateTime? = null
                    millisStart?.let {
                        startTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                    }

                    var endTime: LocalDateTime? = null
                    millisEnd?.let {
                        endTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneId.systemDefault())
                    }

                    stats.add(StatEntry(
                        startTime = startTime,
                        endTime = endTime,
                        calories = dataValue["calories"] as Int?,
                        distance = dataValue["distance"] as Int?,
                        uid = dataValue["uid"] as String?
                    ))
                }
            }

            return stats
        }
    }
}
