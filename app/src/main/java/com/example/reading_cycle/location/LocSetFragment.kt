package com.example.reading_cycle.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentLocSetBinding
import com.example.reading_cycle.location.model.Post
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocSetFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentLocSetBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocSetBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNavigation()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(com.example.reading_cycle.R.id.mapViewLocSet) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.nowlocationLocSet.setOnClickListener {
            getCurrentLocation()
        }

        binding.btnLocSetFinish.setOnClickListener {
            Toast.makeText(requireContext(), "작성을 완료했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSION
            )
            return
        }

        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null && googleMap != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                googleMap?.addMarker(MarkerOptions().position(currentLatLng).title("현재 위치"))
                googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, DEFAULT_ZOOM))

                // 현재 위치 기준으로 반경 내 게시글 필터링
                filterPostsWithinRadius(currentLatLng, RADIUS)
            } else {
                Toast.makeText(requireContext(), "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun filterPostsWithinRadius(currentLatLng: LatLng, radius: Double) {
        // 게시글 목록 (가상의 데이터)
        val posts = listOf(
            Post("게시글 1", LatLng(37.5665, 126.9780)),  // 서울
            Post("게시글 2", LatLng(37.5651, 126.9895)),  // 서울
            Post("게시글 3", LatLng(35.1796, 129.0756))   // 부산
        )

        // 반경 내 게시글 필터링
        val nearbyPosts = posts.filter {
            calculateDistance(currentLatLng, it.location) <= radius
        }

        // 필터링된 게시글을 지도에 표시
        googleMap?.clear()
        googleMap?.addMarker(MarkerOptions().position(currentLatLng).title("현재 위치"))
        for (post in nearbyPosts) {
            googleMap?.addMarker(MarkerOptions().position(post.location).title(post.title))
        }
    }

    private fun calculateDistance(start: LatLng, end: LatLng): Double {
        val radius = 6371.0 // 지구 반지름 (km)
        val dLat = Math.toRadians(end.latitude - start.latitude)
        val dLon = Math.toRadians(end.longitude - start.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(start.latitude)) * cos(Math.toRadians(end.latitude)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * acos(sqrt(a))
        return radius * c
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val DEFAULT_ZOOM = 15f
        private const val RADIUS = 5.0 // 반경 5km
    }
}