package davi.xavier.aep.util.builders

import com.google.firebase.database.DataSnapshot
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.util.FirebaseLiveData

class StatsBuilder : FirebaseLiveData.DataBuilder<List<StatEntry>> {
    companion object {
        private val statBuilder by lazy { StatBuilder() }
    }
    
    override fun buildData(dataSnapshot: DataSnapshot): List<StatEntry> {
        val stats: MutableList<StatEntry> = mutableListOf()
        for (ds in dataSnapshot.children) {
            val stat = statBuilder.buildData(ds)
            if (stat != null) {
                stats.add(stat)
            }
        }

        return stats
    }
}
