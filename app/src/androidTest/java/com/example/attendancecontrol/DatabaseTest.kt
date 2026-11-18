package com.example.attendancecontrol

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.attendancecontrol.data.db.AppDatabase
import com.example.attendancecontrol.data.entity.AttendanceEntity
import com.example.attendancecontrol.data.entity.StudentEntity
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var studentDao: com.example.attendancecontrol.data.db.StudentDao
    private lateinit var attendanceDao: com.example.attendancecontrol.data.db.AttendanceDao

    @Before
    fun createDb() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        studentDao = db.studentDao()
        attendanceDao = db.attendanceDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    // ТЕСТ 4: Расчет статистики на подготовленных данных
    @Test
    fun tc04_statistics_calculatesCorrectly() = runBlocking {
        val s1 = StudentEntity(id = 1, firstName = "A", lastName = "A", groupName = "G1")
        studentDao.insert(s1)

        attendanceDao.insertAll(listOf(
            AttendanceEntity(studentId = 1, lessonDate = "2023-10-01", subject = "Math", status = "Присутствовал"),
            AttendanceEntity(studentId = 1, lessonDate = "2023-10-01", subject = "Math", status = "Отсутствовал"), // Та же пара
            AttendanceEntity(studentId = 1, lessonDate = "2023-10-02", subject = "Physics", status = "Опоздал")
        ))

        val counts = attendanceDao.getStatusCounts()
        val present = counts.find { it.status == "Присутствовал" }?.count ?: 0
        val absent = counts.find { it.status == "Отсутствовал" }?.count ?: 0
        val late = counts.find { it.status == "Опоздал" }?.count ?: 0

        assertEquals(1, present)
        assertEquals(1, absent)
        assertEquals(1, late)
    }

    // ТЕСТ 5: Фильтрация истории по студенту
    @Test
    fun tc05_historyFilter_byStudentId() = runBlocking {
        val s1 = StudentEntity(id = 1, firstName = "A", lastName = "A", groupName = "G1")
        val s2 = StudentEntity(id = 2, firstName = "B", lastName = "B", groupName = "G1")
        studentDao.insert(s1)
        studentDao.insert(s2)

        attendanceDao.insertAll(listOf(
            AttendanceEntity(studentId = 1, lessonDate = "2023-10-01", subject = "Math", status = "Присутствовал"),
            AttendanceEntity(studentId = 2, lessonDate = "2023-10-01", subject = "Math", status = "Отсутствовал")
        ))

        val history = attendanceDao.getHistoryForStudent(1)
        assertEquals(1, history.size)
        assertEquals(1, history[0].studentId)
    }

    // ТЕСТ 6: Пустая история (нет записей)
    @Test
    fun tc06_emptyHistory_returnsEmptyList() = runBlocking {
        val history = attendanceDao.getHistoryForStudent(99)
        assertTrue(history.isEmpty())
    }

    // ТЕСТ 7: Большой список (Стресс-тест БД)
    @Test
    fun tc07_largeDataset_insertsAndCountsCorrectly() = runBlocking {
        val students = (1..100).map { StudentEntity(firstName = "Name$it", lastName = "Last$it", groupName = "G1") }
        students.forEach { studentDao.insert(it) }
        val inserted = studentDao.getAll()

        val records = inserted.flatMap { student ->
            (1..10).map { AttendanceEntity(studentId = student.id, lessonDate = "2023-10-01", subject = "Subj$it", status = "Присутствовал") }
        }
        attendanceDao.insertAll(records)

        assertEquals(1000, attendanceDao.getAll().size)
    }
}