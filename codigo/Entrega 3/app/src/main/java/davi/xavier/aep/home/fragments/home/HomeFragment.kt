package davi.xavier.aep.home.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.StatsViewModel
import davi.xavier.aep.data.UserViewModel
import davi.xavier.aep.databinding.FragmentHomeBinding
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), SensorEventListener, LocationListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mapFrag: SupportMapFragment
    private lateinit var locationManager: LocationManager
    private lateinit var sensorManager: SensorManager
    private lateinit var permissionRequest: ActivityResultLauncher<String>
    private var sensorStep: Sensor? = null
    private val statsViewModel: StatsViewModel by activityViewModels { 
        StatsViewModel.StatsViewModelFactory(
            (activity?.application as AepApplication).statRepository
        )
    }
    private val userViewModel: UserViewModel by activityViewModels {
        UserViewModel.AuthViewModelFactory(
            (activity?.application as AepApplication).userRepository
        )
    }
    
    private var isProcessing = true
    private var isPlaying = false
    private var currentSavedPoints: MutableList<LatLng> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                requestLocations()
            } else {
                // TODO Mensagem dizendo que a funcionalidade está desabilitada
            }
        }

        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        sensorStep = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    
    @SuppressLint("MissingPermission")
    private fun requestLocations() {
        val perm = askForLocationPermission()
        
        if (perm) {
            locationManager.getBestProvider(Criteria(), true)?.let {
                locationManager.requestLocationUpdates(it, 5000, 0f, this)
            }
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.onCreate(savedInstanceState)
        mapFrag.getMapAsync { mapCallback(it) }

        binding.caloriasText.text = getString(R.string.calorias, 0)
        binding.distanciaText.text = getString(R.string.distancia, 0)
        
        binding.startButton.setOnClickListener { onPlayClick() }
        
        userViewModel.getUserInfo().observe(viewLifecycleOwner, {
            isPlaying = it != null && it.info.currentStat != null
            isProcessing = false
            
            val drawable = if (isPlaying) R.drawable.ic_baseline_stop_24 else R.drawable.ic_baseline_play_arrow_24
            binding.startButton.setImageDrawable(resources.getDrawable(drawable, requireContext().theme))
            
            if (isPlaying) {
                requestLocations()
            } else {
                currentSavedPoints = mutableListOf()
            }
        })
    }
    
    private fun onPlayClick() {
        if (!askForLocationPermission()) return
        
        if (!isProcessing) {
            lifecycleScope.launch {
                isProcessing = true
                var message: Int? = null
                
                if (isPlaying) {
                    try {
                        statsViewModel.finishStats()
                        userViewModel.setCurrentStatUid(null)
                        
                        locationManager.removeUpdates(this@HomeFragment)
                    } catch (e: Exception) {
                        Log.e("CREATE_STAT_ERROR", e.message ?: "", e)
                        message = R.string.unknown_error
                    }
                } else {
                    try {
                        val newStatUid = statsViewModel.createStat()
                        userViewModel.setCurrentStatUid(newStatUid)
                    } catch (e: Exception) {
                        Log.e("FINISH_STAT_ERROR", e.message ?: "", e)
                        message = R.string.unknown_error
                    }
                }
                
                message?.let { Toast.makeText(requireContext(), message, Toast.LENGTH_LONG) }
                isProcessing = false
            }
        }
    }

    fun mapCallback(googleMap: GoogleMap) {
        val quix = LatLng(-4.979296205882684, -39.056413536665154)
        googleMap.addMarker(
            MarkerOptions()
                .position(quix)
                .title("UFC - Campus Quixadá")
        )
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(quix, 15f))
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event?.takeIf { sensorStep != null && event.sensor == sensorStep }?.let {
            Log.e("Steps", event.values[0].toString())
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    
    private fun askForLocationPermission(): Boolean {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
               return true
            }
            
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // TODO Mensagem explicando a utilização da funcionalidade
                return false
            }
            else -> {
                permissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                return false
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        val last = currentSavedPoints.lastOrNull()
        if (last == null || last.latitude != location.latitude || last.longitude != location.longitude) {
            currentSavedPoints.add(LatLng(location.latitude, location.longitude))
        }
    }
}
