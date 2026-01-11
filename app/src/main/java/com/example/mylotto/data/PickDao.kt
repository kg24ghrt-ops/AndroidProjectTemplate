package com.example.mylotto.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PickDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPick(entry: PickEntry)

    @Query("SELECT * FROM pick_entries ORDER BY timestamp DESC")
    fun getAllPicks(): Flow<List<PickEntry>>

    @Query("SELECT * FROM pick_entries WHERE personName = :name ORDER BY timestamp DESC")
    fun getPicksByPerson(name: String): Flow<List<PickEntry>>

    @Query("SELECT * FROM pick_entries WHERE categoryCode = :cat ORDER BY timestamp DESC")
    fun getPicksByCategory(cat: String): Flow<List<PickEntry>>

    @Query("SELECT * FROM pick_entries WHERE lotteryType = :type ORDER BY timestamp DESC")
    fun getPicksByType(type: String): Flow<List<PickEntry>>

    @Delete
    suspend fun deletePick(entry: PickEntry)

    @Query("DELETE FROM pick_entries")
    suspend fun deleteAll()
}