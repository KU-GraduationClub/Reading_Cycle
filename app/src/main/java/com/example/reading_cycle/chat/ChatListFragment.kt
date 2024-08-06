package com.example.reading_cycle.chat


import ChatListAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.MainActivity
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.chat.model.ChatRoom
import com.example.reading_cycle.chat.vm.ChatRoomActivity
import com.example.reading_cycle.databinding.FragmentChatListBinding
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase

class ChatListFragment : Fragment(), ChatListAdapter.OnChatItemClickListener {

    private lateinit var mainActivity: MainActivity
    private lateinit var fragmentChatListBinding: FragmentChatListBinding
    private lateinit var chatListAdapter: ChatListAdapter
    private val chatRoomList = mutableListOf<ChatRoom>()
    private lateinit var database: DatabaseReference
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mainActivity = activity as MainActivity
        fragmentChatListBinding = FragmentChatListBinding.inflate(inflater, container, false)
        mainActivity.showBottomNavigation()

        database = Firebase.database.reference.child("chatRooms")
        firestore = FirebaseFirestore.getInstance() // Firestore 인스턴스 초기화

        // 초기 빈 어댑터 설정
        chatListAdapter = ChatListAdapter(emptyList(), this)
        fragmentChatListBinding.recyclerChatList.layoutManager = LinearLayoutManager(requireContext())
        fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter

        return fragmentChatListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // roomaddbtn 클릭 리스너 설정
        fragmentChatListBinding.roomaddbtn.setOnClickListener {
            showAddRoomDialog()
        }

        // Firebase 데이터베이스 리스너 설정
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatRoomList.clear()
                val chatItemList = mutableListOf<ChatItem>()

                // 모든 채팅방 가져오기
                for (childSnapshot in snapshot.children) {
                    val chatRoom = childSnapshot.getValue(ChatRoom::class.java)
                    chatRoom?.let { chatRoomList.add(it) }
                }

                // Firestore에서 프로필 이미지 가져오기
                fetchUserProfileImages(chatRoomList) { userProfileImages ->
                    for (chatRoom in chatRoomList) {
                        val imageUrl = userProfileImages[chatRoom.name] ?: ""
                        chatItemList.add(ChatItem(
                            profileImage = imageUrl,
                            name = chatRoom.name ?: "Unknown",
                            lastMessage = chatRoom.lastMessage ?: "No message",
                            lastMessageTime = chatRoom.lastMessageTime ?: "Unknown time",
                            chatRoomId = chatRoom.chatRoomId ?: " "
                        ))
                    }

                    // 어댑터 업데이트
                    Log.d("ChatListFragment", "Loaded chat items: $chatItemList")
                    chatListAdapter = ChatListAdapter(chatItemList, this@ChatListFragment)
                    fragmentChatListBinding.recyclerChatList.adapter = chatListAdapter
                    chatListAdapter.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatListFragment", "Firebase Database error: ${error.message}")
            }
        })
    }

    private fun fetchUserProfileImages(chatRoomList: List<ChatRoom>, callback: (Map<String, String>) -> Unit) {
        val userProfileImages = mutableMapOf<String, String>()
        val userNicknames = chatRoomList.mapNotNull { it.name }.distinct()
        val tasks = mutableListOf<Task<QuerySnapshot>>()

        for (nickname in userNicknames) {
            val task: Task<QuerySnapshot> = firestore
                .collection("users")
                .whereEqualTo("userNickname", nickname)
                .get()
                .addOnSuccessListener { documents: QuerySnapshot ->  // 명시적 타입 정의
                    if (documents.size() > 0) {
                        val document = documents.first()
                        val imageUrl = document.getString("userProfileImage")
                        if (imageUrl != null) {
                            userProfileImages[nickname] = imageUrl
                        }
                    }
                }

            tasks.add(task)
        }

        Tasks.whenAllSuccess<Task<QuerySnapshot>>(tasks).addOnCompleteListener {
            callback(userProfileImages)
        }.addOnFailureListener {
            Log.e("ChatListFragment", "Failed to fetch user profile images")
        }
    }

    private fun showAddRoomDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("상대방 이름 입력")

        val input = EditText(requireContext())
        input.inputType = android.text.InputType.TYPE_CLASS_TEXT
        builder.setView(input)

        builder.setPositiveButton("확인") { dialog, which ->
            val addName = input.text.toString()
            if (addName.isNotEmpty()) {
                checkUserNicknameExists(addName) { exists ->
                    if (exists) {
                        createChatRoom(addName)
                    } else {
                        Toast.makeText(requireContext(), "존재하지 않는 사용자입니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "상대방이름을 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("취소") { dialog, which -> dialog.cancel() }
        builder.show()
    }

    private fun checkUserNicknameExists(nickname: String, callback: (Boolean) -> Unit) {
        firestore.collection("users")  // "users"는 Firestore에서 사용자 정보를 저장하는 컬렉션 이름
            .get()
            .addOnSuccessListener { result ->
                val exists = result.any { it.getString("userNickname") == nickname }
                callback(exists)
            }
            .addOnFailureListener { exception ->
                Log.e("ChatListFragment", "Firestore error: ${exception.message}")
                Toast.makeText(requireContext(), "사용자 확인 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
                callback(false)
            }
    }

    private fun createChatRoom(addName: String) {
        val chatRoomRef = database.push()
        val chatRoomId = chatRoomRef.key ?: return Toast.makeText(requireContext(), "채팅방 ID를 생성할 수 없습니다.", Toast.LENGTH_SHORT).show()

        val chatRoom = ChatRoom(
            chatRoomId = chatRoomId,
            name = addName,
            lastMessage = null,
            lastMessageTime = null
        )

        chatRoomRef.setValue(chatRoom)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "채팅방이 생성되었습니다: $addName", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { exception ->
                Log.e("ChatListFragment", "Failed to create chat room", exception)
                Toast.makeText(requireContext(), "채팅방 생성에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onChatItemClicked(chatItem: ChatItem) {
        val chatRoomId = chatItem.chatRoomId
        val name = chatItem.name

        firestore.collection("users")
            .whereEqualTo("userNickname", name) // 클릭한 상대방의 닉네임으로 쿼리
            .get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    val document = documents.first()
                    val userProfileImage = document.getString("userProfileImage") // 프로필 이미지 URL
                    val userNickname = document.getString("userNickname") // 사용자의 닉네임

                    // Intent 생성 및 ChatRoomActivity로 전환
                    val intent = Intent(requireContext(), ChatRoomActivity::class.java).apply {
                        putExtra("chatRoomId", chatRoomId)
                        putExtra("name", name)
                        putExtra("userProfileImage", userProfileImage) // 프로필 이미지 전달
                        putExtra("userNickname", userNickname) // 사용자 닉네임 전달
                    }

                    Log.d("IntentDebug", "Sending chatRoomId: $chatRoomId")
                    Log.d("IntentDebug", "Sending name: $name")
                    Log.d("IntentDebug", "Sending userProfileImage: $userProfileImage")
                    Log.d("IntentDebug", "Sending userNickname: $userNickname")

                    startActivity(intent)
                } else {
                    Toast.makeText(requireContext(), "해당 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ChatListFragment", "Firestore error: ${exception.message}")
            }
    }
}
