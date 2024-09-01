package com.example.reading_cycle.post

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.health.connect.datatypes.ExerciseRoute
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.UserViewModel
import com.example.reading_cycle.databinding.FragmentPostMainBinding
import com.example.reading_cycle.post.model.PostMainAdapter
import com.example.reading_cycle.post.repository.PostMainRepository
import com.example.reading_cycle.post.vm.PostMainViewModel
import com.example.reading_cycle.post.vm.PostMainViewModelFactory
import com.example.reading_cycle.post.vm.PostSheetViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.GeoPoint
import kotlinx.coroutines.launch
import java.util.jar.Manifest


class PostMainFragment : Fragment(), PostMainAdapter.OnPostItemClickListener {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentPostMainBinding: FragmentPostMainBinding
    private lateinit var postMainAdapter: PostMainAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var bottomSheetViewModel: PostSheetViewModel
    private lateinit var postMainViewModel: PostMainViewModel
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val userViewModel: UserViewModel by activityViewModels()

    // 권한 요청 결과 처리
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getLastLocationAndLoadPosts()
        } else {
            // 권한이 거부된 경우 처리 (예: 사용자에게 알림)
            Log.e("PostMainFragment", "Location permission denied")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentPostMainBinding = FragmentPostMainBinding.inflate(inflater)
        mainActivity.showBottomNavigation()

        // BottomSheetBehavior 초기화
        bottomSheetBehavior = BottomSheetBehavior.from(fragmentPostMainBinding.bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        // BottomSheetViewModel 초기화
        bottomSheetViewModel = ViewModelProvider(this)[PostSheetViewModel::class.java]

        // FusedLocationProviderClient 초기화
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        // UserIdx 확인 및 로그인 화면으로 이동
        val userIdx = userViewModel.userIdx
        if (userIdx == null) {
            navigateToLogin()
            return null
        }

        // ViewModel 초기화
        val postMainRepository = PostMainRepository()
        val viewModelFactory = PostMainViewModelFactory(postMainRepository)
        postMainViewModel = ViewModelProvider(this, viewModelFactory)[PostMainViewModel::class.java]

        // 어댑터 초기화
        postMainAdapter = PostMainAdapter(userViewModel, this)

        // RecyclerView 설정
        fragmentPostMainBinding.recyclerViewPostMain.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postMainAdapter
        }

        // LiveData 관찰
        postMainViewModel.combinedPosts.observe(viewLifecycleOwner, Observer { combinedPosts ->
            val (salePosts, swapPosts) = combinedPosts
            postMainAdapter.submitList(salePosts, swapPosts)
        })

        // 툴바 알림 메뉴 클릭 이벤트 처리
        fragmentPostMainBinding.toolbarLayoutPostMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.postMenuItemNotify -> {
                    mainActivity.navigateToNotifyFragment()
                    true
                }

                else -> false
            }
        }

        // 위치 설정 버튼 클릭 이벤트 처리
        fragmentPostMainBinding.conPostMainLocation.setOnClickListener {
            mainActivity.navigateToLocSetFragment()
        }

        // 필터 클릭 리스너 설정
        fragmentPostMainBinding.conPostMainFilter.setOnClickListener {
            bottomSheetViewModel.toggleBottomSheet()
        }

        // 바텀시트 상태 관찰
        bottomSheetViewModel.bottomSheetExpanded.observe(viewLifecycleOwner) { expanded ->
            if (expanded) {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            }
        }

        // 이미지 버튼 클릭 이벤트 처리
        fragmentPostMainBinding.imgBtnPostMain.setOnClickListener {
            showPostTypeDialog()
        }

        // 위치 기반 데이터 로드
        checkLocationPermissionAndLoadPosts()

        return fragmentPostMainBinding.root
    }

    override fun onResume() {
        super.onResume()
        // 위치가 변경되었을 수 있으므로 데이터를 갱신
        checkLocationPermissionAndLoadPosts()

        // 정렬 팝업 메뉴
        fragmentPostMainBinding.conPostMainSort.setOnClickListener {
            showPopupMenu(it)
        }
    }

    private fun checkLocationPermissionAndLoadPosts() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 이미 부여됨
                getLastLocationAndLoadPosts()
            }

            shouldShowRequestPermissionRationale(  android.Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // 권한 설명을 보여준 후 다시 요청
                AlertDialog.Builder(requireContext())
                    .setTitle("위치 권한 필요")
                    .setMessage("게시글을 표시하기 위해 위치 권한이 필요합니다.")
                    .setPositiveButton("허용") { _, _ ->
                        requestPermissionLauncher.launch(  android.Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    .setNegativeButton("거부") { dialog, _ ->
                        dialog.dismiss()
                        Log.e("PostMainFragment", "Location permission denied")
                    }
                    .create()
                    .show()
            }

            else -> {
                // 권한 요청
                requestPermissionLauncher.launch(  android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocationAndLoadPosts() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    val radiusInKm = 10.0 // 원하는 반경 설정 (예: 10km)
                    Log.d("PostMainFragment", "Location found: $geoPoint")
                    postMainViewModel.loadNearbyPosts(geoPoint, radiusInKm)
                } else {
                    // 위치 정보가 null인 경우 처리
                    Log.e("PostMainFragment", "Unable to obtain location.")
                }
            }
            .addOnFailureListener {
                // 위치 획득 실패 처리
                Log.e("PostMainFragment", "Failed to get location:")
            }
    }

    private fun showPopupMenu(view: View) {
        val popup = PopupMenu(requireContext(), view)
        val inflater = popup.menuInflater
        inflater.inflate(R.menu.popup_menu_post_main, popup.menu)

        // 팝업 메뉴 아이템 클릭 이벤트 처리
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menuItemSortByRecent -> {
                    postMainViewModel.sortPostsByRecent()
                    updateSortText("최신 순")
                    true
                }

//                R.id.menuItemSortByDistance -> {
//                    // 거리순 정렬
//                    getLastLocationAndLoadPosts()
//                    updateSortText("거리 순")
//                    true
//                }

                R.id.menuItemSortBySwap -> {
                    // 교환 게시글 필터링
                    viewLifecycleOwner.lifecycleScope.launch {
                        postMainViewModel.filterPostsByType(isSwap = true)
                    }
                    updateSortText("교환 옵션")
                    true
                }

                R.id.menuItemSortBySale -> {
                    // 판매 게시글 필터링
                    viewLifecycleOwner.lifecycleScope.launch {
                        postMainViewModel.filterPostsByType(isSwap = false)
                    }
                    updateSortText("판매 옵션")
                    true
                }

                else -> false
            }
        }
        // 팝업 메뉴 보이기
        popup.show()
    }

    private fun updateSortText(sortText: String) {
        // 정렬 텍스트 업데이트
        fragmentPostMainBinding.textPostMainSort.text = sortText
    }

    private fun showPostTypeDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("게시글 유형을 선택해 주세요")

        val postTypes = arrayOf("교환 게시글", "판매 게시글")

        builder.setItems(postTypes) { _, which ->
            // 사용자가 선택한 항목에 따라 해당 프래그먼트로 이동하는 로직 추가
            when (which) {
                0 -> (requireActivity() as MainActivity).replaceFragment(
                    MainActivity.ADD_SWAP_POST_FRAGMENT,
                    true
                )

                1 -> (requireActivity() as MainActivity).replaceFragment(
                    MainActivity.ADD_SALE_POST_FRAGMENT,
                    true
                )
            }
        }
        val dialog = builder.create()
        dialog.show()
    }

    // RecyclerView 아이템 클릭 처리
    override fun onSwapItemClick(document: DocumentSnapshot) {
        val documentId = document.id
        mainActivity.navigateToSwapPostFragment(documentId)
    }

    override fun onSaleItemClick(document: DocumentSnapshot) {
        val documentId = document.id
        mainActivity.navigateToSalePostFragment(documentId)
    }

    // UserIdx 미전달 시 초기화면으로 이동
    private fun navigateToLogin() {
        val fragmentManager = requireActivity().supportFragmentManager
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        // 로그인 프래그먼트로 이동
        (activity as MainActivity).replaceFragment(
            MainActivity.LOGIN_MAIN_FRAGMENT,
            false
        )
    }
}