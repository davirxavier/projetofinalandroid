package davi.xavier.aep.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import davi.xavier.aep.data.entities.StatEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

class StatsViewModel(private val repository: StatRepository) : ViewModel() {
    private val statsData: LiveData<List<StatEntry>> by lazy { 
        repository.getStats()
    }
    
    fun getStats(): LiveData<List<StatEntry>> {
        return statsData
    }
    
    suspend fun createStat() {
        withContext(Dispatchers.IO) {
            repository.insert()
        }
    }
    
    suspend fun removeStat(uid: String) {
        withContext(Dispatchers.IO) {
            repository.deleteStat(uid)
        }
    }
    
    suspend fun updateStat(startTime: LocalDateTime,
                           endTime: LocalDateTime?,
                           distance: Int?,
                           calories: Int?,
                           uid: String) {
        withContext(Dispatchers.IO) {
            val stat = StatEntry(startTime, endTime, distance, calories, uid)
            
            repository.updateStat(stat)
        }
    }

    suspend fun updateStat(statEntry: StatEntry) {
        withContext(Dispatchers.IO) {
            repository.updateStat(statEntry)
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
