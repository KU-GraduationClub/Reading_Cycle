package com.example.reading_cycle.notify.model

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.reading_cycle.databinding.RowNotifyBinding


class NotifyAdapter(private val notifyList: List<NotifyDataClass>) : RecyclerView.Adapter<NotifyAdapter.NotifyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotifyViewHolder {
        val binding = RowNotifyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotifyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotifyViewHolder, position: Int) {
        val notifyData = notifyList[position]
        holder.bind(notifyData)
    }

    override fun getItemCount(): Int {
        return notifyList.size
    }

    inner class NotifyViewHolder(private val binding: RowNotifyBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(notifyData: NotifyDataClass) {
            binding.textNotify.text = notifyData.NotifyContent
            binding.textNotifyTime.text = notifyData.NotifyTimestamp.toString()
            // 필요한 경우 다른 뷰에도 데이터 설정
        }
    }
}