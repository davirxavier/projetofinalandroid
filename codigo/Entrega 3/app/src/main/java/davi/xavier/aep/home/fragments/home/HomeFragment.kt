package davi.xavier.aep.home.fragments.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
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
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.SphericalUtil
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.addPolyline
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import davi.xavier.aep.AepApplication
import davi.xavier.aep.R
import davi.xavier.aep.data.StatsViewModel
import davi.xavier.aep.data.UserViewModel
import davi.xavier.aep.data.entities.StatEntry
import davi.xavier.aep.data.entities.User
import davi.xavier.aep.databinding.FragmentHomeBinding
import davi.xavier.aep.util.BitmapHelper
import davi.xavier.aep.util.Constants
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter

import davi.xavier.aep.util.addInfoWindow
import davi.xavier.aep.util.observeOnce
import java.time.Duration
import java.time.LocalDateTime

class HomeFragment : Fragment(), SensorEventListener {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var mapFrag: SupportMapFragment
    private lateinit var locationManager: LocationManager
//    private lateinit var sensorManager: SensorManager
//    private var sensorStep: Sensor? = null
    private lateinit var permissionRequest: ActivityResultLauncher<String>
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
    private val colorPrimary by lazy {
        ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)
    }
    private val colorExtra by lazy { 
        ContextCompat.getColor(requireContext(), R.color.colorExtra)
    }
    private val runIcon: BitmapDescriptor by lazy { 
        BitmapHelper.vectorToBitmap(requireContext(),
            R.drawable.ic_baseline_directions_run_24,
            ContextCompat.getColor(requireContext(), R.color.colorPrimary))
    }
    private val runIconExtra: BitmapDescriptor by lazy {
        BitmapHelper.vectorToBitmap(requireContext(),
            R.drawable.ic_baseline_directions_run_24,
            ContextCompat.getColor(requireContext(), R.color.colorExtra))
    }
    private val args: HomeFragmentArgs by navArgs()
    
    private var isProcessing = true
    private var isPlaying = false
    private var currentSavedPoints: MutableList<LatLng> = mutableListOf()
    private var currentStatUid: String? = null
    private var extraStatUid: String? = null
    
    private lateinit var map: GoogleMap
    private val lineMarkers: MutableMap<Polyline, Marker?> = mutableMapOf()
    private var currentLine: Polyline? = null
    private var currentExtraLine: Polyline? = null
    private var startMarker: Marker? = null
    private var endMarker: Marker? = null
    
    private var currentUser: User? = null
    private var currentStat: StatEntry? = null
    private var extraStat: StatEntry? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        permissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                zoomOnCurrentLocation()
            } else {
                // TODO Mensagem dizendo que a funcionalidade está desabilitada
            }
        }

