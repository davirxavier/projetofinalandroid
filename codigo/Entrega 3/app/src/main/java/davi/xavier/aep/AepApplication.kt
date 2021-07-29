package davi.xavier.aep

import android.app.Application
import davi.xavier.aep.data.StatRepository

class AepApplication : Application() {
    val repository: StatRepository by lazy { StatRepository() }
}
