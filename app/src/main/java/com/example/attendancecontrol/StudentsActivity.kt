package com.example.attendancecontrol

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class StudentsActivity : AppCompatActivity() {

    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etGroup: EditText
    private lateinit var btnAddStudent: Button
    private lateinit var rvStudents: RecyclerView

    private lateinit var studentDao: StudentDao
    private val students = mutableListOf<StudentEntity>()
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        val db = AppDatabase.getDatabase(this)
        studentDao = db.studentDao()

        etLastName = findViewById(R.id.etLastName)
        etFirstName = findViewById(R.id.etFirstName)
        etGroup = findViewById(R.id.etGroup)
        btnAddStudent = findViewById(R.id.btnAddStudent)
        rvStudents = findViewById(R.id.rvStudents)

        adapter = StudentAdapter(students) { student ->
            showDeleteDialog(student)
        }
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        btnAddStudent.setOnClickListener {
            addStudent()
        }

        loadStudents()
    }

    private fun loadStudents() {
        lifecycleScope.launch {
            val list = studentDao.getAll()
            students.clear()
            students.addAll(list)
            adapter.notifyDataSetChanged()
        }
    }

    private fun addStudent() {
        val lastName = etLastName.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val group = etGroup.text.toString().trim()

        val error = StudentValidator.validate(lastName, firstName, group)
        if (error != null) {
            when {
                error.contains("фамилию") -> {
                    etLastName.error = error
                    etLastName.requestFocus()
                }
                error.contains("имя") -> {
                    etFirstName.error = error
                    etFirstName.requestFocus()
                }
                else -> {
                    etGroup.error = error
                    etGroup.requestFocus()
                }
            }
            return
        }

        val student = StudentEntity(
            firstName = firstName,
            lastName = lastName,
            groupName = group
        )

        lifecycleScope.launch {
            studentDao.insert(student)
            loadStudents()

            etLastName.text.clear()
            etFirstName.text.clear()
            etGroup.text.clear()

            Toast.makeText(this@StudentsActivity, "Студент добавлен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDeleteDialog(student: StudentEntity) {
        AlertDialog.Builder(this)
            .setTitle("Удаление студента")
            .setMessage("Удалить ${student.lastName} ${student.firstName}?")
            .setPositiveButton("Удалить") { _, _ ->
                lifecycleScope.launch {
                    studentDao.delete(student)
                    loadStudents()
                    Toast.makeText(this@StudentsActivity, "Студент удалён", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}