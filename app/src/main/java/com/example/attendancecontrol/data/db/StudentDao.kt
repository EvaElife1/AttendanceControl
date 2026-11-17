package com.example.attendancecontrol.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.attendancecontrol.data.entity.StudentEntity

@Dao
interface StudentDao {

    @Insert
    suspend fun insert(student: StudentEntity)

    @Query("SELECT * FROM students ORDER BY groupName ASC, lastName ASC, firstName ASC")
    suspend fun getAll(): List<StudentEntity>

    @Delete
    suspend fun delete(student: StudentEntity)

    @Query("SELECT * FROM students WHERE groupName = :groupName ORDER BY lastName ASC")
    suspend fun getByGroup(groupName: String): List<StudentEntity>
}
