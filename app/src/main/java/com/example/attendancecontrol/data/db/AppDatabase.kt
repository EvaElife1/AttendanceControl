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
// DAO посещаемости (небольшой — хранится здесь)
// ──────────────────────────────────────────────
@Dao
interface AttendanceDao {
    @Insert
    suspend fun insertAll(attendances: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance")
    suspend fun getAll(): List<AttendanceEntity>
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
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
