import android.app.AlertDialog
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
        val imgBtnFriendMain: ImageView = itemView.findViewById(R.id.imgBtnFriendMain)
        val imgBookmark: ImageView = itemView.findViewById(R.id.star)

        init {
            imgBtnFriendMain.setOnClickListener { clickedView ->
                val friendData = friendList[adapterPosition]
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    // 이미지 버튼 클릭 이벤트 이후에만 팝업 메뉴를 표시하도록 합니다.
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

        holder.itemView.setOnClickListener {
            // 아이템 클릭 시 해당 아이템의 위치를 전달
            val friendData = friendList[position]
            showBookTypeMenu(holder.imgBtnFriendMain, friendData, position)
        }

        holder.imgBookmark.setOnClickListener {
            // 즐겨 찾기 아이콘 클릭 시 해당 아이템의 위치를 전달
            val friendData = friendList[position]
            deleteBookmark(friendData, position)
        }
    }

    override fun getItemCount(): Int {
        return friendList.size
    }

    private fun showBookTypeMenu(view: View, friendData: FriendDataClass, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu_friend_main)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuItemSortBybookmark -> {
                    toggleBookmark(friendData, position)
                    true
                }
                R.id.menuItemSortByelimination -> {
                    // "삭제" 메뉴를 선택했을 때 처리
                    // showDeleteConfirmationDialog(view, friendData, position)
                    true
                }
                else -> false
            }
        }

        // 팝업 메뉴의 "삭제" 메뉴를 찾아서 클릭 이벤트 핸들러를 설정합니다.
        val menuItemSortByelimination = popupMenu.menu.findItem(R.id.menuItemSortByelimination)
        menuItemSortByelimination.setOnMenuItemClickListener {
            showDeleteConfirmationDialog(view, friendData, position)
            true
        }

        popupMenu.show()
    }


    private fun deleteFriend(clickedView: View, friendData: FriendDataClass, position: Int) {
        showDeleteConfirmationDialog(clickedView, friendData, position)
    }

    private fun showDeleteConfirmationDialog(clickedView: View, friendData: FriendDataClass, position: Int) {
        val builder = AlertDialog.Builder(clickedView.context) // 클릭된 뷰의 컨텍스트를 가져옵니다.
        builder.setTitle("삭제 확인")
        builder.setMessage("정말 삭제하시겠습니까?")

        builder.setPositiveButton("삭제") { dialog, which ->
            // "삭제" 버튼을 클릭한 경우에만 삭제 작업을 수행합니다.
            friendList.removeAt(position)
            notifyItemRemoved(position)
        }

        builder.setNegativeButton("취소") { dialog, which ->
            // "취소" 버튼을 클릭한 경우 아무 작업도 수행하지 않습니다.
        }

        val dialog = builder.create()
        dialog.show()
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










