package davi.xavier.aep

import android.app.Application
import davi.xavier.aep.data.UserRepository
import davi.xavier.aep.data.StatRepository

class AepApplication : Application() {
    val userRepository: UserRepository by lazy { UserRepository() }
    val statRepository: StatRepository by lazy { StatRepository() }
}
