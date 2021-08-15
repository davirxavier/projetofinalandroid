package davi.xavier.aep.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.model.LatLng
import davi.xavier.aep.data.entities.StatEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StatsViewModel(private val repository: StatRepository) : ViewModel() {
    private val statsData: LiveData<List<StatEntry>> by lazy { 
        repository.getStats()
    }
    private val cache: MutableMap<String, LiveData<StatEntry?>> by lazy { 
        mutableMapOf()
    }
    
    fun getStats(): LiveData<List<StatEntry>> {
        return statsData
    }
    
    fun getStat(uid: String): LiveData<StatEntry?> {
        return cache[uid] ?: repository.getStat(uid).also { cache[uid] = it }
    }
    
    suspend fun createStat(): String? {
        return withContext(Dispatchers.IO) {
            repository.finishPendingStats()
            repository.insert()
        }
    }
    
    suspend fun finishStats() {
        withContext(Dispatchers.IO) {
            repository.finishPendingStats()
        }
    }
    
    suspend fun removeStat(uid: String) {
        withContext(Dispatchers.IO) {
            repository.deleteStat(uid)
        }
    }
    
    suspend fun updateStat(distance: Double?,
                           calories: Int?,
                           obs: String?,
                           uid: String) {
        withContext(Dispatchers.IO) {
            val stat = StatEntry(distance = distance, calories = calories, obs = obs, uid = uid)
            repository.updateStat(stat)
        }
    }

    suspend fun updateStat(statEntry: StatEntry) {
        withContext(Dispatchers.IO) {
            repository.updateStat(statEntry)
        }
    }
    
    suspend fun addLatLngToStat(uid: String, loc: LatLng) {
        withContext(Dispatchers.IO) {
            repository.addLocation(uid, loc)
        }
    }

    class StatsViewModelFactory(private val repository: StatRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(StatsViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return StatsViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown viewmodel")
        }
    }
}
