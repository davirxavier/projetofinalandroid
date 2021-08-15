package davi.xavier.aep.data.entities

import com.google.android.gms.maps.model.LatLng
import java.time.LocalDateTime

data class StatEntry(
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var distance: Double? = null,
    var calories: Int? = null,
    var obs: String? = null,
    var locations: List<LatLng>? = null,
    var uid: String? = null
) {
    fun toMap() = mutableMapOf(
        "startTime" to startTime,
        "endTime" to endTime,
        "distance" to distance,
        "calories" to calories,
        "obs" to obs,
        "uid" to uid
    )
}
