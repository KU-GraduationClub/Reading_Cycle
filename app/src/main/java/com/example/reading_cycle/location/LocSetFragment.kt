package com.example.reading_cycle.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLocSetBinding
import com.example.reading_cycle.location.model.LocDataClass
import com.example.reading_cycle.location.vm.LocViewModel
import com.example.reading_cycle.location.vm.LocViewModelFactory
import com.example.reading_cycle.location.repository.LocRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.io.IOException
import java.util.Locale

class LocSetFragment : Fragment(), OnMapReadyCallback {
    private lateinit var mainActivity: MainActivity
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentLocSetBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private val repository = LocRepository()
    private val locViewModel: LocViewModel by activityViewModels {
        LocViewModelFactory(repository)
    }
    private val userViewModel: UserViewModel by activityViewModels() // userViewModel 초기화


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = activity as MainActivity
        _binding = FragmentLocSetBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNavigation()
        // UserViewModel에 사용자 ID 설정
        val userId = getUserIdFromSomeSource()
        userViewModel.setUserId(userId)

        val userIdx = userViewModel.userIdx
        Log.d("PostMainFragment", "User Index: $userIdx")

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapViewLocSet) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        binding.nowlocationLocSet.setOnClickListener {
           requestLocationPermission()
        }

        binding.btnLocSetFinish.setOnClickListener {
            saveLocationAndDisableButton()
        }
        // Observe the saveLocationResult LiveData
        locViewModel.saveLocationResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                Toast.makeText(requireContext(), "위치가 저장되었습니다.", Toast.LENGTH_SHORT).show()
                disableFinishButton()
            } else {
                Toast.makeText(requireContext(), "위치 저장에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap

        googleMap.uiSettings.isZoomControlsEnabled = true

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true
        }

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        getCurrentLocation()
    }

    private fun requestLocationPermission() {
        when {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        Log.d("LocSetFragment", "Requesting current location")
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
                Log.d("LocSetFragment", "Current location: ${location.latitude}, ${location.longitude}")
                // Ensure the location is valid and in the correct sequence
                val currentLatLng = LatLng(location.latitude, location.longitude)
                addMarker(currentLatLng, "현재 위치")
                moveCamera(currentLatLng)

                val address = getAddressFromLatLng(requireContext(), currentLatLng)
                binding.textLocSetNow.text = address
            } else {
                Log.d("LocSetFragment", "Failed to get location")
                Toast.makeText(requireContext(), "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener { e ->
            // Handle potential failure here
            Log.e("LocSetFragment", "위치 조회 실패: ${e.message}", e)
            Toast.makeText(requireContext(), "위치 조회 실패: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addMarker(latLng: LatLng, title: String) {
        val markerOptions = MarkerOptions().position(latLng).title(title)
        val bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        markerOptions.icon(bitmapDescriptor)
        googleMap?.addMarker(markerOptions)
    }

    private fun moveCamera(latLng: LatLng) {
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
    }

    private fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = ""
        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses?.get(0)
                    if (address != null) {
                        addressText = address.getAddressLine(0)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "주소 변환에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
        return addressText
    }

    private fun saveLocationAndDisableButton() {
        val currentLocation = LocDataClass(
            latitude = googleMap?.cameraPosition?.target?.latitude ?: 0.0,
            longitude = googleMap?.cameraPosition?.target?.longitude ?: 0.0,
            address = binding.textLocSetNow.text.toString()
        )
        val userId = userViewModel.userIdx
        Log.d("LocSetFragment", "Saving location for user ID: $userId")  // 로그 추가

        if (userId.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "사용자 ID가 설정되지 않았습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        // ViewModel의 LiveData를 관찰
        lifecycleScope.launch {
            locViewModel.saveLocation(userId, currentLocation)
        }
    }

    private fun disableFinishButton() {
        binding.btnLocSetFinish.isEnabled = false
        binding.btnLocSetFinish.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.gray))
    }

    private fun getUserIdFromSomeSource(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val DEFAULT_ZOOM = 15f
    }
}


class UserViewModel : ViewModel() {
    // 사용자 ID를 저장하는 변수
    var userIdx: String? = null
        private set

    fun setUserId(userId: String) {
        userIdx = userId
        Log.d("UserViewModel", "User ID set to: $userId")
    }
}
