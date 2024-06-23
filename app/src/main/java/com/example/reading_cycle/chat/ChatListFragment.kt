
package com.example.reading_cycle.chat.ui

import ChatListAdapter
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.chat.model.ChatRoom
import com.example.reading_cycle.databinding.FragmentChatListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentChatListBinding: FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatRoom>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentChatListBinding = FragmentChatListBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()

        val database = Firebase.database.reference.child("chatRoom")

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

        // 초기 빈 어댑터 설정
        chatListAdapter = ChatListAdapter(emptyList())
        fragmentChatListBinding.recyclerChatList.layoutManager = LinearLayoutManager(requireContext())
        fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter

        return fragmentChatListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Firebase.database.reference.child("chatRooms")

        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoomList.clear()
                for (childSnapshot in snapshot.children) {
                    val chatRoom = childSnapshot.getValue(ChatRoom::class.java)
                    if (chatRoom != null) {
                        chatRoomList.add(chatRoom)
                    } else {
                        Log.w("ChatListFragment", "Invalid ChatRoom object: $childSnapshot")
                    }
                }
                val chatItems = chatRoomList.map {
                    ChatItem(
                        profileImage = R.drawable.ic_launcher_foreground, // 기본 이미지 설정 필요시
                        name = it.name ?: "Unknown",
                        lastMessage = it.lastMessage ?: "No message",
                        lastMessageTime = it.lastMessageTime ?: "Unknown time"
                    )
                }

                Log.d("ChatListFragment", "Loaded chat items: $chatItems")
                requireActivity().runOnUiThread {
                    chatListAdapter = ChatListAdapter(chatItems)
                    fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter
                    chatListAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Firebase Database error: ${error.message}")
            }
        })
    }
}
