package davi.xavier.aep

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import davi.xavier.aep.data.UserRepository
import davi.xavier.aep.data.StatRepository

class AepApplication : Application() {
    val userRepository: UserRepository by lazy {
        enablePersistence()
        UserRepository() 
    }
    val statRepository: StatRepository by lazy {
        enablePersistence()
        StatRepository() 
    }
    private var persistenceEnabled = false
    
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
    
    private fun enablePersistence() {
        if (!persistenceEnabled) {
            Firebase.database.setPersistenceEnabled(true)
            persistenceEnabled = true
        }
    }
}
