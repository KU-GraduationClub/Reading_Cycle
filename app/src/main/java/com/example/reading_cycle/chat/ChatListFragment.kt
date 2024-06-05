package com.example.reading_cycle.chat

import ChatListAdapter
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.databinding.FragmentChatListBinding
import com.example.reading_cycle.R


class ChatListFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentChatListBinding : FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter //채팅리스트 어댑터

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentChatListBinding = FragmentChatListBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()

        val ChatItems = listOf(
            ChatItem(R.drawable.baseline_account_circle_24, "김민재", "안녕하세요!", "12:30 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "손흥민", "데미안 구매하고 싶습니다", "1:45 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "이강인", "15000원에 거래합니다.", "3:20 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "박지성", "안녕하세요~", "12:30 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "이승우", "사거리 앞에 있습니다.", "1:45 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "차두리", "네 감사합니다", "3:20 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "차범근", "안녕하세요!", "12:30 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "이청용", "3000원 네고 가능한가요?", "1:45 PM"),
            ChatItem(R.drawable.baseline_account_circle_24, "기성룡", "안녕하세요", "3:20 PM"),
            // ... 다른 채팅 아이템들을 추가할 수 있습니다.
        )
        /*lateinit var mainActivity: MainActivity
        mainActivity = activity as MainActivity
        fragmentChatListBinding.run {
            toolbarAuthJoin.setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.AUTH_JOIN_FRAGMENT)
            }*/
        initRecyclerView(ChatItems)

        // 툴바 알림 메뉴 클릭 이벤트 처리
        fragmentChatListBinding.toolbarLayoutChatList.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.libraryMenuItemNotify -> {
                    mainActivity.navigateToNotifyFragment()
                    true
                }
                else -> false
            }
        }

        return fragmentChatListBinding.root
    }
    private fun initRecyclerView(ChatItems: List<ChatItem>) {
        // LinearLayoutManager를 거꾸로 설정하여 최신 데이터가 위에 위치하도록 함
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.reverseLayout = true
        fragmentChatListBinding.recyclerChatList.layoutManager = layoutManager

        // ItemDecoration을 사용하여 아이템 간격을 0으로 설정
        val itemDecoration = object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.set(0, 0, 0, 0)
            }
        }
        // 어댑터 초기화 및 RecyclerView에 설정
        chatListAdapter = ChatListAdapter(ChatItems)
        fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter

        chatListAdapter.notifyDataSetChanged()    // 데이터 변경을 어댑터에 알림


    }
}