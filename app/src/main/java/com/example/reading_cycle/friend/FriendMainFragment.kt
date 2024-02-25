package com.example.reading_cycle.friend


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFirendMainBinding
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.friend.model.FriendMainAdapter


class FriendMainFragment : Fragment() {

    private lateinit var fragmentFriendMainBinding: FragmentFirendMainBinding
    private lateinit var friendMainAdapter: FriendMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFriendMainBinding = FragmentFirendMainBinding.inflate(inflater)

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


        //데이터 생성(임시)
        val friendList = listOf(
            FriendDataClass("닉네임1"),
            FriendDataClass("닉네임2"),
            FriendDataClass("닉네임3"),
            FriendDataClass("닉네임4"),
            FriendDataClass("닉네임5"),
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








