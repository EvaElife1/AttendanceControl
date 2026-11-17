package com.example.attendancecontrol

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AttendanceActivity : AppCompatActivity() {

    // ИЗМЕНЕНО на StudentEntity
    private val studentsList = mutableListOf<StudentEntity>()
    private lateinit var adapter: AttendanceAdapter
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        db = AppDatabase.getDatabase(this)

        val etSubject = findViewById<EditText>(R.id.etSubject)
        val etDate = findViewById<EditText>(R.id.etDate)
        val btnSave = findViewById<Button>(R.id.btnSaveAttendance)
        val rvAttendance = findViewById<RecyclerView>(R.id.rvAttendance)

        adapter = AttendanceAdapter(studentsList)
        rvAttendance.layoutManager = LinearLayoutManager(this)
        rvAttendance.adapter = adapter

        loadStudentsFromDb()

        btnSave.setOnClickListener {
            val subject = etSubject.text.toString().trim()
            val date = etDate.text.toString().trim()

            if (subject.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Заполните дисциплину и дату", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isValidDate(date)) {
                etDate.error = "Введите дату в формате ГГГГ-ММ-ДД"
                return@setOnClickListener
            }

            val selectedStatuses = adapter.getSelectedStatuses()
            if (selectedStatuses.size < studentsList.size) {
                Toast.makeText(this, "Отметьте статус для всех студентов", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveAttendanceToDb(subject, date, selectedStatuses)
        }
    }

    private fun loadStudentsFromDb() {
        lifecycleScope.launch {
            val entities = db.studentDao().getAll()
            studentsList.clear()
            studentsList.addAll(entities) // Берем напрямую из БД
            adapter.notifyDataSetChanged()
        }
    }

    private fun saveAttendanceToDb(subject: String, date: String, statuses: Map<Int, String>) {
        lifecycleScope.launch {
            val recordsToInsert = mutableListOf<AttendanceEntity>()
            for ((index, status) in statuses) {
                val student = studentsList[index]
                recordsToInsert.add(
                    AttendanceEntity(
                        studentId = student.id,
                        lessonDate = date,
                        subject = subject,
                        status = status
                    )
                )
            }
            db.attendanceDao().insertAll(recordsToInsert)
            Toast.makeText(this@AttendanceActivity, "Посещаемость сохранена", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidDate(dateStr: String): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        sdf.isLenient = false
        return try {
            sdf.parse(dateStr)
            true
        } catch (e: ParseException) {
            false
        }
    }
}