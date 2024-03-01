package com.example.reading_cycle.location.model

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLocSetBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class LocSetFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var googleMap: GoogleMap
    private var _binding: FragmentLocSetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLocSetBinding.inflate(inflater)
        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.onResume()
        mapView.getMapAsync(this)

        // 각각의 EditText 참조
        val editTextFirst = binding.editFirstaddress
        val editTextLast = binding.editLastaddress

        // 각각의 EditText에서 텍스트 읽어오기 예제
        val textFromFirstEditText = editTextFirst.text.toString()
        val textFromLastEditText = editTextLast.text.toString()

        // 읽어온 값을 사용하거나 처리하는 로직을 여기에 추가
        Log.d("LocSetFragment", "First EditText: $textFromFirstEditText, Last EditText: $textFromLastEditText")
        return binding.root


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



