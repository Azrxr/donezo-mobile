package com.jasawira.donezo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.jasawira.donezo.data.local.entity.CardEntity
import com.jasawira.donezo.data.local.entity.CardWithItems
import com.jasawira.donezo.data.local.entity.ChecklistItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO untuk CARD
 */
@Dao
interface CardDao {
    // CREATE
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(card: CardEntity): Long

    // READ
    @Query("SELECT * FROM cards ORDER BY position ASC, createdAt DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE categoryId = :categoryId ORDER BY position ASC")
    fun getCardsByCategory(categoryId: String): Flow<List<CardEntity>>

    @Query("SELECT * FROM cards WHERE id = :id")
    suspend fun getCardById(id: String): CardEntity?

    // READ dengan items (nested)
    @Transaction
    @Query("SELECT * FROM cards WHERE id = :cardId")
    suspend fun getCardWithItems(cardId: String): CardWithItems?

    // UPDATE
    @Update
    suspend fun update(card: CardEntity): Int

    @Query("UPDATE cards SET position = :position WHERE id = :cardId")
    suspend fun updatePosition(cardId: String, position: Int): Int

    // DELETE
    @Delete
    suspend fun delete(card: CardEntity): Int

    @Query("DELETE FROM cards WHERE id = :id")
    suspend fun deleteById(id: String): Int

    // Search
    @Query("SELECT * FROM cards WHERE name LIKE '%' || :query || '%' ORDER BY position ASC")
    suspend fun searchCards(query: String): List<CardEntity>
}