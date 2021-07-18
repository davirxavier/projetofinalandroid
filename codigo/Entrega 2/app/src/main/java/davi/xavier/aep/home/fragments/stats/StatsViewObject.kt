package davi.xavier.aep.home.fragments.stats

import java.time.LocalDate

class StatsViewObject(
    var periodStartHour: Int,
    var periodEndHour: Int,
    var distance: Int,
    var calories: Int,
    var date: LocalDate
) {
}
