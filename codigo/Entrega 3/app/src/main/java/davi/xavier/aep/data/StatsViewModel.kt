package davi.xavier.aep.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import davi.xavier.aep.home.fragments.stats.StatsViewObject
import java.time.LocalDate

class StatsViewModel : ViewModel() {
    private val stats: MutableLiveData<List<StatsViewObject>> by lazy {
        MutableLiveData<List<StatsViewObject>>(listOf(
            StatsViewObject(11, 12, 10, 500, LocalDate.now().minusDays(4)),
            StatsViewObject(11, 12, 10, 500, LocalDate.now().minusDays(3)),
            StatsViewObject(11, 12, 10, 500, LocalDate.now().minusDays(2)),
            StatsViewObject(11, 12, 10, 500, LocalDate.now().minusDays(1)),
            StatsViewObject(11, 12, 10, 500, LocalDate.now())
        ))
    }
    
    fun getStats(): LiveData<List<StatsViewObject>> {
        return stats;
    }
    
}
