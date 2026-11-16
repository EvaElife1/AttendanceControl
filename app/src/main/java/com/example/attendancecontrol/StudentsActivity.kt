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

    // Временное хранение в памяти (Room подключается в спринте 4)
    private val students = mutableListOf<Student>()
    private var nextId = 1
    private lateinit var adapter: StudentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        val etLastName = findViewById<EditText>(R.id.etLastName)
        val etFirstName = findViewById<EditText>(R.id.etFirstName)
        val etGroup = findViewById<EditText>(R.id.etGroup)
        val btnAdd = findViewById<Button>(R.id.btnAddStudent)
        val rvStudents = findViewById<RecyclerView>(R.id.rvStudents)

        adapter = StudentAdapter(students) { student -> confirmDelete(student) }
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        btnAdd.setOnClickListener {
            val lastName = etLastName.text.toString().trim()
            val firstName = etFirstName.text.toString().trim()
            val groupName = etGroup.text.toString().trim()

            // Проверка обязательных полей: пустые и состоящие из пробелов отклоняются
            var isValid = true
            if (lastName.isEmpty()) {
                etLastName.error = "Введите фамилию"
                isValid = false
            }
            if (firstName.isEmpty()) {
                etFirstName.error = "Введите имя"
                isValid = false
            }
            if (groupName.isEmpty()) {
                etGroup.error = "Введите группу"
                isValid = false
            }
            if (!isValid) return@setOnClickListener

            students.add(Student(nextId++, firstName, lastName, groupName))
            adapter.notifyDataSetChanged()

            etLastName.text.clear()
            etFirstName.text.clear()
            etGroup.text.clear()
            Toast.makeText(this, "Студент добавлен", Toast.LENGTH_SHORT).show()
        }
    }

    private fun confirmDelete(student: Student) {
        AlertDialog.Builder(this)
            .setTitle("Удаление студента")
            .setMessage("Удалить студента ${student.lastName} ${student.firstName}?")
            .setPositiveButton("Удалить") { _, _ ->
                students.remove(student)
                adapter.notifyDataSetChanged()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }
}
