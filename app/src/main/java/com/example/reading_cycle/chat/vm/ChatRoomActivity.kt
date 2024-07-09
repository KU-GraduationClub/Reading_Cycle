package com.example.reading_cycle.chat.vm


import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reading_cycle.chat.adapter.MessageAdapter
import com.example.reading_cycle.chat.model.DataMessage
import com.example.reading_cycle.databinding.ActivityChatRoomBinding
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatRoomActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatRoomBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var databaseReference: DatabaseReference
    private var name: String = ""
    private var userNickname: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatRoomBinding.inflate(layoutInflater)
        val view = binding.root


        setContentView(view)
        val db = Firebase.firestore
        val userIdx = "NOlPsc5cyVf2rxxAZvbAbDc0yrF2" // 임의로 설정
        db.collection("users")
            .document(userIdx)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    // '1111'의 nickname 가져오기
                    val userNickname = document.getString("userNickname")

                    if (userNickname != null) {
                        // 가져온 nickname을 사용
                        Log.d("UserInfo", "userNickname: $userNickname")
                    }
                } else {
                    Log.d("UserInfo", "Document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("UserInfo", "Error getting document: ", exception)
            }
        FirebaseApp.initializeApp(this)

        val firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.reference

        // RecyclerView 설정
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerViewMessages.layoutManager = layoutManager
        // textOpponent를 "이도형"로 설정
        binding.textOpponent.text = "이도형"
        val RoomID = "room2"


        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                Log.d("MessageActivity", "데이터베이스에 연결되었습니다.")
                val dataList = mutableListOf<DataMessage>()

                val specificPathSnapshot = dataSnapshot.child("chatRooms").child(RoomID) // 루트 설정

                for (snapshot in specificPathSnapshot.children) {
                    val message = snapshot.child("message").getValue(String::class.java)
                    val timestamp = snapshot.child("timestamp").getValue(String::class.java)
                    val name = snapshot.child("name").getValue(String::class.java)
                    if (message != null && timestamp != null && name != null) {
                        val content = DataMessage(message, timestamp.toString(), name)
                        dataList.add(content)
                    }
                }

                // 어댑터 초기화 및 데이터 설정
                name = userNickname
                messageAdapter = MessageAdapter(dataList, name) // name 변수 추가
                binding.recyclerViewMessages.adapter = messageAdapter

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MessageActivity", "데이터베이스 연결에 실패했습니다: ${error.message}")
            }
        })

        binding.btnSubmit.setOnClickListener {
            val messageContent = binding.edtSend.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val currentTime = System.currentTimeMillis()
                val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.KOREA)
                val formattedTime = dateFormat.format(Date())

                val content = DataMessage(messageContent, formattedTime, name)
                databaseReference.child("chatRooms").child(RoomID).push().setValue(content)
                    .addOnSuccessListener {
                        Log.d("MessageActivity", "데이터 쓰기 성공: $content")
                        binding.edtSend.setText("")
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MessageActivity", "데이터 쓰기 실패: ${exception.message}")
                    }
            } else {
                Log.e("MessageActivity", "메시지를 입력하세요.")
            }
        }
    }
}
