package com.example.attendancecontrol

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AttendanceAdapter(
    private val students: List<StudentEntity> // <-- ИЗМЕНЕНО на StudentEntity
) : RecyclerView.Adapter<AttendanceAdapter.AttendanceViewHolder>() {

    private val selectedStatuses = mutableMapOf<Int, String>()

    class AttendanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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