package com.example.attendancecontrol.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancecontrol.R
import com.example.attendancecontrol.data.db.AppDatabase
import com.example.attendancecontrol.data.entity.StudentEntity
import com.example.attendancecontrol.ui.adapter.StudentAdapter
import com.example.attendancecontrol.util.applyStatusBarPadding
import kotlinx.coroutines.launch

class StudentsActivity : AppCompatActivity() {

    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etGroup: EditText
    private lateinit var btnAddStudent: Button
    private lateinit var rvStudents: RecyclerView
    private lateinit var tvStudentsCount: TextView

    private val studentsList = mutableListOf<StudentEntity>()
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        findViewById<View>(R.id.headerLayout)?.let { applyStatusBarPadding(it) }

        db = AppDatabase.getDatabase(this)

        etLastName = findViewById(R.id.etLastName)
        etFirstName = findViewById(R.id.etFirstName)
        etGroup = findViewById(R.id.etGroup)
        btnAddStudent = findViewById(R.id.btnAddStudent)
        rvStudents = findViewById(R.id.rvStudents)
        tvStudentsCount = findViewById(R.id.tvStudentsCount)

        adapter = StudentAdapter(studentsList) { studentEntity -> showDeleteDialog(studentEntity) }
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        btnAddStudent.setOnClickListener { addStudent() }
        loadStudents()
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val entities = db.studentDao().getAll()
            studentsList.clear()
            studentsList.addAll(entities)
            adapter.notifyDataSetChanged()
            tvStudentsCount.text = "Студентов: ${studentsList.size}"
        }
    }

    private fun addStudent() {
        val lastName = etLastName.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val group = etGroup.text.toString().trim()

        if (lastName.isEmpty() || firstName.isEmpty() || group.isEmpty()) {
            Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            db.studentDao().insert(
                StudentEntity(firstName = firstName, lastName = lastName, groupName = group)
            )

            etLastName.text.clear()
            etFirstName.text.clear()
            etGroup.text.clear()
            Toast.makeText(this@StudentsActivity, "Студент добавлен", Toast.LENGTH_SHORT).show()
            loadStudents()
        }
    }

    private fun showDeleteDialog(student: StudentEntity) {
        AlertDialog.Builder(this)
            .setTitle("Удаление")
            .setMessage("Удалить ${student.lastName} ${student.firstName}?")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    db.studentDao().delete(student)
                    loadStudents()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
