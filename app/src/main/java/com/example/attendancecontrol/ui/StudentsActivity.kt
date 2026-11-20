package com.example.attendancecontrol.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
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
import com.example.attendancecontrol.util.StudentValidator
import com.example.attendancecontrol.util.applyNavigationBarPadding
import com.example.attendancecontrol.util.applyStatusBarPadding
import kotlinx.coroutines.launch

/**
 * Экран управления студентами.
 * Администратор: может добавлять и удалять студентов.
 * Учитель: только просмотр списка.
 */
class StudentsActivity : AppCompatActivity() {

    private lateinit var etLastName: EditText
    private lateinit var etFirstName: EditText
    private lateinit var etGroup: EditText
    private lateinit var rvStudents: RecyclerView
    private lateinit var tvStudentsCount: TextView

    private val studentsList = mutableListOf<StudentEntity>()
    private lateinit var adapter: StudentAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_students)

        // Определяем роль: true = ADMIN, false = TEACHER
        val isAdmin = intent.getStringExtra(LoginActivity.EXTRA_USER_ROLE) == "ADMIN"

        // Отступы под статус-бар и навигационную панель
        findViewById<View>(R.id.headerLayout)?.let { applyStatusBarPadding(it) }
        findViewById<View>(R.id.rvStudents)?.let { applyNavigationBarPadding(it) }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        db = AppDatabase.getDatabase(this)

        etLastName      = findViewById(R.id.etLastName)
        etFirstName     = findViewById(R.id.etFirstName)
        etGroup         = findViewById(R.id.etGroup)
        rvStudents      = findViewById(R.id.rvStudents)
        tvStudentsCount = findViewById(R.id.tvStudentsCount)

        // Форма добавления и кнопка — скрыты для учителя
        val formContainer = findViewById<View>(R.id.formContainer)
        formContainer.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Адаптер знает роль — показывает/скрывает кнопку «Удалить»
        adapter = StudentAdapter(
            students = studentsList,
            isAdmin = isAdmin,
            onDeleteClick = { student -> showDeleteDialog(student) }
        )
        rvStudents.layoutManager = LinearLayoutManager(this)
        rvStudents.adapter = adapter

        if (isAdmin) {
            findViewById<Button>(R.id.btnAddStudent).setOnClickListener { addStudent() }
        }

        loadStudents()
    }

    /** Загружает всех студентов из БД и обновляет список и счётчик */
    private fun loadStudents() {
        lifecycleScope.launch {
            val entities = db.studentDao().getAll()
            studentsList.clear()
            studentsList.addAll(entities)
            adapter.notifyDataSetChanged()
            tvStudentsCount.text = "Студентов: ${studentsList.size}"
        }
    }

    /** Валидирует поля формы и добавляет нового студента в БД (только для админа) */
    private fun addStudent() {
        val lastName  = etLastName.text.toString().trim()
        val firstName = etFirstName.text.toString().trim()
        val group     = etGroup.text.toString().trim()

        val validationError = StudentValidator.validate(lastName, firstName, group)
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show()
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

    /** Показывает диалог подтверждения удаления студента (только для админа) */
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
