package com.example.attendancecontrol

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StudentsActivity : AppCompatActivity() {

    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etGroup: EditText
    private lateinit var btnAddStudent: Button
    private lateinit var rvStudents: RecyclerView

    private val students = mutableListOf<Student>()
    private lateinit var adapter: StudentAdapter
    private var nextId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

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
    }

    private fun addStudent() {
        val lastName = etLastName.text.toString()
        val firstName = etFirstName.text.toString()
        val group = etGroup.text.toString()

        // Используем StudentValidator
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

        val student = Student(
            id = nextId++,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            groupName = group.trim()
        )

        students.add(student)
        adapter.notifyItemInserted(students.size - 1)

        etLastName.text.clear()
        etFirstName.text.clear()
        etGroup.text.clear()

        Toast.makeText(this, "Студент добавлен", Toast.LENGTH_SHORT).show()
    }

    private fun showDeleteDialog(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Удаление студента")
            .setMessage("Удалить ${student.lastName} ${student.firstName}?")
            .setPositiveButton("Удалить") { _, _ ->
                val position = students.indexOf(student)
                if (position != -1) {
                    students.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this, "Студент удалён", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}