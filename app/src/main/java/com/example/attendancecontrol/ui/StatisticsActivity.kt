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

/**
 * Экран статистики посещаемости.
 * Показывает сводные карточки (присутствовал / отсутствовал / опоздал),
 * прогресс-бар посещаемости и список записей с фильтрацией по группе и статусу.
 */
class StatisticsActivity : AppCompatActivity() {

    // ── Views ──────────────────────────────────────────────────────────────────
    private lateinit var progressBar: ProgressBar       // крутилка во время загрузки
    private lateinit var layoutEmpty: View              // заглушка «нет данных»
    private lateinit var layoutContent: View            // основной контент

    private lateinit var tvPresentCount: TextView       // счётчик «Присутствовал»
    private lateinit var tvAbsentCount: TextView        // счётчик «Отсутствовал»
    private lateinit var tvLateCount: TextView          // счётчик «Опоздал»
    private lateinit var tvAttendancePct: TextView      // процент посещаемости
    private lateinit var tvFilteredCount: TextView      // «X из Y» отфильтровано
    private lateinit var progressAttendance: ProgressBar
    private lateinit var rvHistory: RecyclerView

    private lateinit var spinnerGroup: Spinner          // фильтр по группе
    private lateinit var spinnerStatus: Spinner         // фильтр по статусу

    private lateinit var db: AppDatabase
    private val historyAdapter = HistoryAdapter()

    // Кэш всех записей для клиентской фильтрации без повторных запросов к БД
    private var allRecords: List<AttendanceWithStudent> = emptyList()
    private var allStatusCounts: List<StatusCount> = emptyList()

    // Фиксированный список вариантов статуса для спиннера
    private val statusOptions = listOf("Все статусы", "Присутствовал", "Отсутствовал", "Опоздал")

    // ── Lifecycle ──────────────────────────────────────────────────────────────
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        // Отступы под статус-бар (шапка) и навигационную панель (контент / пустой стейт)
        findViewById<View>(R.id.headerLayout)?.let { applyStatusBarPadding(it) }
        findViewById<View>(R.id.contentContainer)?.let { applyNavigationBarPadding(it) }
        findViewById<View>(R.id.layoutEmpty)?.let { applyNavigationBarPadding(it) }

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // Привязка View по id
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

        // Настройка RecyclerView — вложенная прокрутка отключена (внутри ScrollView)
        rvHistory.layoutManager = LinearLayoutManager(this)
        rvHistory.adapter = historyAdapter
        rvHistory.isNestedScrollingEnabled = false

        // Статичный список статусов — устанавливается один раз
        val statusAdapter = ArrayAdapter(this, R.layout.item_spinner_compact, statusOptions)
        statusAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_compact)
        spinnerStatus.adapter = statusAdapter

        db = AppDatabase.getDatabase(this)
        loadStatistics()
    }

    // ── Загрузка данных ────────────────────────────────────────────────────────

    /**
     * Загружает все данные из БД, настраивает спиннер групп и подключает слушатели фильтров.
     * При пустой БД показывает экран-заглушку.
     */
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

                // Единый слушатель для обоих спиннеров — перефильтровывает список при выборе
                val filterListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                        applyFilters()
                    }
                    override fun onNothingSelected(parent: AdapterView<*>?) {}
                }
                spinnerGroup.onItemSelectedListener  = filterListener
                spinnerStatus.onItemSelectedListener = filterListener

                // Первичное применение фильтров (показывает «Все группы» / «Все статусы»)
                applyFilters()

            } catch (_: Exception) {
                showEmpty()
            }
        }
    }

    // ── Настройка спиннера ─────────────────────────────────────────────────────

    /** Заполняет спиннер групп: «Все группы» + уникальные группы из БД */
    private fun setupGroupSpinner(groups: List<String>) {
        val items = listOf("Все группы") + groups
        val adapter = ArrayAdapter(this, R.layout.item_spinner_compact, items)
        adapter.setDropDownViewResource(R.layout.item_spinner_dropdown_compact)
        spinnerGroup.adapter = adapter
    }

    // ── Фильтрация ─────────────────────────────────────────────────────────────

    /**
     * Применяет текущие значения спиннеров к кэшированному списку [allRecords].
     * Обновляет RecyclerView, счётчик записей и сводные карточки.
     */
    private fun applyFilters() {
        val selectedGroup  = spinnerGroup.selectedItem  as? String ?: "Все группы"
        val selectedStatus = spinnerStatus.selectedItem as? String ?: "Все статусы"

        val filtered = allRecords
            .filter { r ->
                (selectedGroup  == "Все группы"   || r.groupName == selectedGroup) &&
                (selectedStatus == "Все статусы"  || r.status    == selectedStatus)
            }
            .sortedByDescending { it.lessonDate }
            .take(100)

        historyAdapter.submitList(filtered)
        tvFilteredCount.text = "${filtered.size} из ${allRecords.size}"

        // Пересчитываем сводные карточки по отфильтрованному набору
        val filteredCounts = filtered
            .groupBy { it.status }
            .map { StatusCount(it.key, it.value.size) }
        bindStatusCards(filteredCounts)
    }

    // ── Привязка данных ────────────────────────────────────────────────────────

    /** Обновляет числа в сводных карточках и прогресс-бар посещаемости */
    private fun bindStatusCards(counts: List<StatusCount>) {
        val map = counts.associateBy { it.status }

        val present = map["Присутствовал"]?.count ?: 0
        val absent  = map["Отсутствовал"]?.count  ?: 0
        val late    = map["Опоздал"]?.count        ?: 0
        val total   = counts.sumOf { it.count }

        tvPresentCount.text = present.toString()
        tvAbsentCount.text  = absent.toString()
        tvLateCount.text    = late.toString()

        // Посещаемость = (присутствовал + опоздал) / всего × 100
        val attendedPct = if (total > 0) ((present + late) * 100) / total else 0
        tvAttendancePct.text = getString(R.string.stats_pct, attendedPct)
        progressAttendance.progress = attendedPct
    }

    // ── Переключение состояний экрана ──────────────────────────────────────────

    /** Показывает прогресс-бар загрузки, скрывает остальное */
    private fun showLoading() {
        progressBar.visibility   = View.VISIBLE
        layoutEmpty.visibility   = View.GONE
        layoutContent.visibility = View.GONE
    }

    /** Показывает заглушку «нет данных», скрывает остальное */
    private fun showEmpty() {
        progressBar.visibility   = View.GONE
        layoutEmpty.visibility   = View.VISIBLE
        layoutContent.visibility = View.GONE
    }

    /** Показывает основной контент, скрывает остальное */
    private fun showContent() {
        progressBar.visibility   = View.GONE
        layoutEmpty.visibility   = View.GONE
        layoutContent.visibility = View.VISIBLE
    }
}
