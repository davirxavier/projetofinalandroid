package davi.xavier.aep.util.builders

import com.google.android.gms.maps.model.LatLng
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
            
            val locations: MutableList<LatLng> = mutableListOf()
            (dataValue["locations"] as String?)?.takeIf { it.isNotEmpty() }?.let { locString ->
                val split = locString.split(",")

                for (i in split.indices step 2) {
                    try {
                        locations.add(LatLng(split[i].toDouble(), split[i+1].toDouble()))
                    } catch (ignored: Exception) {}
                }
            }

            StatEntry(
                startTime = startTime,
                endTime = endTime,
                calories = (dataValue["calories"] as Number?)?.toInt(),
                distance = (dataValue["distance"] as Number?)?.toDouble(),
                obs = dataValue["obs"] as String?,
                locations = locations,
                uid = dataValue["uid"] as String?
            )
        } else null
    }

}
