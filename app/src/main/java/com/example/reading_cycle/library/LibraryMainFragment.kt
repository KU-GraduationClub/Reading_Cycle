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
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.library.repository.LibraryRepository
import com.example.reading_cycle.library.vm.LibraryViewModel
import com.example.reading_cycle.library.vm.LibraryViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LibraryMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentLibraryMainBinding: FragmentLibraryMainBinding
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var repository: LibraryRepository // 추가된 부분

    private var userId: String? = null
    private var isFollowing: Boolean = false
    private val handler = android.os.Handler()

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
        repository = LibraryRepository() // 초기화
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
                libraryViewModel.fetchUserLibraryImages(it)
                fetchUserData(it)
                fetchUserPostCount(it)
                checkIfFollowing(it) // 팔로우 상태 확인
            }
        }

        // Observe changes in LiveData from ViewModel
        libraryViewModel.images.observe(viewLifecycleOwner) { imageUrlToDocumentIdMap ->
            val adapter = LibraryMainAdapter(requireContext(), imageUrlToDocumentIdMap) { documentId ->
                mainActivity.navigateToSalePostFragment(documentId)
            }
            fragmentLibraryMainBinding.recyclerViewLibraryMain.adapter = adapter
        }

        fragmentLibraryMainBinding.btnLibAdd.setOnClickListener {
            handleFollowButtonClick()
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
        val userData = repository.getUserData(userIdx) // 수정된 부분

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
        val postCount = repository.getUserPostCount(userIdx) // 수정된 부분
        fragmentLibraryMainBinding.textLibraryMainPostCount.text = postCount.toString()
    }

    private suspend fun checkIfFollowing(targetUserIdx: String) {
        val currentUserIdx = mainActivity.userViewModel.userIdx ?: return
        val friendsCollection = FirebaseFirestore.getInstance()
            .collection("Users")
            .document(currentUserIdx)
            .collection("Friends")
            .document(targetUserIdx)
            .get()
            .await()

        if (friendsCollection.exists()) {
            isFollowing = friendsCollection.getBoolean("IsFollowing") ?: false
            updateFollowButtonState(isFollowing)
        } else {
            // 사용자가 친구 목록에 없는 경우 기본 상태 설정
            isFollowing = false
            updateFollowButtonState(isFollowing)
        }
    }

    private fun updateFollowButtonState(isFollowing: Boolean) {
        if (isFollowing) {
            fragmentLibraryMainBinding.btnLibAdd.apply {
                text = "팔로잉"
                setTextColor(ContextCompat.getColor(requireContext(), R.color.theme_green))
                backgroundTintList = null // backgroundTint 제거
            }
        } else {
            fragmentLibraryMainBinding.btnLibAdd.apply {
                text = "팔로우"
                setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                backgroundTintList = ContextCompat.getColorStateList(requireContext(), R.color.theme_green)
            }
        }
    }

    private fun handleFollowButtonClick() {
        val targetUserIdx = userId ?: return
        val currentUserIdx = mainActivity.userViewModel.userIdx ?: return

        lifecycleScope.launch {
            if (isFollowing) {
                // 언팔로우 처리
                libraryViewModel.removeFriend(currentUserIdx, targetUserIdx)
                isFollowing = false
                updateFollowButtonState(isFollowing)

                // 10초 후 팔로잉 리스트에서 제거
                handler.postDelayed({
                    libraryViewModel.getFollowingList(currentUserIdx)
                    val friendsList = libraryViewModel.followingList.value ?: emptyList()
                    val friendToRemove = friendsList.find { friend ->
                        friend.userIdx == targetUserIdx
                    }
                    friendToRemove?.let {
                        libraryViewModel.removeFriend(currentUserIdx, targetUserIdx)
                    }
                }, 10000)
            } else {
                // 팔로우 처리
                val friendData = FriendDataClass(userIdx = targetUserIdx, IsFollowing = true)
                val firestore = FirebaseFirestore.getInstance()
                firestore.collection("Users")
                    .document(currentUserIdx)
                    .collection("Friends")
                    .document(targetUserIdx)
                    .set(friendData)
                    .await()
                isFollowing = true
                updateFollowButtonState(isFollowing)
            }
        }
    }
}
