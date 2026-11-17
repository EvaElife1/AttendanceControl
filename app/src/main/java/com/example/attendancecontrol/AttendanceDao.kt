package com.example.attendancecontrol

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AttendanceDao {

    @Insert
    suspend fun insert(attendance: AttendanceEntity)

    @Query("SELECT * FROM attendance WHERE studentId = :studentId")
    suspend fun getByStudent(studentId: Int): List<AttendanceEntity>

    @Query("SELECT * FROM attendance WHERE lessonDate = :date AND subject = :subject")
    suspend fun getByLesson(date: String, subject: String): List<AttendanceEntity>

    @Query("SELECT * FROM attendance")
    suspend fun getAll(): List<AttendanceEntity>
}