package com.example.attendancecontrol.data.db

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.attendancecontrol.data.entity.AttendanceEntity
import com.example.attendancecontrol.data.entity.StudentEntity

// ──────────────────────────────────────────────────────────────
// Вспомогательные data class-ы для запросов
// ──────────────────────────────────────────────────────────────

/** Результат агрегирующего запроса: статус и его количество */
data class StatusCount(
    val status: String,
    val count: Int
)

/** JOIN-результат: запись посещаемости с данными студента (ФИО, группа) */
data class AttendanceWithStudent(
    val id: Int,
    val studentId: Int,
    val lessonDate: String,
    val subject: String,
    val status: String,
    val firstName: String,
    val lastName: String,
    val groupName: String
)

// ──────────────────────────────────────────────────────────────
// DAO посещаемости
// ──────────────────────────────────────────────────────────────

@Dao
interface AttendanceDao {

    /** Вставляет список записей посещаемости в таблицу */
    @Insert
    suspend fun insertAll(attendances: List<AttendanceEntity>)

    /** Возвращает все записи посещаемости без фильтрации */
    @Query("SELECT * FROM attendance")
    suspend fun getAll(): List<AttendanceEntity>

    /** Возвращает историю посещаемости конкретного студента, сортировка по дате убывания */
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY lessonDate DESC")
    suspend fun getHistoryForStudent(studentId: Int): List<AttendanceEntity>

    /** Возвращает количество записей для каждого статуса (для сводных карточек) */
    @Query("SELECT status, COUNT(*) as count FROM attendance GROUP BY status")
    suspend fun getStatusCounts(): List<StatusCount>

    /** Возвращает все записи с JOIN-данными студента — ФИО и группой (для экрана статистики) */
    @Query("""
        SELECT a.id, a.studentId, a.lessonDate, a.subject, a.status,
               s.firstName, s.lastName, s.groupName
        FROM attendance a
        INNER JOIN students s ON a.studentId = s.id
        ORDER BY a.lessonDate DESC
    """)
    suspend fun getAllWithStudent(): List<AttendanceWithStudent>

    /** Возвращает список уникальных групп из записей посещаемости (для фильтра-спиннера) */
    @Query("SELECT DISTINCT s.groupName FROM attendance a INNER JOIN students s ON a.studentId = s.id ORDER BY s.groupName ASC")
    suspend fun getDistinctGroups(): List<String>
}

// ──────────────────────────────────────────────────────────────
// База данных Room — Singleton
// ──────────────────────────────────────────────────────────────

@Database(entities = [StudentEntity::class, AttendanceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /** Возвращает единственный экземпляр базы данных (Singleton) */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "attendance_database"
                ).fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
