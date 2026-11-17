package com.example.attendancecontrol.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.attendancecontrol.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStudents = findViewById<Button>(R.id.btnStudents)
        val btnAttendance = findViewById<Button>(R.id.btnAttendance)
        val btnStatistics = findViewById<Button>(R.id.btnStatistics)

        btnStudents.setOnClickListener {
            startActivity(Intent(this, StudentsActivity::class.java))
        }

        btnAttendance.setOnClickListener {
            startActivity(Intent(this, AttendanceActivity::class.java))
        }

        btnStatistics.setOnClickListener {
            startActivity(Intent(this, StatisticsActivity::class.java))
        }
    }
}
