package com.example.reading_cycle.chat.ui

import ChatListAdapter
import android.content.Intent
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
import com.example.reading_cycle.chat.vm.ChatRoomActivity
import com.example.reading_cycle.databinding.FragmentChatListBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(), ChatListAdapter.OnChatItemClickListener {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentChatListBinding: FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatRoom>()
    private val userIdx: String? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mainActivity = activity as MainActivity
        fragmentChatListBinding = FragmentChatListBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()

        val database = Firebase.database.reference.child("chatRoom")

        // 기타 코드 유지

        // 초기 빈 어댑터 설정
        chatListAdapter = ChatListAdapter(emptyList(), this)
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
                        profileImage = R.drawable.ic_launcher_foreground,
                        name = it.name ?: "Unknown",
                        lastMessage = it.lastMessage ?: "No message",
                        lastMessageTime = it.lastMessageTime ?: "Unknown time",
                        chatRoomId = it.chatRoomId ?: " "
                    )
                }

                Log.d("ChatListFragment", "Loaded chat items: $chatItems")
                requireActivity().runOnUiThread {
                    chatListAdapter = ChatListAdapter(chatItems, this@ChatListFragment)
                    fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter
                    chatListAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Firebase Database error: ${error.message}")
            }
        })
    }

    override fun onChatItemClicked(chatItem: ChatItem) {
        // 클릭된 아이템의 ChatRoomId와 name 가져오기
        val chatRoomId = chatItem.chatRoomId
        val name = chatItem.name

        // Intent 생성 및 ChatRoomActivity로 전환
        val intent = Intent(requireContext(), ChatRoomActivity::class.java)
        intent.putExtra("chatRoomId", chatRoomId)
        intent.putExtra("name", name)

        // 인텐트에 포함된 데이터를 Log로 출력
        Log.d("IntentDebug", "Sending chatRoomId: $chatRoomId")
        Log.d("IntentDebug", "Sending name: $name")

        startActivity(intent)
    }

}
