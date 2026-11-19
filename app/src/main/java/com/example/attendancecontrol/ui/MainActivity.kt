package com.example.attendancecontrol.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancecontrol.R
import com.example.attendancecontrol.util.applySystemBarsPadding

/**
 * Главный экран приложения.
 * Содержит три кнопки-навигации: Студенты, Отметить посещаемость, Статистика.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Добавляем отступы под статус-бар и навигационную панель
        findViewById<View>(R.id.mainRoot)?.let { applySystemBarsPadding(it) }

        val btnStudents   = findViewById<Button>(R.id.btnStudents)
        val btnAttendance = findViewById<Button>(R.id.btnAttendance)
        val btnStatistics = findViewById<Button>(R.id.btnStatistics)

        // Переход на экран управления студентами
        btnStudents.setOnClickListener {
            startActivity(Intent(this, StudentsActivity::class.java))
        }

        // Переход на экран отметки посещаемости
        btnAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        // Переход на экран статистики
        btnStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }
}
