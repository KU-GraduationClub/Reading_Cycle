package com.example.reading_cycle.library.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.R

data class SwapBooknameDataClass(val title: String)
data class SaleBooknameDataClass(val title: String)

class LibraryMainAdapter(private val swapBooknameList: List<SwapBooknameDataClass>, private val saleBooknameList: List<SaleBooknameDataClass>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    // View Type 상수 정의
    private val VIEW_TYPE_SWAPBOOKNAME = 1
    private val  VIEW_TYPE_SALEBOOKNAME = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SWAPBOOKNAME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_library_item_swap, parent, false)
                SwapViewHolder(view)
            }
            VIEW_TYPE_SALEBOOKNAME -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.row_library_item_sale, parent, false)
                SaleViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_TYPE_SWAPBOOKNAME -> {
                val swapbooknameData =  swapBooknameList[position]
                (holder as SwapViewHolder).bind(swapbooknameData)
            }
            VIEW_TYPE_SALEBOOKNAME -> {
                val salebooknameData = swapBooknameList[position - swapBooknameList.size]
                (holder as SaleViewHolder).bind(salebooknameData)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun getItemCount(): Int {
        return  swapBooknameList.size +  swapBooknameList.size
    }

    override fun getItemViewType(position: Int): Int {
        return when {
            position <  swapBooknameList.size ->  VIEW_TYPE_SWAPBOOKNAME
            else -> VIEW_TYPE_SALEBOOKNAME
        }
    }

    // SwapViewHolder 구현
    class SwapViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: SwapViewHolder의 뷰들을 초기화하는 코드를 추가할 수 있습니다.

        fun bind(swapData: SwapBooknameDataClass) {
            // TODO: SwapDataClass 데이터를 뷰에 바인딩하는 코드를 추가할 수 있습니다.
        }
    }

    // SaleViewHolder 구현
    class SaleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // TODO: SaleViewHolder의 뷰들을 초기화하는 코드를 추가할 수 있습니다.

        fun bind(saleData: SwapBooknameDataClass) {
            // TODO: SaleDataClass 데이터를 뷰에 바인딩하는 코드를 추가할 수 있습니다.
        }
    }
}