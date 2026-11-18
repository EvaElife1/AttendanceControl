package com.example.attendancecontrol.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancecontrol.R
import com.example.attendancecontrol.data.db.AppDatabase
import com.example.attendancecontrol.data.db.StatusCount
import com.example.attendancecontrol.ui.adapter.HistoryAdapter
import com.example.attendancecontrol.util.applyStatusBarPadding
import kotlinx.coroutines.launch

class StatisticsActivity : AppCompatActivity() {

    // ── Views ──────────────────────────────────────────────────────────────────
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutEmpty: View
    private lateinit var layoutContent: View

    private lateinit var tvPresentCount: TextView
    private lateinit var tvAbsentCount: TextView
    private lateinit var tvLateCount: TextView
    private lateinit var tvAttendancePct: TextView
    private lateinit var progressAttendance: ProgressBar
    private lateinit var rvHistory: RecyclerView

    private lateinit var db: AppDatabase
    private val historyAdapter = HistoryAdapter()

    // ── Lifecycle ──────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Status-bar padding
        findViewById<View>(R.id.headerLayout)?.let { applyStatusBarPadding(it) }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Bind views
        progressBar       = findViewById(R.id.progressBar)
        layoutEmpty       = findViewById(R.id.layoutEmpty)
        layoutContent     = findViewById(R.id.layoutContent)
        tvPresentCount    = findViewById(R.id.tvPresentCount)
        tvAbsentCount     = findViewById(R.id.tvAbsentCount)
        tvLateCount       = findViewById(R.id.tvLateCount)
        tvAttendancePct   = findViewById(R.id.tvAttendancePct)
        progressAttendance = findViewById(R.id.progressAttendance)
        rvHistory         = findViewById(R.id.rvHistory)

        // RecyclerView
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
        rvHistory.isNestedScrollingEnabled = false

        db = AppDatabase.getDatabase(this)
        loadStatistics()
    }

    // ── Data loading ───────────────────────────────────────────────────────────
    private fun loadStatistics() {
        showLoading()
        lifecycleScope.launch {
            try {
                val statusCounts: List<StatusCount> = db.attendanceDao().getStatusCounts()
                val history = db.attendanceDao().getAll()
                    .asSequence()
                    .sortedByDescending { it.lessonDate }
                    .take(30)
                    .toList()

                if (statusCounts.isEmpty()) {
                    showEmpty()
                    return@launch
                }

                bindStatusCards(statusCounts)
                historyAdapter.submitList(history)
                showContent()

            } catch (_: Exception) {
                showEmpty()
            }
        }
    }

    // ── Binding ────────────────────────────────────────────────────────────────
    private fun bindStatusCards(counts: List<StatusCount>) {
        val map = counts.associateBy { it.status }

        val present = map["Присутствовал"]?.count ?: 0
        val absent  = map["Отсутствовал"]?.count  ?: 0
        val late    = map["Опоздал"]?.count        ?: 0
        val total   = counts.sumOf { it.count }

        tvPresentCount.text = present.toString()
        tvAbsentCount.text  = absent.toString()
        tvLateCount.text    = late.toString()

        // Посещаемость = (присутствовал + опоздал) / всего
        val attendedPct = if (total > 0) ((present + late) * 100) / total else 0
        tvAttendancePct.text = getString(R.string.stats_pct, attendedPct)
        progressAttendance.progress = attendedPct
    }

    // ── Visibility helpers ─────────────────────────────────────────────────────
    private fun showLoading() {
        progressBar.visibility  = View.VISIBLE
        layoutEmpty.visibility  = View.GONE
        layoutContent.visibility = View.GONE
    }

    private fun showEmpty() {
        progressBar.visibility  = View.GONE
        layoutEmpty.visibility  = View.VISIBLE
        layoutContent.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility  = View.GONE
        layoutEmpty.visibility  = View.GONE
        layoutContent.visibility = View.VISIBLE
    }
}