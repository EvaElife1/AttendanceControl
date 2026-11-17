package com.example.attendancecontrol

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StudentDao {

    @Insert
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students ORDER BY lastName ASC")
    suspend fun getAll(): List<StudentEntity>

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("SELECT * FROM students WHERE groupName = :groupName ORDER BY lastName ASC")
    suspend fun getByGroup(groupName: String): List<StudentEntity>
}