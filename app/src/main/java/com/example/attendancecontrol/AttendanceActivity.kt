package com.example.attendancecontrol

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AttendanceActivity : AppCompatActivity() {

    // Имитация получения студентов из базы
    private val mockStudents = listOf(
        Student(1, "Иван", "Иванов", "ИС-21"),
        Student(2, "Петр", "Петров", "ИС-21"),
        Student(3, "Анна", "Сидорова", "ИС-21")
    )

    private val attendanceRecords = mutableListOf<Attendance>()
    private lateinit var adapter: AttendanceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        val etSubject = findViewById<EditText>(R.id.etSubject)
        val etDate = findViewById<EditText>(R.id.etDate)
        val btnSave = findViewById<Button>(R.id.btnSaveAttendance)
        val rvAttendance = findViewById<RecyclerView>(R.id.rvAttendance)

        adapter = AttendanceAdapter(mockStudents)
        rvAttendance.layoutManager = LinearLayoutManager(this)
        rvAttendance.adapter = adapter

        btnSave.setOnClickListener {
            val subject = etSubject.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (subject.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Заполните дисциплину и дату", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверка на правильность ввода даты (формат ГГГГ-ММ-ДД)
            if (!isValidDate(date)) {
                etDate.error = "Введите дату в формате ГГГГ-ММ-ДД"
                Toast.makeText(this, "Неверный формат даты", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Проверяем, что статус выбран для каждого студента
            val selectedStatuses = adapter.getSelectedStatuses()
            if (selectedStatuses.size < mockStudents.size) {
                Toast.makeText(this, "Отметьте статус для всех студентов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Сохраняем отметки
            attendanceRecords.clear()
            for ((index, status) in selectedStatuses) {
                val student = mockStudents[index]
                attendanceRecords.add(
                    Attendance(
                        studentId = student.id,
                        lessonDate = date,
                        subject = subject,
                        status = status
                    )
                )
            }

            Toast.makeText(this, "Посещаемость сохранена", Toast.LENGTH_SHORT).show()
        }
    }

    // Вспомогательная функция проверки даты
    private fun isValidDate(dateStr: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.isLenient = false // Строгая проверка (чтобы не проходило 2026-13-45)
        return try {
            sdf.parse(dateStr)
            true
        } catch (e: ParseException) {
            false
        }
    }
}