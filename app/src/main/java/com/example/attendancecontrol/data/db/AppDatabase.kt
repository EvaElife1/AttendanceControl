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

// ──────────────────────────────────────────────
// Data class for status count aggregation
// ──────────────────────────────────────────────
data class StatusCount(
    val status: String,
    val count: Int
)

// Data class combining attendance + student info
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

// ──────────────────────────────────────────────
// DAO посещаемости (небольшой — хранится здесь)
// ──────────────────────────────────────────────
@Dao
interface AttendanceDao {
    @Insert
    suspend fun insertAll(attendances: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance")
    suspend fun getAll(): List<AttendanceEntity>

    // Get attendance history for a specific student, ordered by date descending
    @Query("SELECT * FROM attendance WHERE studentId = :studentId ORDER BY lessonDate DESC")
    suspend fun getHistoryForStudent(studentId: Int): List<AttendanceEntity>

    // Get count of each attendance status (e.g., Present, Absent)
    @Query("SELECT status, COUNT(*) as count FROM attendance GROUP BY status")
    suspend fun getStatusCounts(): List<StatusCount>

    // Join attendance with students to get full info
    @Query("""
        SELECT a.id, a.studentId, a.lessonDate, a.subject, a.status,
               s.firstName, s.lastName, s.groupName
        FROM attendance a
        INNER JOIN students s ON a.studentId = s.id
        ORDER BY a.lessonDate DESC
    """)
    suspend fun getAllWithStudent(): List<AttendanceWithStudent>

    // Get distinct group names for filter
    @Query("SELECT DISTINCT s.groupName FROM attendance a INNER JOIN students s ON a.studentId = s.id ORDER BY s.groupName ASC")
    suspend fun getDistinctGroups(): List<String>
}

// ──────────────────────────────────────────────
// База данных Room
// ──────────────────────────────────────────────
@Database(entities = [StudentEntity::class, AttendanceEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
    abstract fun attendanceDao(): AttendanceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

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
