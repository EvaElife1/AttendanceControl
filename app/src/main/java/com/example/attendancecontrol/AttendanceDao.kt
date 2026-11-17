package com.example.attendancecontrol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AttendanceDao {
    @Insert
    suspend fun insertAll(attendances: List<AttendanceEntity>)

    @Query("SELECT * FROM attendance")
    suspend fun getAll(): List<AttendanceEntity>
}