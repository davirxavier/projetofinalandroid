package davi.xavier.aep.home.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import davi.xavier.aep.R
import davi.xavier.aep.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding

    private lateinit var map: GoogleMap
    private lateinit var mapFrag: SupportMapFragment

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
    }

    fun mapCallback(googleMap: GoogleMap) {
        map = googleMap
        
        val quix = LatLng(-4.979296205882684, -39.056413536665154)
        map.addMarker(
            MarkerOptions()
                .position(quix)
                .title("UFC - Campus Quixadá")
        )
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(quix, 15f))
    } // TODO Fix landscape
    // TODO Logout button
}
