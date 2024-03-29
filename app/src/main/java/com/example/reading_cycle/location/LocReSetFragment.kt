package com.example.reading_cycle.location

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentAddSalePostBinding
import com.example.reading_cycle.databinding.FragmentLocResetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocReSetFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fragmentLocResetBinding : FragmentLocResetBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentLocResetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        mainActivity.hideBottomNavigation()
        _binding = FragmentLocResetBinding.inflate(inflater)
        fragmentLocResetBinding = FragmentLocResetBinding.inflate(inflater)
        mapView = binding.mapViewLocReset
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        // 뒤로 가기 버튼 클릭 리스너
        fragmentLocResetBinding.toolbarLayoutLocReset.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.LOC_RESET_FRAGMENT)
        }

        return fragmentLocResetBinding.root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        with(googleMap) {
            moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.7749, -122.4194), 12f))
            addMarker(MarkerOptions().position(LatLng(37.7749, -122.4194)).title("Temporary Marker"))
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}



