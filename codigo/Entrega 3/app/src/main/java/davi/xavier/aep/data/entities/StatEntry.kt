package davi.xavier.aep.data.entities

import java.time.LocalDateTime

data class StatEntry(
    var startTime: LocalDateTime? = null,
    var endTime: LocalDateTime? = null,
    var distance: Int? = null,
    var calories: Int? = null,
    var uid: String? = null
)