//        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
//        sensorStep = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)

        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
    
    private fun startService() {
        requireActivity().startService(Intent(requireActivity(), LocationUpdateService::class.java).apply { 
            putExtra(UID_INTENT, currentStatUid)
            currentUser?.info?.weight?.let { 
                putExtra(WEIGHT_INTENT, it)
            }
        })
    }
    
    private fun stopService() {
        requireActivity().stopService(Intent(requireActivity(), LocationUpdateService::class.java))
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
        
        extraStatUid = args.extraUid
        
        mapFrag = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFrag.onCreate(savedInstanceState)
        
        lifecycleScope.launchWhenCreated {
            map = mapFrag.awaitMap()
            map.awaitMapLoad()
            map.setOnPolylineClickListener { 
                Log.e("", lineMarkers.toString())
                
                lineMarkers[it]?.let { marker ->
                    if (marker.isInfoWindowShown) marker.hideInfoWindow()
                    else marker.showInfoWindow()
                }
            }
            
            userViewModel.getUserInfo().observe(viewLifecycleOwner, {
                currentUser = it
                isPlaying = it != null && it.info.currentStat != null
                isProcessing = false

                currentStatUid = it?.info?.currentStat

                val drawable = if (isPlaying) R.drawable.ic_baseline_stop_24 else R.drawable.ic_baseline_play_arrow_24
                binding.startButton.setImageDrawable(resources.getDrawable(drawable, requireContext().theme))

                if (isPlaying) {
                    currentStatUid?.let { uid ->
                        val data = statsViewModel.getStat(uid)
                        data.observe(viewLifecycleOwner) { stat ->
                            stat?.locations?.let { locs -> currentSavedPoints = locs.toMutableList() }
                            currentStat = stat

                            updatePolyline()
                            setUpStartMarker()
                            stat?.endTime?.let { setUpEndMarker() }
                            zoomOnCurrentLocation()
                            updateStatInfo()
                        }
                    }
                } else {
                    currentStat = null
                    currentSavedPoints = mutableListOf()
                    binding.distanciaText.text = getString(R.string.distancia, "0")
                    binding.caloriasText.text = getString(R.string.calorias, "0")
                    zoomOnCurrentLocation()
                }
            })

            extraStatUid?.let { extraUid ->
                statsViewModel.getStat(extraUid).observeOnce(viewLifecycleOwner) { foundStat ->
                    extraStat = foundStat
                    setUpExtra()
                }
            }
        }

        binding.caloriasText.text = getString(R.string.calorias, 0)
        binding.distanciaText.text = getString(R.string.distancia, 0)
        
        binding.startButton.setOnClickListener { onPlayClick() }
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
                        
                        stopService()
                    } catch (e: Exception) {
                        Log.e("CREATE_STAT_ERROR", e.message ?: "", e)
                        message = R.string.unknown_error
                    }
                } else {
                    try {
                        val newStatUid = statsViewModel.createStat()
                        userViewModel.setCurrentStatUid(newStatUid)

                        startService()
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
    
    private fun zoomOnCurrentLocation() {
        if (askForLocationPermission()) {
            var loc: LatLng? = null
            
            locationManager.getBestProvider(Criteria(), false)?.let { provider ->
                locationManager.getLastKnownLocation(provider)?.let { location ->
                    loc = LatLng(location.latitude, location.longitude)
                }
            }

            if (!map.isMyLocationEnabled) map.isMyLocationEnabled = true

            if (currentSavedPoints.isNotEmpty()) {
                val bounds = LatLngBounds.builder()
                currentSavedPoints.forEach { bounds.include(it) }
                loc?.let { bounds.include(it) }
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 80))
            } else {
                loc?.let { map.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 15f)) }
            }
        }
    }
    
    fun setUpStartMarker() {
        currentSavedPoints.firstOrNull()?.let {
            startMarker?.remove()
            startMarker = map.addMarker {
                position(it)
                icon(runIcon)
                title(getString(R.string.start_point))
                snippet(currentStat?.startTime?.format(DateTimeFormatter.ofPattern(getString(R.string.date_hour_format))))
            }
        }
    }
    
    fun setUpEndMarker() {
        currentSavedPoints.takeIf { currentSavedPoints.size > 1 }?.lastOrNull()?.let {
            endMarker?.remove()
            endMarker = map.addMarker {
                position(it)
                icon(runIcon)
                title(getString(R.string.end_point))
                snippet(currentStat?.endTime?.format(DateTimeFormatter.ofPattern(getString(R.string.date_hour_format))))
            }
        }
    }

    fun updatePolyline() {
        currentLine?.let {
            lineMarkers[it]?.remove()
            lineMarkers.remove(it)
            it.remove()
        }
        val line = map.addPolyline {
            addAll(currentSavedPoints)
            color(colorPrimary)
            width(12f)
            clickable(true)
        }
        
        if (currentSavedPoints.size > 1) {
            lineMarkers[line] = line.addInfoWindow(
                map,
                getString(R.string.distancia_curta, String.format("%.2f", currentStat?.distance?.toFloat() ?: 0f)),
                getString(R.string.calorias_curta, currentStat?.calories ?: 0)
            )
        }
        currentLine = line
    }
    
    private fun setUpExtra() {
        extraStat?.takeUnless { it.locations.isNullOrEmpty() }?.let { stat ->
            val locs = stat.locations!!
            
            val line = map.addPolyline {
                addAll(locs)
                color(colorExtra)
                clickable(true)
                width(12f)
            }
            
            val lineInfoMarker = line.addInfoWindow(map, 
                getString(R.string.distancia_curta, String.format("%.2f", stat.distance?.toFloat() ?: 0f)),
                getString(R.string.calorias_curta, stat.calories ?: 0))
            
            currentExtraLine?.let { 
                lineMarkers[it]?.remove()
                lineMarkers.remove(it) 
                it.remove()
            }
            currentExtraLine = line
            lineMarkers[line] = lineInfoMarker
            
            locs.firstOrNull()?.let {
                map.addMarker {
                    position(it)
                    icon(runIconExtra)
                    title(getString(R.string.start_point_marked))
                    snippet(stat.startTime?.format(DateTimeFormatter.ofPattern(getString(R.string.date_hour_format))))
                }
            }
            locs.takeIf { locs.size > 1 }?.lastOrNull()?.let {
                map.addMarker {
                    position(it)
                    icon(runIconExtra)
                    title(getString(R.string.end_point_marked))
                    snippet(stat.endTime?.format(DateTimeFormatter.ofPattern(getString(R.string.date_hour_format))))
                }
            }

            val bounds = LatLngBounds.builder()
            currentSavedPoints.forEach { bounds.include(it) }
            locs.forEach { bounds.include(it) }
            locationManager.getBestProvider(Criteria(), false)?.let { provider ->
                locationManager.getLastKnownLocation(provider)?.let { location ->
                    bounds.include(LatLng(location.latitude, location.longitude))
                }
            }
            
            if (currentSavedPoints.isNotEmpty() || locs.isNotEmpty()) {
                map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 100))
            }
        }
    }
    
    private fun updateStatInfo() {
        val duration = Duration.between(currentStat?.startTime ?: LocalDateTime.now(), LocalDateTime.now()).seconds / 3600.0
        val distanceKm = SphericalUtil.computeLength(currentSavedPoints)/1000
        val calories = Constants.getCaloriesBurned(duration, distanceKm, currentUser?.info?.weight ?: Constants.DEFAULT_WEIGHT).toInt()

        binding.distanciaText.text = getString(R.string.distancia, String.format("%.2f", distanceKm))
        binding.caloriasText.text = getString(R.string.calorias, calories)
    }

    override fun onSensorChanged(event: SensorEvent?) {
//        event?.takeIf { sensorStep != null && event.sensor == sensorStep }?.let {
//            Log.e("Steps", event.values[0].toString())
//        }
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
}
