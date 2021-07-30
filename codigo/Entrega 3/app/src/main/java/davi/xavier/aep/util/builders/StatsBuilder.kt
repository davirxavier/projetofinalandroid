package davi.xavier.aep.util.builders

import com.google.firebase.database.DataSnapshot
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.util.FirebaseLiveData
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

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

                stats.add(
                    StatEntry(
                    startTime = startTime,
                    endTime = endTime,
                    calories = dataValue["calories"] as Int?,
                    distance = dataValue["distance"] as Int?,
                    uid = dataValue["uid"] as String?
                )
                )
            }
        }

        return stats
    }
}
