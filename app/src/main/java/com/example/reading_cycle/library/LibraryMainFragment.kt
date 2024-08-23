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
import kotlinx.coroutines.launch
import com.example.reading_cycle.login.model.LoginDataClass

class LibraryMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentLibraryMainBinding: FragmentLibraryMainBinding
    private lateinit var libraryRepository: LibraryRepository

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentLibraryMainBinding = FragmentLibraryMainBinding.inflate(inflater, container, false)
        libraryRepository = LibraryRepository() // Repository 초기화

        // ViewModel에서 userIdx 가져오기
        val userIdx = mainActivity.userViewModel.userIdx
        Log.d("LibraryMainFragment", "User Index: $userIdx")

        setupToolbar()
        setupRecyclerView()

        // 사용자 데이터와 이미지 로딩
        userIdx?.let {
            lifecycleScope.launch {
                fetchUserData(it)
                fetchUserLibraryImages(it)
            }
        }

        return fragmentLibraryMainBinding.root
    }

    private fun setupToolbar() {
        // 타이틀 아이콘 작업
        ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)?.let {
            fragmentLibraryMainBinding.toolbarLibraryMainTitle.setCompoundDrawablesWithIntrinsicBounds(
                null, null, it, null
            )
        }
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.compoundDrawablePadding =
            resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.text = "라이브러리"

        // 툴바 알림 메뉴 클릭 이벤트 처리
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
        // RecyclerView에 사용될 GridLayoutManager 설정
        val layoutManager = GridLayoutManager(requireContext(), 3)
        fragmentLibraryMainBinding.recyclerViewLibraryMain.layoutManager = layoutManager
    }

    private suspend fun fetchUserData(userIdx: String) {
        val userData = libraryRepository.getUserData(userIdx)

        // 사용자 프로필 이미지와 닉네임을 UI에 설정
        userData?.let { data ->
            Glide.with(this)
                .load(data.userProfileImage)
                .into(fragmentLibraryMainBinding.imgLibraryMainProfile)

            fragmentLibraryMainBinding.textLibraryMainUser.text = data.userNickname

            // 로그인된 사용자와 라이브러리 주인 사용자 비교하여 버튼 숨기기
            if (userIdx == mainActivity.userViewModel.userIdx) {
                fragmentLibraryMainBinding.btnLibAdd.visibility = View.GONE
                fragmentLibraryMainBinding.btnLibChat.visibility = View.GONE
            }
        }
    }

    private suspend fun fetchUserLibraryImages(userIdx: String) {
        val imageUrlToDocumentIdMap = libraryRepository.getUserLibraryImages(userIdx)

        // Adapter 설정
        val adapter = LibraryMainAdapter(requireContext(), imageUrlToDocumentIdMap) { documentId ->
            // 클릭 시 처리할 작업
            Toast.makeText(requireContext(), "Navigating to post: $documentId", Toast.LENGTH_SHORT).show()
            mainActivity.navigateToSalePostFragment(documentId)
        }
        fragmentLibraryMainBinding.recyclerViewLibraryMain.adapter = adapter
    }
}
