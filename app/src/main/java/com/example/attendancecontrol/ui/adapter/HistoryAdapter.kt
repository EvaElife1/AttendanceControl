package com.example.attendancecontrol.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancecontrol.R
import com.example.attendancecontrol.data.entity.AttendanceEntity

class HistoryAdapter : ListAdapter<AttendanceEntity, HistoryAdapter.ViewHolder>(DIFF) {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvSubject: TextView = view.findViewById(R.id.tvSubject)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvDate.text = item.lessonDate
        holder.tvSubject.text = item.subject

        val ctx = holder.itemView.context
        holder.tvStatus.text = item.status

        // Окраска чипа статуса в зависимости от значения
        val (bgRes, fgColor) = when (item.status) {
            "Присутствовал" -> Pair(
                R.drawable.bg_chip_present,
                ContextCompat.getColor(ctx, R.color.stat_present_text)
            )
            "Отсутствовал" -> Pair(
                R.drawable.bg_chip_absent,
                ContextCompat.getColor(ctx, R.color.stat_absent_text)
            )
            "Опоздал" -> Pair(
                R.drawable.bg_chip_absent,  // переиспользуем форму, цвет другой
                ContextCompat.getColor(ctx, R.color.stat_late_text)
            )
            else -> Pair(
                R.drawable.bg_chip_present,
                ContextCompat.getColor(ctx, R.color.stat_other_text)
            )
        }
        holder.tvStatus.setBackgroundResource(bgRes)
        holder.tvStatus.setTextColor(fgColor)
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<AttendanceEntity>() {
            override fun areItemsTheSame(a: AttendanceEntity, b: AttendanceEntity) = a.id == b.id
            override fun areContentsTheSame(a: AttendanceEntity, b: AttendanceEntity) = a == b
        }
    }
}
