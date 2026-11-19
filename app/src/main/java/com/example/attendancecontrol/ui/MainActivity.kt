package com.example.attendancecontrol.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancecontrol.R
import com.example.attendancecontrol.util.applySystemBarsPadding

/**
 * Главный экран приложения с навигацией по разделам и выходом из аккаунта.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<View>(R.id.mainRoot)?.let { applySystemBarsPadding(it) }

        findViewById<Button>(R.id.btnStudents).setOnClickListener {
            startActivity(Intent(this, StudentsActivity::class.java))
        }

        findViewById<Button>(R.id.btnAttendance).setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        findViewById<Button>(R.id.btnStatistics).setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }

        findViewById<Button>(R.id.btnLogout).setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }
}
