import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.chat.model.ChatItem
import com.example.reading_cycle.databinding.RowChatListBinding

class ChatListAdapter(private val chatList: List<ChatItem>, private val listener: OnChatItemClickListener) :
    RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {

    interface OnChatItemClickListener {
        fun onChatItemClicked(chatItem: ChatItem)
    }

    class ChatViewHolder(private val binding: RowChatListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(chatItem: ChatItem) {
            binding.apply {
                imgProfile.setImageResource(chatItem.profileImage)
                textUsername.text = chatItem.name
                textLastMessage.text = chatItem.lastMessage
                textLastMessageTime.text = chatItem.lastMessageTime
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = RowChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chatItem = chatList[position]
        holder.bind(chatItem)

        holder.itemView.setOnClickListener {
            listener.onChatItemClicked(chatItem)
        }
    }

    override fun getItemCount(): Int {
        return chatList.size
    }
}
