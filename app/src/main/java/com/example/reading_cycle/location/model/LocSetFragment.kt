package com.example.reading_cycle.location.model

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.reading_cycle.LocDataClass
import com.example.reading_cycle.databinding.FragmentLocSetBinding
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
import java.io.IOException
import java.util.*

class LocSetFragment : Fragment(), OnMapReadyCallback {

    private val DEFAULT_ZOOM: Float = 0.0f
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var _binding: FragmentLocSetBinding? = null
    private val binding get() = _binding!!
    private var googleMap: GoogleMap? = null
    private val locViewModel: LocViewModel by activityViewModels()

    private lateinit var locRepository: LocRepository

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
        _binding = FragmentLocSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // LocRepository 초기화
        locRepository = LocRepository(requireContext())

        // SupportMapFragment 가져오기 및 지도 준비 완료 시 콜백 받기
        val mapFragment =
            childFragmentManager.findFragmentById(com.example.reading_cycle.R.id.mapViewLocSet) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // 버튼 클릭 리스너 설정
        binding.nowlocationLocSet.setOnClickListener {
            requestLocationPermission()
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

        // 줌 컨트롤 활성화
        googleMap.uiSettings.isZoomControlsEnabled = true

        // 위치 권한 확인 및 My Location 버튼 활성화
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

        // 지도 유형 설정 (일반 지도)
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        // 현재 위치 가져오기
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

                // 마커 추가
                addMarker(currentLatLng, "현재 위치")

                // 카메라 이동
                moveCamera(currentLatLng)

                // LatLng를 주소로 변환
                val address = getAddressFromLatLng(requireContext(), currentLatLng)
                binding.textLocSetNow.text = address

                // 현재 위치 정보를 LocDataClass로 저장
                val currentLocation = LocDataClass(location.latitude, location.longitude)
                saveLocation(currentLocation)
            } else {
                Toast.makeText(requireContext(), "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addMarker(latLng: LatLng, title: String) {
        val markerOptions = MarkerOptions().position(latLng).title(title)

        // 마커 아이콘 설정 (옵션)
        val bitmapDescriptor =
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
        markerOptions.icon(bitmapDescriptor)

        googleMap?.addMarker(markerOptions)
    }

    private fun moveCamera(latLng: LatLng) {
        // 카메라 이동
        googleMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
    }

    private fun animateCamera(latLng: LatLng) {
        // 카메라 이동 + 애니메이션
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSION -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = ""

        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses!!.isNotEmpty()) {
                val address = addresses[0]
                addressText = address.getAddressLine(0) // 여기서 주소를 가져오는 부분을 변경하면 됩니다.
            }
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "주소 변환에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }

        return addressText
    }

    private fun saveLocation(location: LocDataClass) {
        // 위치 정보를 ViewModel에 저장
        locViewModel.currentLocation = location

        // 위치 정보를 LocRepository를 통해 Firebase 데이터베이스에 저장
        locRepository.saveLocation(location, {
            // 성공 콜백
        }, {
            // 실패 콜백
        })
    }

    companion object {
        private const val REQUEST_LOCATION_PERMISSION = 1
    }
}
