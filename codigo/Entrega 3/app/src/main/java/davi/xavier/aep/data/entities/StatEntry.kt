package davi.xavier.aep.data.entities

import java.time.LocalDateTime

data class StatEntry(
    var startTime: LocalDateTime?,
    var endTime: LocalDateTime?,
    var distance: Int?,
    var calories: Int?,
    var uid: String?
)
