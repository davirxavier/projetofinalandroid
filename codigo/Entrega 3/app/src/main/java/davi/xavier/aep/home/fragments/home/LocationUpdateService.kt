package davi.xavier.aep.home.fragments.home

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LiveData
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.StatRepository
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.home.HomeActivity
import davi.xavier.aep.util.Constants
import davi.xavier.aep.util.observeOnce
import kotlinx.coroutines.*
import java.time.Duration
import java.time.LocalDateTime

const val UID_INTENT = "UID_INTENT"
const val WEIGHT_INTENT = "WEIGHT_INTENT"

class LocationUpdateService : Service(), LocationListener {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)
    
    private lateinit var repository: StatRepository
    private var notificationBuilder: NotificationCompat.Builder? = null
    private var currentStatUid: String? = null
    private var currentUserWeight: Double = Constants.DEFAULT_WEIGHT
    private var currentStat: StatEntry? = null
    private var currentLocations: MutableList<LatLng> = mutableListOf()
    
    private var statData: LiveData<StatEntry?>? = null
    
    override fun onCreate() {
        super.onCreate()
        repository = (application as AepApplication).statRepository
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int { // TODO Use stepSensor if location is disabled
        intent?.takeIf { intent.hasExtra(UID_INTENT) && intent.hasExtra(WEIGHT_INTENT) }?.let { originalIntent ->
            currentStatUid = originalIntent.getStringExtra(UID_INTENT)
            currentUserWeight = originalIntent.getDoubleExtra(WEIGHT_INTENT, Constants.DEFAULT_WEIGHT)
            
            val intentNotif = Intent(this, HomeActivity::class.java)
            val newIntent = PendingIntent.getActivity(this, 0, intentNotif, 0)

            if (notificationBuilder == null) {
                notificationBuilder = NotificationCompat.Builder(this, AepApplication.EXERCISE_CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_baseline_directions_run_24)
                    .setContentIntent(newIntent)
                    .setOnlyAlertOnce(true)
            }

            notificationBuilder?.let { builder ->
                startForeground(1,
                    builder
                        .setContentTitle(getString(R.string.notif_title))
                        .setContentText(System.nanoTime().toString())
                        .build())
            }
            
            val locationManager = getSystemService(LocationManager::class.java)
            locationManager.getBestProvider(Criteria(), true)?.let {
                locationManager.requestLocationUpdates(it, 5000, 0f, this)
            }
            
            currentStatUid?.let { uid ->
                scope.launch {
                    statData = repository.getStat(uid).apply {
                        withContext(Dispatchers.Main) { 
                            observeOnce { 
                                currentStat = it
                                currentLocations = it?.locations?.toMutableList() ?: mutableListOf()
                                updateStat()
                            } 
                        }
                    }
                }
            }
        }
        
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onLocationChanged(location: Location) {
        val lastLoc = currentLocations.lastOrNull()
        currentStatUid?.let { uid ->
            if (lastLoc == null ||
                SphericalUtil.computeDistanceBetween(lastLoc, LatLng(location.latitude, location.longitude)) > Constants.LIMIT_DISTANCE
            ) {
                val loc = LatLng(location.latitude, location.longitude)
                scope.launch {
                    repository.addLocation(uid, loc)
                }
                
                currentLocations.add(loc)
                updateStat()

                Log.e("LOCATION", "Added new location to stat.")
            }
        }
    }
    
    private fun updateStat() {
        currentStat?.let {
            val duration = Duration.between(currentStat?.startTime ?: LocalDateTime.now(), LocalDateTime.now()).seconds / 3600.0
            val distanceKm = SphericalUtil.computeLength(currentLocations)/1000
            val calories = Constants.getCaloriesBurned(duration, distanceKm, currentUserWeight).toInt()
            
            it.calories = calories
            it.distance = distanceKm

            scope.launch {
                repository.updateStat(it)

                notificationBuilder?.let { builder ->
                    startForeground(1,
                        builder
                            .setContentTitle(getString(R.string.notif_title))
                            .setContentText(getString(R.string.distance_calories, String.format("%.2f", (it.distance ?: 0).toFloat()), (it.calories ?: 0).toString()))
                            .build())
                }
            }
        }
    }
}
