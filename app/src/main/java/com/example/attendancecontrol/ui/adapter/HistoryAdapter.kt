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
import com.example.attendancecontrol.data.db.AttendanceWithStudent

/**
 * Адаптер для списка записей посещаемости на экране «Статистика».
 * Использует ListAdapter + DiffUtil для эффективного обновления списка при фильтрации.
 * Каждая строка отображает ФИО, группу, предмет, дату и цветной статус-чип.
 */
class HistoryAdapter : ListAdapter<AttendanceWithStudent, HistoryAdapter.ViewHolder>(DIFF) {

    /** ViewHolder: кэширует View-ссылки для одной строки истории */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvFullName: TextView = view.findViewById(R.id.tvFullName)
        val tvGroup: TextView    = view.findViewById(R.id.tvGroup)
        val tvSubject: TextView  = view.findViewById(R.id.tvSubject)
        val tvDate: TextView     = view.findViewById(R.id.tvDate)
        val tvStatus: TextView   = view.findViewById(R.id.tvStatus)
    }

    /** Создаёт новый ViewHolder, надувая layout item_history_row */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history_row, parent, false)
        return ViewHolder(view)
    }

    /** Привязывает данные записи к ViewHolder; раскрашивает чип статуса в нужный цвет */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.tvFullName.text = "${item.lastName} ${item.firstName}"
        holder.tvGroup.text    = "Гр. ${item.groupName}"
        holder.tvSubject.text  = item.subject
        holder.tvDate.text     = item.lessonDate
        holder.tvStatus.text   = item.status

        val ctx = holder.itemView.context

        // Определяем фон и цвет текста чипа в зависимости от статуса
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
                R.drawable.bg_chip_absent,
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
        /** DiffUtil: сравнивает элементы по id и содержимому для минимального обновления списка */
        private val DIFF = object : DiffUtil.ItemCallback<AttendanceWithStudent>() {
            override fun areItemsTheSame(a: AttendanceWithStudent, b: AttendanceWithStudent) = a.id == b.id
            override fun areContentsTheSame(a: AttendanceWithStudent, b: AttendanceWithStudent) = a == b
        }
    }
}
