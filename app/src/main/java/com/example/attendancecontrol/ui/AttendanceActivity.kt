package com.example.attendancecontrol.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancecontrol.R
import com.example.attendancecontrol.data.db.AppDatabase
import com.example.attendancecontrol.data.entity.AttendanceEntity
import com.example.attendancecontrol.data.entity.StudentEntity
import com.example.attendancecontrol.ui.adapter.AttendanceAdapter
import com.example.attendancecontrol.util.applyNavigationBarPadding
import com.example.attendancecontrol.util.applyStatusBarPadding
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class AttendanceActivity : AppCompatActivity() {

    private val studentsList = mutableListOf<StudentEntity>()
    private lateinit var adapter: AttendanceAdapter
    private lateinit var db: AppDatabase
    private lateinit var tvStudentsCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)

        findViewById<View>(R.id.headerLayout)?.let { applyStatusBarPadding(it) }
        findViewById<View>(R.id.rvAttendance)?.let { applyNavigationBarPadding(it) }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        db = AppDatabase.getDatabase(this)

        val etSubject = findViewById<EditText>(R.id.etSubject)
        val etDate = findViewById<EditText>(R.id.etDate)
        val btnSave = findViewById<Button>(R.id.btnSaveAttendance)
        val rvAttendance = findViewById<RecyclerView>(R.id.rvAttendance)
        tvStudentsCount = findViewById(R.id.tvStudentsCount)

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
            studentsList.addAll(entities)
            adapter.notifyDataSetChanged()
            tvStudentsCount.text = "Студентов: ${studentsList.size}"
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
