package davi.xavier.aep

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import davi.xavier.aep.data.UserRepository
import davi.xavier.aep.data.StatRepository

class AepApplication : Application() {
    val userRepository: UserRepository by lazy { UserRepository() }
    val statRepository: StatRepository by lazy { StatRepository() }
    
    companion object {
        const val EXERCISE_CHANNEL_ID = "EXERCISE_CHANNEL_ID"
    }

    override fun onCreate() {
        super.onCreate()
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(EXERCISE_CHANNEL_ID, "Canal AEP", NotificationManager.IMPORTANCE_DEFAULT)
            getSystemService(NotificationManager::class.java).createNotificationChannel(serviceChannel)
        }
    }
}
