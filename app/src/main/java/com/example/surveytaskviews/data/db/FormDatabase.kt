package com.example.surveytaskviews.data.db

import androidx.room.Database
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

// 1. Entity: Defines the table for our submitted answers
@Entity(tableName = "submitted_forms")
data class SubmittedForm(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val answersJson: String // We will store the map of answers as a JSON string
)

// 2. DAO: Defines the database operations
@Dao
interface FormDao {
    @Insert
    suspend fun insertForm(form: SubmittedForm)

    @Query("SELECT * FROM submitted_forms ORDER BY id DESC")
    fun getAllSubmittedForms(): Flow<List<SubmittedForm>>
}

// 3. Database: The main database holder
@Database(entities = [SubmittedForm::class], version = 1, exportSchema = false)
abstract class FormDatabase : RoomDatabase() {
    abstract fun formDao(): FormDao
}