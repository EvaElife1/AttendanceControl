package com.example.attendancecontrol.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.attendancecontrol.data.entity.StudentEntity

/** DAO для работы с таблицей студентов */
@Dao
interface StudentDao {

    /** Добавляет нового студента в таблицу */
    @Insert
    suspend fun insert(student: StudentEntity)

    /** Возвращает всех студентов, отсортированных по группе, фамилии и имени */
    @Query("SELECT * FROM students ORDER BY groupName ASC, lastName ASC, firstName ASC")
    suspend fun getAll(): List<StudentEntity>

    /** Удаляет студента из таблицы */
    @Delete
    suspend fun delete(student: StudentEntity)

    /** Возвращает студентов конкретной группы, отсортированных по фамилии */
    @Query("SELECT * FROM students WHERE groupName = :groupName ORDER BY lastName ASC")
    suspend fun getByGroup(groupName: String): List<StudentEntity>
}
