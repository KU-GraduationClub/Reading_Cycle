package com.example.reading_cycle.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.friend.vm.FriendViewModel
import com.example.reading_cycle.friend.vm.FriendViewModelFactory
import com.example.reading_cycle.friend.repository.FriendRepository

class FriendMainFragment : Fragment() {

    private lateinit var binding: FragmentFriendMainBinding
    private lateinit var viewModel: FriendViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendMainBinding.inflate(inflater, container, false)

        val repository = FriendRepository()
        val viewModelFactory = FriendViewModelFactory(repository)
        viewModel = ViewModelProvider(this, viewModelFactory).get(FriendViewModel::class.java)

        val userIdx = arguments?.getString("userIdx") ?: return binding.root

        setupRecyclerView()
        observeFollowingUsers()

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
