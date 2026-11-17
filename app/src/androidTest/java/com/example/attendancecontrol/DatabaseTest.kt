package com.example.attendancecontrol

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class DatabaseTest {

    private lateinit var db: AppDatabase
    private lateinit var studentDao: StudentDao

    @Before
    fun createDb() {
        // Создаем тестовую базу данных в оперативной памяти (данные удалятся после теста)
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries() // Разрешаем в тестах, чтобы не усложнять корутины
            .build()
        studentDao = db.studentDao()
    }

    @After
    fun closeDb() {
        db.close()
    }

    /**
     * Тест-кейс 1: Добавление и чтение (Сохранность данных)
     */
    @Test
    fun tc09_insertAndReadStudent() = runBlocking {
        val student = StudentEntity(firstName = "Иван", lastName = "Иванов", groupName = "ИС-21")
        studentDao.insert(student)

        val students = studentDao.getAll()
        assertEquals(1, students.size)
        assertEquals("Иванов", students[0].lastName)
        assertEquals("ИС-21", students[0].groupName)
    }

    /**
     * Тест-кейс 2: Пустая база данных при первом запуске
     */
    @Test
    fun tc12_emptyDatabase_returnsEmptyList() = runBlocking {
        val students = studentDao.getAll()
        assertTrue("Список студентов должен быть пустым", students.isEmpty())
    }

    /**
     * Тест-кейс 3: Удаление записи
     */
    @Test
    fun tc13_deleteStudent() = runBlocking {
        val student = StudentEntity(firstName = "Петр", lastName = "Петров", groupName = "ИС-22")
        studentDao.insert(student)

        // Проверяем, что добавился
        val addedStudent = studentDao.getAll().first()

        // Удаляем
        studentDao.delete(addedStudent)

        // Проверяем, что список пуст
        val students = studentDao.getAll()
        assertTrue("После удаления список должен быть пуст", students.isEmpty())
    }
}