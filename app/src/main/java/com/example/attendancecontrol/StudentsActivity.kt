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

    private val studentsList = mutableListOf<StudentEntity>()
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        db = AppDatabase.getDatabase(this)

        etLastName = findViewById(R.id.etLastName)
        etFirstName = findViewById(R.id.etFirstName)
        etGroup = findViewById(R.id.etGroup)
        btnAddStudent = findViewById(R.id.btnAddStudent)
        rvStudents = findViewById(R.id.rvStudents)

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
        }
    }

    private fun addStudent() {
        val lastName = etLastName.text.toString().trim()
        // ИСПРАВЛЕНО: text вместо text()
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