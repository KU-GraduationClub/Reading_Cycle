package com.example.reading_cycle.library

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentLibraryMainBinding
import com.example.reading_cycle.library.repository.LibraryRepository
import com.example.reading_cycle.library.vm.LibraryViewModelFactory
import com.example.reading_cycle.library.vm.LibraryViewModel
import kotlinx.coroutines.launch

class LibraryMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentLibraryMainBinding: FragmentLibraryMainBinding
    private lateinit var libraryViewModel: LibraryViewModel

    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentLibraryMainBinding = FragmentLibraryMainBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNavigation()

        fragmentLibraryMainBinding.toolbarLayoutLibraryMain.apply {
            setNavigationIcon(R.drawable.baseline_arrow_back_ios_28) // 아이콘 설정
            setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.LIBRARY_MAIN_FRAGMENT) // 뒤로 가기 동작
            }
        }

        // ViewModelFactory를 통해 ViewModel 인스턴스 생성
        val repository = LibraryRepository()
        val viewModelFactory = LibraryViewModelFactory(repository)
        libraryViewModel = ViewModelProvider(this, viewModelFactory).get(LibraryViewModel::class.java)

        setupToolbar()
        setupRecyclerView()

        // Fragment 호출 시 Bundle로 전달된 userId를 가져옴
        userId = arguments?.getString("userId")

        // userId가 null일 경우, 현재 로그인한 사용자의 userIdx 사용
        if (userId == null) {
            userId = mainActivity.userViewModel.userIdx
        }

        Log.d("LibraryMainFragment", "User Index: $userId")

        userId?.let {
            lifecycleScope.launch {
                // ViewModel을 통해 이미지 데이터 로드
                libraryViewModel.fetchUserLibraryImages(it)
                fetchUserData(it)
                fetchUserPostCount(it)
            }
        }

        // Observe changes in LiveData from ViewModel
        libraryViewModel.images.observe(viewLifecycleOwner) { imageUrlToDocumentIdMap ->
            val adapter = LibraryMainAdapter(requireContext(), imageUrlToDocumentIdMap) { documentId ->
                Toast.makeText(requireContext(), "Navigating to post: $documentId", Toast.LENGTH_SHORT).show()
                mainActivity.navigateToSalePostFragment(documentId)
            }
            fragmentLibraryMainBinding.recyclerViewLibraryMain.adapter = adapter
        }

        return fragmentLibraryMainBinding.root
    }

    private fun setupToolbar() {
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.compoundDrawablePadding =
            resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.text = "라이브러리"

        fragmentLibraryMainBinding.toolbarLayoutLibraryMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.libraryMenuItemNotify -> {
                    mainActivity.navigateToNotifyFragment()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        val layoutManager = GridLayoutManager(requireContext(), 3)
        fragmentLibraryMainBinding.recyclerViewLibraryMain.layoutManager = layoutManager
    }

    private suspend fun fetchUserData(userIdx: String) {
        val userData = LibraryRepository().getUserData(userIdx)

        userData?.let { data ->
            Glide.with(this)
                .load(data.userProfileImage)
                .circleCrop()
                .into(fragmentLibraryMainBinding.imgLibraryMainProfile)

            fragmentLibraryMainBinding.textLibraryMainUser.text = data.userNickname

            // 로그인된 사용자와 라이브러리 주인 사용자 비교하여 버튼 숨기기
            if (userIdx == mainActivity.userViewModel.userIdx) {
                fragmentLibraryMainBinding.btnLibAdd.visibility = View.GONE
                fragmentLibraryMainBinding.btnLibAdd.isEnabled = false
            } else {
                fragmentLibraryMainBinding.btnLibAdd.visibility = View.VISIBLE
                fragmentLibraryMainBinding.btnLibAdd.isEnabled = true
            }
        }
    }

    private suspend fun fetchUserPostCount(userIdx: String) {
        val postCount = LibraryRepository().getUserPostCount(userIdx)
        fragmentLibraryMainBinding.textLibraryMainPostCount.text = postCount.toString()
    }
}
