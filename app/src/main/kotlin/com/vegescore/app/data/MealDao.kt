package com.vegescore.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("SELECT * FROM meal_entries ORDER BY timestampMs DESC")
    fun getAllEntries(): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries WHERE timestampMs >= :fromMs ORDER BY timestampMs DESC")
    fun getEntriesSince(fromMs: Long): Flow<List<MealEntry>>

    @Query("SELECT * FROM meal_entries ORDER BY timestampMs DESC")
    suspend fun getAllEntriesOnce(): List<MealEntry>

    @Insert
    suspend fun insert(entry: MealEntry): Long

    @Update
    suspend fun update(entry: MealEntry)

    @Delete
    suspend fun delete(entry: MealEntry)

    @Query("DELETE FROM meal_entries")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<MealEntry>)
}
