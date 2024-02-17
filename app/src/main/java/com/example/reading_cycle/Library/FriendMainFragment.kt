package com.example.reading_cycle.Library

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapp.friends.model.FriendDateClass
import com.example.myapp.friends.model.FriendMainAdapter
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
        val iconDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.baseline_sync_40)
        fragmentFriendMainBinding.toolbarTitle.setCompoundDrawablesWithIntrinsicBounds(
            null,
            null,
            null,
            null
        )
        fragmentFriendMainBinding.toolbarTitle.compoundDrawablePadding = resources.getDimensionPixelSize(R.dimen.icon_text_padding)
        // 텍스트 설정
        fragmentFriendMainBinding.toolbarTitle.text = "프렌드"

        //데이터 생성(임시)
        val friendList = listOf(
            FriendDateClass("닉네임1"),
            FriendDateClass("닉네임2"),
            FriendDateClass("닉네임3"),
            FriendDateClass("닉네임4"),
            FriendDateClass("닉네임5"),
        )

        //어댑터 초기화
        friendMainAdapter = FriendMainAdapter(friendList)

        //RecyclerView 설정
        val layoutManager = LinearLayoutManager(requireContext())
        fragmentFriendMainBinding.recyclerViewFriendMain.layoutManager = layoutManager

        // 어댑터 설정
        fragmentFriendMainBinding.recyclerViewFriendMain.adapter = friendMainAdapter

        return fragmentFriendMainBinding.root
    }
}
