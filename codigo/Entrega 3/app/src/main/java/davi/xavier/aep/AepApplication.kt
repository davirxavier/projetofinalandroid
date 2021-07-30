package davi.xavier.aep

import android.app.Application
import davi.xavier.aep.data.AuthRepository
import davi.xavier.aep.data.StatRepository

class AepApplication : Application() {
    val authRepository: AuthRepository by lazy { AuthRepository() }
    val statRepository: StatRepository by lazy { StatRepository() }
}
