package com.example.reading_cycle.friend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.friend.model.FriendMainAdapter

class FriendMainFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentFriendMainBinding: FragmentFriendMainBinding
    private lateinit var friendMainAdapter: FriendMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentFriendMainBinding = FragmentFriendMainBinding.inflate(inflater)
        mainActivity.showBottomNavigation()

        // RecyclerView 설정
        val layoutManager = LinearLayoutManager(requireContext())
        fragmentFriendMainBinding.recyclerViewFriendMain.layoutManager = layoutManager

        // 데이터 생성(임시)
        val friendList = mutableListOf<FriendDataClass>() // MutableList로 변경

        // 데이터 추가
        friendList.add(FriendDataClass("닉네임1"))
        friendList.add(FriendDataClass("닉네임2"))
        friendList.add(FriendDataClass("닉네임3"))
        friendList.add(FriendDataClass("닉네임4"))
        friendList.add(FriendDataClass("닉네임5"))

        // 어댑터 설정
        friendMainAdapter = FriendMainAdapter(friendList)
        fragmentFriendMainBinding.recyclerViewFriendMain.adapter = friendMainAdapter

        // 툴바 알림 메뉴 클릭 이벤트 처리
        fragmentFriendMainBinding.toolbarLayoutFriendMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.friendMenuItemNotify -> {
                    mainActivity.navigateToNotifyFragment()
                    true
                }
                else -> false
            }
        }

        return fragmentFriendMainBinding.root
    }
}


