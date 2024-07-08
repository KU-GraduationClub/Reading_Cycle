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
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.databinding.FragmentChatListBinding
import com.google.android.material.R


class ChatListFragment : Fragment() {
    private lateinit var fragmentChatListBinding : FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter //채팅리스트 어댑터
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentChatListBinding = FragmentChatListBinding.inflate(inflater, container, false)

        val ChatItems = listOf(
            ChatItem(R.drawable.design_ic_visibility, "김민수", "안녕하세요!", "12:30 PM"),
            ChatItem(R.drawable.design_ic_visibility, "손흥민", "오늘 날씨 어때?", "1:45 PM"),
            ChatItem(R.drawable.design_ic_visibility, "한문철", "뭐해?", "3:20 PM"),
            ChatItem(R.drawable.design_ic_visibility, "박지성", "안녕하세요!", "12:30 PM"),
            ChatItem(R.drawable.design_ic_visibility, "이승우", "오늘 날씨 어때?", "1:45 PM"),
            ChatItem(R.drawable.design_ic_visibility, "차두리", "뭐해?", "3:20 PM"),
            ChatItem(R.drawable.design_ic_visibility, "차범근", "안녕하세요!", "12:30 PM"),
            ChatItem(R.drawable.design_ic_visibility, "이청용", "오늘 날씨 어때?", "1:45 PM"),
            ChatItem(R.drawable.design_ic_visibility, "기성룡", "뭐해?", "3:20 PM"),
            // ... 다른 채팅 아이템들을 추가할 수 있습니다.
        )
        /*lateinit var mainActivity: MainActivity
        mainActivity = activity as MainActivity
        fragmentChatListBinding.run {
            toolbarAuthJoin.setNavigationOnClickListener {
                mainActivity.removeFragment(MainActivity.AUTH_JOIN_FRAGMENT)
            }*/
        initRecyclerView(ChatItems)

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