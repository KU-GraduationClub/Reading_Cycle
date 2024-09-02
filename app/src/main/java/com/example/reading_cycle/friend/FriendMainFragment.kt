package com.example.reading_cycle.friend

import android.os.Bundle
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

class FriendMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentFriendMainBinding: FragmentFriendMainBinding
    private lateinit var friendViewModel: FriendViewModel
    private lateinit var repository: FriendRepository

    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentFriendMainBinding = FragmentFriendMainBinding.inflate(inflater, container, false)

        // Repository와 ViewModel 초기화
        repository = FriendRepository()
        val viewModelFactory = FriendViewModelFactory(repository)
        friendViewModel = ViewModelProvider(this, viewModelFactory).get(FriendViewModel::class.java)

        // 사용자 ID 가져오기
        userId = arguments?.getString("userId") ?: mainActivity.userViewModel.userIdx

        // 사용자 ID가 null이 아닌 경우, 팔로잉 목록을 로드
        userId?.let {
            friendViewModel.fetchFollowingUsers(it)
        }

        // ViewModel의 LiveData를 관찰하여 데이터 업데이트
        friendViewModel.followingUsers.observe(viewLifecycleOwner) { users ->
            val adapter = FriendAdapter(requireContext(), users)
            fragmentFriendMainBinding.recyclerViewFriends.layoutManager = LinearLayoutManager(requireContext())
            fragmentFriendMainBinding.recyclerViewFriends.adapter = adapter
        }

        return fragmentFriendMainBinding.root
    }
}
