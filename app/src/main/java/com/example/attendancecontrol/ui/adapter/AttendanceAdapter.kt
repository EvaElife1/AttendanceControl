package com.example.attendancecontrol.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancecontrol.R
import com.example.attendancecontrol.data.entity.StudentEntity

class AttendanceAdapter(
    private val students: List<StudentEntity>
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    private val selectedStatuses = mutableMapOf<Int, String>()

    class AttendanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupHeaderLayout: View = view.findViewById(R.id.groupHeaderLayout)
        val tvGroupHeader: TextView = view.findViewById(R.id.tvGroupHeader)
        val tvStudentName: TextView = view.findViewById(R.id.tvAttendanceStudentName)
        val rgStatus: RadioGroup = view.findViewById(R.id.rgStatus)
        val rbPresent: RadioButton = view.findViewById(R.id.rbPresent)
        val rbAbsent: RadioButton = view.findViewById(R.id.rbAbsent)
        val rbLate: RadioButton = view.findViewById(R.id.rbLate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_attendance, parent, false)
        return AttendanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val student = students[position]

        // Показываем заголовок группы, если это первый студент группы
        val isFirstInGroup = position == 0 || students[position - 1].groupName != student.groupName
        if (isFirstInGroup) {
            holder.groupHeaderLayout.visibility = View.VISIBLE
            holder.tvGroupHeader.text = "Группа: ${student.groupName}"
        } else {
            holder.groupHeaderLayout.visibility = View.GONE
        }

        holder.tvStudentName.text = "${student.lastName} ${student.firstName} (${student.groupName})"

        holder.rgStatus.setOnCheckedChangeListener(null)

        val savedStatus = selectedStatuses[position]
        when (savedStatus) {
            "Присутствовал" -> holder.rbPresent.isChecked = true
            "Отсутствовал" -> holder.rbAbsent.isChecked = true
            "Опоздал" -> holder.rbLate.isChecked = true
            else -> holder.rgStatus.clearCheck()
        }

        holder.rgStatus.setOnCheckedChangeListener { _, checkedId ->
            val status = when (checkedId) {
                R.id.rbPresent -> "Присутствовал"
                R.id.rbAbsent -> "Отсутствовал"
                R.id.rbLate -> "Опоздал"
                else -> ""
            }
            if (status.isNotEmpty()) {
                selectedStatuses[position] = status
            }
        }
    }

    override fun getItemCount(): Int = students.size

    fun getSelectedStatuses(): Map<Int, String> {
        return selectedStatuses
    }
}
