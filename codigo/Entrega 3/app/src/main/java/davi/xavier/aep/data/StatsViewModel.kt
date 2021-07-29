package davi.xavier.aep.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.util.Constants
import davi.xavier.aep.util.FirebaseLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class StatsViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val databaseReference = Firebase.database.reference
    private val stats: FirebaseLiveData<List<StatEntry>> by lazy {
        val data = FirebaseLiveData(null, StatsBuilder())
        firebaseAuth.addAuthStateListener {
            updateInfoQuery()
        }
        
        return@lazy data
    }

    private fun updateInfoQuery() {
        firebaseAuth.currentUser?.let {
            Log.i("Stats", "Updating stats userId reference.");
            stats.updateQuery(databaseReference
                .child(Constants.USER_INFO_PATH)
                .child(it.uid)
                .child(Constants.STATS_PATH))
        }
    }
    
    suspend fun createStatEntry(startTime: LocalDateTime,
                                endTime: LocalDateTime?,
                                distance: Int?,
                                calories: Int?) {
        withContext(Dispatchers.IO) {
            val user = validateUserLogged()
            val ref = databaseReference.child(Constants.USER_INFO_PATH)
                .child(user.uid)
                .child(Constants.STATS_PATH)
                .push()

            ref.setValue(StatEntry(startTime, endTime, distance, calories, uid = ref.key)).await()
        }
    }
    
    suspend fun deleteStat(uid: String) {
        withContext(Dispatchers.IO) {
            val user = validateUserLogged()
            databaseReference
                .child(Constants.USER_INFO_PATH)
                .child(user.uid)
                .child(Constants.STATS_PATH)
                .child(uid)
                .removeValue().await()
        }
    }

    fun getStats(): LiveData<List<StatEntry>> {
        return stats
    }

    private fun validateUserLogged(): FirebaseUser {
        return firebaseAuth.currentUser ?: throw IllegalStateException("User auth is invalid.")
    }
    
    class StatsBuilder : FirebaseLiveData.DataBuilder<List<StatEntry>> {
        override fun buildData(dataSnapshot: DataSnapshot): List<StatEntry> {
            val stats: MutableList<StatEntry> = mutableListOf()
            for (ds in dataSnapshot.children) {
                val stat = ds.getValue(StatEntry::class.java)
                if (stat != null) {
                    stats.add(stat)
                }
            }
            
            return stats
        }

    }
}
