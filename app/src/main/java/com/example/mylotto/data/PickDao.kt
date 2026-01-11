package com.example.mylotto.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PickDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPick(entry: PickEntry)

    @Query("SELECT * FROM pick_entries ORDER BY timestamp DESC")
    fun getAllPicks(): Flow<List<PickEntry>>

    @Query("DELETE FROM pick_entries")
    suspend fun deleteAll()

    @Delete
    suspend fun deletePick(entry: PickEntry)
}