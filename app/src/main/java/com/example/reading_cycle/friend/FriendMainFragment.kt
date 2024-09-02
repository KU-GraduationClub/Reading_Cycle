package com.example.reading_cycle.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.UserViewModel
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.friend.vm.FriendViewModel
import com.example.reading_cycle.friend.vm.FriendViewModelFactory
import com.example.reading_cycle.friend.repository.FriendRepository

class FriendMainFragment : Fragment() {

    private lateinit var binding: FragmentFriendMainBinding
    private lateinit var viewModel: FriendViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendMainBinding.inflate(inflater, container, false)

        // UserViewModel을 초기화합니다.
        userViewModel = ViewModelProvider(requireActivity()).get(UserViewModel::class.java)

        // Bundle로부터 userIdx를 가져옵니다.
        val userIdx = userViewModel.userIdx ?: return binding.root

        // ViewModel과 Repository를 초기화합니다.
        val repository = FriendRepository()
        val viewModelFactory = FriendViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FriendViewModel::class.java)

        setupRecyclerView()
        observeFollowingUsers()

        // userIdx를 사용하여 팔로잉 목록을 요청합니다.
        viewModel.fetchFollowingUsers(userIdx)

        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewFriends.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeFollowingUsers() {
        viewModel.followingUsers.observe(viewLifecycleOwner) { friends ->
            val adapter = FriendListAdapter(friends)
            binding.recyclerViewFriends.adapter = adapter
        }
    }
}
