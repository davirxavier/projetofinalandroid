package davi.xavier.aep.util

import davi.xavier.aep.data.entities.Sex

object Constants {
    const val USER_INFO_PATH = "user_info"
    const val STATS_PATH = "stats"
    const val LOCATION_PATH = "locations"
    const val CURRENT_STAT = "current_stat"
    
    const val REQUEST_CODE_LOCATION = 0

    const val DEFAULT_WEIGHT: Double = 60.0
    const val DEFAULT_HEIGHT: Int = 160
    const val LIMIT_DISTANCE = 6
    const val STEP_UPDATE_LIMIT = 10
    const val STEP_CONSTANT = 0.414
    
    fun getCaloriesBurned(timeHr: Double, distanceKm: Double, weight: Double): Double {
        val kph = distanceKm / timeHr
        
        return (0.0215 * kph - 0.1765 * kph + 0.8710 * kph + 1.4577) * weight * timeHr
    } // SOURCE: http://www.shapesense.com/fitness-exercise/calculators/walking-calorie-burn-calculator.shtml
    
    fun getDistanceKm(stepCount: Int, heightCm: Int, stepConstant: Double = STEP_CONSTANT): Double {
        return ((heightCm.toDouble() * stepConstant) * stepCount.toDouble()) / 100000.0
    } // SOURCE: https://www.lewisgavin.co.uk/Step-Tracker-Android/
}
