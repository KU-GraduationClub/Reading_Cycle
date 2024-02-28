import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

data class FriendDataClass(val title: String, var isBookmarked: Boolean = false)

class FriendMainAdapter(private val friendList: MutableList<FriendDataClass>) : RecyclerView.Adapter<FriendMainAdapter.FriendViewHolder>() {

    inner class FriendViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgBtnFriendMain: ImageView = itemView.findViewById(R.id.imgBtnFriendMain)
        private val imgBookmark: ImageView = itemView.findViewById(R.id.star) // 즐겨찾기 아이콘

        init {
            // 이미지 버튼에 클릭 이벤트 핸들러 설정
            imgBtnFriendMain.setOnClickListener {
                // 현재 아이템의 위치(position)에 대한 클릭 이벤트 처리
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val friendData = friendList[position]
                    // 클릭된 아이템에 대한 작업 수행
                    // 여기서는 팝업 메뉴를 표시하는 로직을 호출하도록 합니다.
                    showBookTypeMenu(imgBtnFriendMain, friendData, position)
                }
            }

            // 즐겨 찾기 아이콘 클릭 이벤트 핸들러 설정
            imgBookmark.setOnClickListener {
                // 현재 아이템의 위치(position)에 대한 클릭 이벤트 처리
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val friendData = friendList[position]
                    // 클릭된 아이템에 대한 작업 수행
                    // 여기서는 즐겨 찾기 아이콘을 삭제하는 로직을 호출하도록 합니다.
                    deleteBookmark(friendData, position)
                }
            }
        }

        fun bind(friendData: FriendDataClass) {
            // 아이템 데이터를 뷰에 바인딩하는 로직을 작성합니다.
            imgBookmark.visibility = if (friendData.isBookmarked) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_friend_item_layout, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        holder.bind(friendList[position])
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    private fun showBookTypeMenu(view: View, friendData: FriendDataClass, position: Int) {
        // 팝업 메뉴를 위한 PopupMenu 객체 생성
        val popupMenu = PopupMenu(view.context, view)

        // 팝업 메뉴에 XML 리소스 연결
        popupMenu.inflate(R.menu.popup_menu_friend_main)

        // 팝업 메뉴 아이템 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuItemSortBybookmark -> {
                    // "즐겨 찾기" 선택 시 처리
                    // 여기에 즐겨 찾기 관련 작업을 추가하세요.
                    toggleBookmark(friendData, position)
                    true
                }

                R.id.menuItemSortByelimination -> {
                    deleteFriend(friendData, position)
                    true
                }

                else -> false
            }
        }

        // 팝업 메뉴 표시
        popupMenu.show()
    }

    private fun deleteFriend(friendData: FriendDataClass, position: Int) {
        friendList.removeAt(position)
        notifyItemRemoved(position)
    }

    // 즐겨 찾기 아이콘 토글
    private fun toggleBookmark(friendData: FriendDataClass, position: Int) {
        if (friendData.isBookmarked) {
            // 이미 즐겨찾기 상태인 경우, 상단 고정을 해제합니다.
            friendData.isBookmarked = false
            // 현재 위치에서 제거하고, 원래 위치에 다시 추가합니다.
            friendList.removeAt(position)
            friendList.add(position, friendData)
            notifyItemMoved(position, friendList.indexOf(friendData))
        } else {
            // 즐겨찾기 상태가 아닌 경우, 상단 고정을 수행합니다.
            friendData.isBookmarked = true
            // 현재 위치에서 제거하고, 리스트의 맨 앞에 추가합니다.
            friendList.removeAt(position)
            friendList.add(0, friendData)
            notifyItemMoved(position, 0)
        }
        notifyItemChanged(0) // 첫 번째 아이템을 갱신하여 아이콘이 생성될 수 있도록 합니다.
    }

}
    // 즐겨 찾기 아이콘 삭제
    private fun deleteBookmark(friendData: FriendDataClass, position: Int) {
        friendData.isBookmarked = false

    }









