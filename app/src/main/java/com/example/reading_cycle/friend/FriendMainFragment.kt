package com.example.reading_cycle.friend

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.friend.repository.FriendRepository
import com.example.reading_cycle.friend.vm.FriendViewModel
import com.example.reading_cycle.friend.vm.FriendViewModelFactory
import com.example.reading_cycle.library.LibraryMainFragment

class FriendMainFragment : Fragment() {

    private lateinit var fragmentFriendMainBinding: FragmentFriendMainBinding
    private lateinit var friendViewModel: FriendViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentFriendMainBinding = FragmentFriendMainBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()

        val repository = FriendRepository()
        val viewModelFactory = FriendViewModelFactory(repository)
        friendViewModel = ViewModelProvider(this, viewModelFactory).get(FriendViewModel::class.java)

        setupRecyclerView()

        val userIdx = (activity as MainActivity).userViewModel.userIdx
        Log.d("FriendMainFragment", "User Index: $userIdx")

        if (userIdx != null) {
            friendViewModel.getFriendsWithUserInfo(userIdx)
            friendViewModel.friendsList.observe(viewLifecycleOwner) { friendList ->
                val adapter = FriendAdapter(requireContext(), friendList) { friendUserIdx ->
                    // 클릭 시 사용자 라이브러리로 이동
                    val bundle = Bundle().apply {
                        putString("userId", friendUserIdx)
                    }
                    mainActivity.replaceFragment(
                        MainActivity.LIBRARY_MAIN_FRAGMENT,
                        true,
                        bundle
                    )
                }
                fragmentFriendMainBinding.recyclerViewFriend.adapter = adapter
            }
        } else {
            Log.e("FriendMainFragment", "User Index is null. Cannot fetch friends list.")
        }

        return fragmentFriendMainBinding.root
    }

    private fun setupRecyclerView() {
        val layoutManager = LinearLayoutManager(requireContext())
        fragmentFriendMainBinding.recyclerViewFriend.layoutManager = layoutManager
    }
}
