package com.example.reading_cycle.friend


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.R
import com.example.reading_cycle.databinding.FragmentFriendMainBinding
import com.example.reading_cycle.friend.model.FriendDataClass
import com.example.reading_cycle.friend.model.FriendMainAdapter


class FriendMainFragment : Fragment() {

    private lateinit var fragmentFriendMainBinding: FragmentFriendMainBinding
    private lateinit var friendMainAdapter: FriendMainAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentFriendMainBinding = FragmentFriendMainBinding.inflate(inflater)
        // 기존 코드에서 rowFriendItemLayoutBinding을 사용하는 부분을 삭제합니다.

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
        val friendList = listOf(
            FriendDataClass("닉네임1"),
            FriendDataClass("닉네임2"),
            FriendDataClass("닉네임3"),
            FriendDataClass("닉네임4"),
            FriendDataClass("닉네임5"),
        )

        // 어댑터 설정
        friendMainAdapter = FriendMainAdapter(friendList)
        fragmentFriendMainBinding.recyclerViewFriendMain.adapter = friendMainAdapter

        // 이미지 버튼에 클릭 이벤트 핸들러 설정
        fragmentFriendMainBinding.imgBtnFriendMain.setOnClickListener {
            showBookTypeMenu(it)
        }

        return fragmentFriendMainBinding.root

    }

    private fun showBookTypeMenu(view: View) {
        val popupMenu = PopupMenu(requireContext(), view)
        popupMenu.menuInflater.inflate(R.menu.popup_menu_friend_main, popupMenu.menu)

        // 팝업 메뉴 아이템 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener { menuItem ->
            // 각 메뉴 아이템에 대한 처리 추가
            when (menuItem.itemId) {
                R.id.menuItemSortBybookmark -> {
                    // "즐겨 찾기" 선택 시 처리 상단 고정 처리

                }
                R.id.menuItemSortByelimination -> {
                    // "삭제" 선택 시 처리


                }

            }
            true
        }
        // 팝업 메뉴 표시
        popupMenu.show()
    }

} 