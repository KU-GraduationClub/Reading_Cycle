import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.reading_cycle.R
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.databinding.RowChatListBinding

// RecyclerView 어댑터 클래스
class ChatListAdapter(
    private val chatList: List<ChatItem>,
    private val listener: OnChatItemClickListener
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    // 클릭 리스너 인터페이스
    interface OnChatItemClickListener {
        fun onChatItemClicked(chatItem: ChatItem)
    }

    // ViewHolder 클래스 정의
    class ChatViewHolder(private val binding: RowChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        // 뷰에 데이터를 바인딩하는 메서드
        fun bind(chatItem: ChatItem) {
            // Glide를 사용하여 프로필 이미지 URL을 로드
            Glide.with(binding.imgProfile.context)
                .load(chatItem.profileImage)  // 'profileImage'는 이미지 URL
                .placeholder(R.drawable.ic_launcher_foreground)  // 로딩 중 표시할 기본 이미지
                .into(binding.imgProfile)

            binding.apply {
                textUsername.text = chatItem.name  // 사용자 이름 설정
                textLastMessage.text = chatItem.lastMessage  // 마지막 메시지 설정
                textLastMessageTime.text = chatItem.lastMessageTime  // 마지막 메시지 시간 설정
            }
        }
    }

    // 새로운 ViewHolder를 생성하는 메서드
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        // RowChatListBinding을 사용하여 레이아웃을 inflate
        val binding = RowChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    // 특정 위치의 아이템 뷰에 데이터를 바인딩하는 메서드
    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.bind(chatItem)  // ViewHolder에 데이터 바인딩

        // 아이템 클릭 시 리스너 호출
        holder.itemView.setOnClickListener {
            listener.onChatItemClicked(chatItem)
        }
    }

    // 아이템 개수를 반환하는 메서드
    override fun getItemCount(): Int {
        return chatList.size  // 채팅 아이템 리스트의 크기 반환
    }
}
