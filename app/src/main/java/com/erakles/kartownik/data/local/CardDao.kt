package com.erakles.kartownik.data.local

import androidx.room.*
import com.erakles.kartownik.data.model.CardEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardDao {

    @Query("SELECT * FROM loyalty_cards ORDER BY isFavorite DESC, createdAt DESC")
    fun getAllCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM loyalty_cards WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteCards(): Flow<List<CardEntity>>

    @Query("SELECT * FROM loyalty_cards WHERE id = :id LIMIT 1")
    suspend fun getCardById(id: Long): CardEntity?

    @Query("SELECT * FROM loyalty_cards WHERE storeName LIKE '%' || :query || '%' OR note LIKE '%' || :query || '%' ORDER BY isFavorite DESC, createdAt DESC")
    fun searchCards(query: String): Flow<List<CardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCard(card: CardEntity): Long

    @Update
    suspend fun updateCard(card: CardEntity)

    @Delete
    suspend fun deleteCard(card: CardEntity)

    @Query("SELECT COUNT(*) FROM loyalty_cards")
    suspend fun getCardCount(): Int
}
