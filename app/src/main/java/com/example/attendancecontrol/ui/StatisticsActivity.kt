package com.example.attendancecontrol.ui

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.attendancecontrol.R
import com.example.attendancecontrol.data.db.AppDatabase
import com.example.attendancecontrol.data.db.AttendanceWithStudent
import com.example.attendancecontrol.data.db.StatusCount
import com.example.attendancecontrol.ui.adapter.HistoryAdapter
import com.example.attendancecontrol.util.applyNavigationBarPadding
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
    private lateinit var tvFilteredCount: TextView
    private lateinit var progressAttendance: ProgressBar
    private lateinit var rvHistory: RecyclerView

    private lateinit var spinnerGroup: Spinner
    private lateinit var spinnerStatus: Spinner

    private lateinit var db: AppDatabase
    private val historyAdapter = HistoryAdapter()

    // Полный список для фильтрации
    private var allRecords: List<AttendanceWithStudent> = emptyList()
    private var allStatusCounts: List<StatusCount> = emptyList()

    private val statusOptions = listOf("Все статусы", "Присутствовал", "Отсутствовал", "Опоздал")

    // ── Lifecycle ──────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Status-bar and Navigation-bar padding
        findViewById<View>(R.id.headerLayout)?.let { applyStatusBarPadding(it) }
        findViewById<View>(R.id.contentContainer)?.let { applyNavigationBarPadding(it) }
        findViewById<View>(R.id.layoutEmpty)?.let { applyNavigationBarPadding(it) }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // Bind views
        progressBar        = findViewById(R.id.progressBar)
        layoutEmpty        = findViewById(R.id.layoutEmpty)
        layoutContent      = findViewById(R.id.layoutContent)
        tvPresentCount     = findViewById(R.id.tvPresentCount)
        tvAbsentCount      = findViewById(R.id.tvAbsentCount)
        tvLateCount        = findViewById(R.id.tvLateCount)
        tvAttendancePct    = findViewById(R.id.tvAttendancePct)
        tvFilteredCount    = findViewById(R.id.tvFilteredCount)
        progressAttendance = findViewById(R.id.progressAttendance)
        rvHistory          = findViewById(R.id.rvHistory)
        spinnerGroup       = findViewById(R.id.spinnerGroup)
        spinnerStatus      = findViewById(R.id.spinnerStatus)

        // RecyclerView
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
        rvHistory.isNestedScrollingEnabled = false

        // Spinner статуса (компактный адаптер)
        val statusAdapter = ArrayAdapter(this, R.layout.item_spinner_compact, statusOptions)
        statusAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_compact)
        spinnerStatus.adapter = statusAdapter

        db = AppDatabase.getDatabase(this)
        loadStatistics()
    }

    // ── Data loading ───────────────────────────────────────────────────────────
    private fun loadStatistics() {
        showLoading()
        lifecycleScope.launch {
            try {
                allStatusCounts = db.attendanceDao().getStatusCounts()
                allRecords      = db.attendanceDao().getAllWithStudent()
                val groups      = db.attendanceDao().getDistinctGroups()

                if (allRecords.isEmpty()) {
                    showEmpty()
                    return@launch
                }

                bindStatusCards(allStatusCounts)
                setupGroupSpinner(groups)
                showContent()

                // Применить фильтры при изменении спиннеров
                val filterListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                        applyFilters()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                spinnerGroup.onItemSelectedListener  = filterListener
                spinnerStatus.onItemSelectedListener = filterListener

                applyFilters()

            } catch (_: Exception) {
                showEmpty()
            }
        }
    }

    // ── Spinners ───────────────────────────────────────────────────────────────
    private fun setupGroupSpinner(groups: List<String>) {
        val items = listOf("Все группы") + groups
        val adapter = ArrayAdapter(this, R.layout.item_spinner_compact, items)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_compact)
        spinnerGroup.adapter = adapter
    }

    // ── Filtering ──────────────────────────────────────────────────────────────
    private fun applyFilters() {
        val selectedGroup  = spinnerGroup.selectedItem as? String ?: "Все группы"
        val selectedStatus = spinnerStatus.selectedItem as? String ?: "Все статусы"

        val filtered = allRecords
            .filter { r ->
                (selectedGroup == "Все группы" || r.groupName == selectedGroup) &&
                (selectedStatus == "Все статусы" || r.status == selectedStatus)
            }
            .sortedByDescending { it.lessonDate }
            .take(100)

        historyAdapter.submitList(filtered)
        tvFilteredCount.text = "${filtered.size} из ${allRecords.size}"

        // Пересчитать карточки-счётчики по отфильтрованным данным
        val filteredCounts = filtered
            .groupBy { it.status }
            .map { StatusCount(it.key, it.value.size) }
        bindStatusCards(filteredCounts)
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

        val attendedPct = if (total > 0) ((present + late) * 100) / total else 0
        tvAttendancePct.text = getString(R.string.stats_pct, attendedPct)
        progressAttendance.progress = attendedPct
    }

    // ── Visibility helpers ─────────────────────────────────────────────────────
    private fun showLoading() {
        progressBar.visibility   = View.VISIBLE
        layoutEmpty.visibility   = View.GONE
        layoutContent.visibility = View.GONE
    }

    private fun showEmpty() {
        progressBar.visibility   = View.GONE
        layoutEmpty.visibility   = View.VISIBLE
        layoutContent.visibility = View.GONE
    }

    private fun showContent() {
        progressBar.visibility   = View.GONE
        layoutEmpty.visibility   = View.GONE
        layoutContent.visibility = View.VISIBLE
    }
}
