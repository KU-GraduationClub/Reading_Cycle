package com.example.reading_cycle.chat.vm

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.adapter.MessageAdapter
import com.example.reading_cycle.chat.model.DataMessage
import com.example.reading_cycle.databinding.ActivityChatRoomBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var databaseReference: DatabaseReference
    private var name: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val chatRoomId = intent.getStringExtra("chatRoomId")
        val oppname = intent.getStringExtra("name")

        // 뒤로가기 버튼 설정
        val backButton: ImageButton = findViewById(R.id.imgBtnQuit)
        backButton.setOnClickListener {
            onBackPressed()
        }

        // 기본 ActionBar 숨김
        supportActionBar?.hide()

        FirebaseApp.initializeApp(this)
        name = "이도형"

        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.layoutManager = layoutManager
        binding.textOpponent.text = oppname.toString()

        databaseReference.child("chatRooms").child(chatRoomId.toString()).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val dataList = mutableListOf<DataMessage>()

                    for (snapshot in dataSnapshot.children) {
                        val message = snapshot.child("message").getValue(String::class.java)
                        val timestamp = snapshot.child("timestamp").getValue(String::class.java)
                        val name = snapshot.child("name").getValue(String::class.java)
                        val messageId = snapshot.key

                        // 로그 추가: 확인
                        Log.d("ChatRoomActivity", "Loading message: $message, timestamp: $timestamp, name: $name")

                        if (message != null && timestamp != null && name != null && messageId != null) {
                            val content = DataMessage(message, timestamp, name, messageId)
                            dataList.add(content)
                        }
                    }

                    // 데이터가 비어 있지 않은지 확인
                    if (dataList.isEmpty()) {
                        Log.e("ChatRoomActivity", "No messages found in the chat room.")
                    }

                    // 메시지를 timestamp에 따라 정렬
                    dataList.sortBy {
                        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA).parse(it.timestamp)
                    }

                    // 어댑터 설정
                    messageAdapter = MessageAdapter(dataList, name)
                    binding.recyclerViewMessages.adapter = messageAdapter
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatRoomActivity", "Failed to connect to database: ${error.message}")
                }
            })


        binding.btnSubmit.setOnClickListener {
            val messageContent = binding.edtSend.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
                val formattedTime = dateFormat.format(Date(currentTime)) // 현재 시간 포맷팅

                val newMessageRef = databaseReference.child("chatRooms").child(chatRoomId.toString()).child("messages").push()
                val messageId = newMessageRef.key ?: ""
                val content = DataMessage(messageContent, formattedTime, name, messageId)

                newMessageRef.setValue(content)
                    .addOnSuccessListener {
                        Log.d("MessageActivity", "Data write successful: $content")
                        binding.edtSend.setText("")

                        // 마지막 메시지 및 시간 업데이트
                        databaseReference.child("chatRooms").child(chatRoomId.toString()).child("lastMessage").setValue(messageContent)
                        databaseReference.child("chatRooms").child(chatRoomId.toString()).child("lastMessageTime").setValue(formattedTime) // 최신 메시지 시간 업데이트
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MessageActivity", "Data write failed: ${exception.message}")
                    }
            } else {
                Log.e("MessageActivity", "Please enter a message.")
            }
        }


    }

    override fun onResume() {
        super.onResume()
        val chatRoomId = intent.getStringExtra("chatRoomId") ?: return
        val currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)
        val formattedTime = dateFormat.format(Date(currentTime))
        databaseReference.child("chatRooms").child(chatRoomId).child("lastReadTimestamp").setValue(formattedTime)
    }

    override fun onBackPressed() {
        super.onBackPressed()

        // 채팅방 ID 가져오기
        val chatRoomId = intent.getStringExtra("chatRoomId") ?: return
        val currentTime = System.currentTimeMillis()  // 현재 시간(long)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA)  // 날짜 형식 설정
        val formattedTime = dateFormat.format(Date(currentTime))  // 현재 시간을 지정된 형식으로 변환

        // 데이터베이스에서 lastReadTimestamp 업데이트
        databaseReference.child("chatRooms").child(chatRoomId).child("lastReadTimestamp").setValue(formattedTime)

        Log.d("ChatRoomActivity", "Last read timestamp updated: $formattedTime") // 로그 추가
    }

}
