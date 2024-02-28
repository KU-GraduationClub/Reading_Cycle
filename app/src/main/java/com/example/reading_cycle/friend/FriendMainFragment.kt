package com.example.reading_cycle.friend.model

import FriendDataClass
import FriendMainAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFriendMainBinding

class FriendMainFragment : Fragment() {

    private lateinit var fragmentFriendMainBinding: FragmentFriendMainBinding
    private lateinit var friendMainAdapter: FriendMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFriendMainBinding = FragmentFriendMainBinding.inflate(inflater)

        // 타이틀 아이콘 작업
        fragmentFriendMainBinding.toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            null
        )
        fragmentFriendMainBinding.toolbarTitle.compoundDrawablePadding =
            resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        // 텍스트 설정
        fragmentFriendMainBinding.toolbarTitle.text = "프렌드"

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

        return fragmentFriendMainBinding.root
    }
}


