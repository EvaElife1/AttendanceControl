package com.example.attendancecontrol.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancecontrol.R
import com.example.attendancecontrol.data.entity.StudentEntity

class StudentAdapter(
    private val students: List<StudentEntity>,
    private val onDeleteClick: (StudentEntity) -> Unit
) : RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    class StudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val groupHeaderLayout: View = view.findViewById(R.id.groupHeaderLayout)
        val tvGroupHeader: TextView = view.findViewById(R.id.tvGroupHeader)
        val tvName: TextView = view.findViewById(R.id.tvStudentName)
        val tvGroup: TextView = view.findViewById(R.id.tvStudentGroup)
        val btnDelete: Button = view.findViewById(R.id.btnDeleteStudent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]

        // Показываем заголовок группы, если это первый студент группы
        val isFirstInGroup = position == 0 || students[position - 1].groupName != student.groupName
        if (isFirstInGroup) {
            holder.groupHeaderLayout.visibility = View.VISIBLE
            holder.tvGroupHeader.text = "Группа: ${student.groupName}"
        } else {
            holder.groupHeaderLayout.visibility = View.GONE
        }

        holder.tvName.text = "${student.lastName} ${student.firstName}"
        holder.tvGroup.text = "Группа: ${student.groupName}"
        holder.btnDelete.setOnClickListener { onDeleteClick(student) }
    }

    override fun getItemCount(): Int = students.size
}
