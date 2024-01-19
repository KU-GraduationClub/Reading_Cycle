package com.example.reading_cycle.post.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

data class SwapDataClass(val title: String, val author: String)
data class SaleDataClass(val title: String, val author: String)

class PostMainAdapter(private val swapBookList: List<SwapDataClass>, private val saleList: List<SaleDataClass>) : RecyclerView.Adapter<PostMainAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 여기에 ViewHolder의 뷰들을 초기화하는 코드를 추가할 수 있습니다.
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        // ViewHolder를 생성하고 초기화합니다.
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_post_main_swap, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        // ViewHolder의 데이터를 설정합니다.
        when {
            position < swapBookList.size -> {
                val swapData = swapBookList[position]
                // TODO: SwapViewHolder에 데이터 바인딩
            }
            else -> {
                val saleData = saleList[position - swapBookList.size]
                // TODO: SaleViewHolder에 데이터 바인딩
            }
        }
    }

    override fun getItemCount(): Int {
        return swapBookList.size + saleList.size
    }
}