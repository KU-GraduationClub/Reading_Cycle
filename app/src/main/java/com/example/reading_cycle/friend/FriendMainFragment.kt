package com.example.reading_cycle.friend

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.friend.vm.FriendViewModel
import com.example.reading_cycle.friend.vm.FriendViewModelFactory
import com.example.reading_cycle.friend.repository.FriendRepository
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.UserViewModel

class FriendMainFragment : Fragment() {

    private var mainActivity: MainActivity? = null
    private lateinit var binding: FragmentFriendMainBinding
    private lateinit var viewModel: FriendViewModel
    private lateinit var userViewModel: UserViewModel

    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Activity가 MainActivity인지 확인하고, 참조를 저장합니다.
        if (context is MainActivity) {
            mainActivity = context
        } else {
            throw RuntimeException("$context must be MainActivity")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFriendMainBinding.inflate(inflater, container, false)

        // 바텀 네비게이션을 보여줍니다.
        mainActivity?.showBottomNavigation()

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
            val adapter = FriendListAdapter(requireContext(), friends) { userIdx ->
                // 프로필 이미지 클릭 시 해당 사용자의 라이브러리 화면으로 이동합니다.
                mainActivity?.navigateToLibraryFragment(userIdx)
            }
            binding.recyclerViewFriends.adapter = adapter
        }

        viewModel.followingCount.observe(viewLifecycleOwner) { count ->
            // 팔로잉 수를 TextView에 설정합니다.
            binding.textFriendMainCount.text = count.toString()
        }
    }

    override fun onDetach() {
        super.onDetach()
        // Fragment가 detach될 때 mainActivity를 null로 설정하여 메모리 누수를 방지합니다.
        mainActivity = null
    }
}
