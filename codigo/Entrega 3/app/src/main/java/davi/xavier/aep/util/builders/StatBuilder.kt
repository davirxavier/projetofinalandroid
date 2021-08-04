package davi.xavier.aep.util.builders

import com.google.firebase.database.DataSnapshot
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.util.FirebaseLiveData
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class StatBuilder : FirebaseLiveData.DataBuilder<StatEntry?> {
    override fun buildData(dataSnapshot: DataSnapshot): StatEntry? {
        val dataValue = dataSnapshot.value as Map<*, *>?
        
        return if (dataValue != null) {
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

            StatEntry(
                startTime = startTime,
                endTime = endTime,
                calories = dataValue["calories"] as Int?,
                distance = dataValue["distance"] as Double?,
                obs = dataValue["obs"] as String?,
                uid = dataValue["uid"] as String?
            )
        } else null
    }

}
