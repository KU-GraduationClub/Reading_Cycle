package com.example.reading_cycle.library

import android.os.Bundle
import android.util.Log
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
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.library.repository.LibraryRepository
import com.example.reading_cycle.library.vm.LibraryViewModel
import com.example.reading_cycle.library.vm.LibraryViewModelFactory
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

        setupToolbar()
        setupRecyclerView()

        repository = LibraryRepository()
        val viewModelFactory = LibraryViewModelFactory(repository)
        libraryViewModel = ViewModelProvider(this, viewModelFactory).get(LibraryViewModel::class.java)

        userId = arguments?.getString("userId") ?: mainActivity.userViewModel.userIdx

        Log.d("LibraryMainFragment", "User Index: $userId")

        userId?.let {
            lifecycleScope.launch {
                libraryViewModel.fetchUserLibraryImages(it)
                fetchUserData(it)
                fetchUserPostCount(it)
                checkIfFollowing(it)
            }
        }

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
        fragmentLibraryMainBinding.toolbarLayoutLibraryMain.apply {
            setNavigationIcon(R.drawable.baseline_arrow_back_ios_28)
            setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.LIBRARY_MAIN_FRAGMENT)
            }
        }
    }

    private fun setupRecyclerView() {
        fragmentLibraryMainBinding.recyclerViewLibraryMain.layoutManager = GridLayoutManager(requireContext(), 3)
    }

    private suspend fun fetchUserData(userIdx: String) {
        val userData = repository.getUserData(userIdx)
        userData?.let { data ->
            Glide.with(this)
                .load(data.userProfileImage)
                .circleCrop()
                .into(fragmentLibraryMainBinding.imgLibraryMainProfile)

            fragmentLibraryMainBinding.textLibraryMainUser.text = data.userNickname

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
        val postCount = repository.getUserPostCount(userIdx)
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
            isFollowing = false
            updateFollowButtonState(isFollowing)
        }
    }

    private fun updateFollowButtonState(isFollowing: Boolean) {
        fragmentLibraryMainBinding.btnLibAdd.apply {
            text = if (isFollowing) "팔로잉" else "팔로우"
            setTextColor(ContextCompat.getColor(requireContext(), if (isFollowing) R.color.theme_green else R.color.white))
            backgroundTintList = if (isFollowing) null else ContextCompat.getColorStateList(requireContext(), R.color.theme_green)
        }
    }

    private fun handleFollowButtonClick() {
        val targetUserIdx = userId ?: return
        val currentUserIdx = mainActivity.userViewModel.userIdx ?: return

        lifecycleScope.launch {
            if (isFollowing) {
                libraryViewModel.removeFriend(currentUserIdx, targetUserIdx)
                isFollowing = false
                updateFollowButtonState(isFollowing)
            } else {
                val friendData = FriendDataClass(userIdx = targetUserIdx, IsFollowing = true)
                libraryViewModel.addFriend(currentUserIdx, friendData)
                isFollowing = true
                updateFollowButtonState(isFollowing)
            }
        }
    }
}
