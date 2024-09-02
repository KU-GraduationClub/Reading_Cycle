package com.example.reading_cycle.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.reading_cycle.library.vm.LibraryViewModel
import com.example.reading_cycle.library.vm.LibraryViewModelFactory
import kotlinx.coroutines.launch

class LibraryMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentLibraryMainBinding: FragmentLibraryMainBinding
    private lateinit var libraryViewModel: LibraryViewModel
    private lateinit var repository: LibraryRepository

    private var userId: String? = null
    private var isFollowing: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentLibraryMainBinding = FragmentLibraryMainBinding.inflate(inflater, container, false)
        mainActivity.hideBottomNavigation()

        repository = LibraryRepository()
        val viewModelFactory = LibraryViewModelFactory(repository)
        libraryViewModel = ViewModelProvider(this, viewModelFactory).get(LibraryViewModel::class.java)

        setupToolbar()
        setupRecyclerView()

        userId = arguments?.getString("userId") ?: mainActivity.userViewModel.userIdx

        userId?.let {
            lifecycleScope.launch {
                libraryViewModel.fetchUserLibraryImages(it)
                fetchUserData(it)
                fetchUserPostCount(it)
                libraryViewModel.checkIfFollowing(it, mainActivity.userViewModel.userIdx ?: "")
            }
        }

        libraryViewModel.images.observe(viewLifecycleOwner) { imageUrlToDocumentIdMap ->
            val adapter = LibraryMainAdapter(requireContext(), imageUrlToDocumentIdMap) { documentId ->
                mainActivity.navigateToSalePostFragment(documentId)
            }
            fragmentLibraryMainBinding.recyclerViewLibraryMain.adapter = adapter
        }

        libraryViewModel.isFollowing.observe(viewLifecycleOwner) { isFollowing ->
            this.isFollowing = isFollowing
            updateFollowButtonState()
        }

        fragmentLibraryMainBinding.btnLibAdd.setOnClickListener {
            userId?.let { userId ->
                val userNickname = fragmentLibraryMainBinding.textLibraryMainUser.text.toString()
                val userProfileImageUrl = fragmentLibraryMainBinding.imgLibraryMainProfile.tag as? String ?: ""

                libraryViewModel.toggleFollow(userId, mainActivity.userViewModel.userIdx ?: "", userNickname, userProfileImageUrl)
            }
        }

        return fragmentLibraryMainBinding.root
    }

    private fun setupToolbar() {
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.compoundDrawablePadding =
            resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        fragmentLibraryMainBinding.toolbarLibraryMainTitle.text = "라이브러리"

        fragmentLibraryMainBinding.toolbarLayoutLibraryMain.setNavigationIcon(R.drawable.baseline_arrow_back_ios_28)
        fragmentLibraryMainBinding.toolbarLayoutLibraryMain.setNavigationOnClickListener {
            mainActivity.removeFragment(MainActivity.LIBRARY_MAIN_FRAGMENT)
        }

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

    private fun fetchUserData(userIdx: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            val userData = repository.getUserData(userIdx) // UserData 객체를 가져옴
            userData?.let { data ->
                val userProfileImageUrl = data.userProfileImage
                val userNickname = data.userNickname

                // Glide를 사용하여 이미지 로드
                Glide.with(this@LibraryMainFragment)
                    .load(userProfileImageUrl)
                    .circleCrop()
                    .into(fragmentLibraryMainBinding.imgLibraryMainProfile)

                // 프로필 이미지 URL을 tag로 저장
                fragmentLibraryMainBinding.imgLibraryMainProfile.tag = userProfileImageUrl

                // 사용자 닉네임을 UI에 설정
                fragmentLibraryMainBinding.textLibraryMainUser.text = userNickname

                if (data.userIdx == mainActivity.userViewModel.userIdx) {
                    fragmentLibraryMainBinding.btnLibAdd.visibility = View.GONE
                    fragmentLibraryMainBinding.btnLibAdd.isEnabled = false
                } else {
                    fragmentLibraryMainBinding.btnLibAdd.visibility = View.VISIBLE
                    fragmentLibraryMainBinding.btnLibAdd.isEnabled = true
                }
            }
        }
    }

    private suspend fun fetchUserPostCount(userIdx: String) {
        val postCount = repository.getUserPostCount(userIdx)
        fragmentLibraryMainBinding.textLibraryMainPostCount.text = "$postCount"
    }

    private fun updateFollowButtonState() {
        val buttonText = if (isFollowing) "팔로잉" else "팔로우"
        val buttonColor = if (isFollowing) R.color.theme_green else R.color.white
        val backgroundColor = if (isFollowing) R.color.white else R.color.theme_green

        fragmentLibraryMainBinding.btnLibAdd.text = buttonText
        fragmentLibraryMainBinding.btnLibAdd.setTextColor(ContextCompat.getColor(requireContext(), buttonColor))
        fragmentLibraryMainBinding.btnLibAdd.backgroundTintList = ContextCompat.getColorStateList(requireContext(), backgroundColor)
    }
}
