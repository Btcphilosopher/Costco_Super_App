package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingItemDao {
    @Query("SELECT * FROM shopping_items ORDER BY isChecked ASC, id DESC")
    fun getAllItems(): Flow<List<ShoppingItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: ShoppingItem)

    @Update
    suspend fun updateItem(item: ShoppingItem)

    @Delete
    suspend fun deleteItem(item: ShoppingItem)

    @Query("DELETE FROM shopping_items")
    suspend fun clearAll()
}

@Dao
interface CostcoOrderDao {
    @Query("SELECT * FROM costco_orders ORDER BY id DESC")
    fun getAllOrders(): Flow<List<CostcoOrder>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: CostcoOrder)

    @Query("DELETE FROM costco_orders")
    suspend fun clearAll()
}

@Dao
interface SavedMembershipDao {
    @Query("SELECT * FROM saved_memberships WHERE id = 1 LIMIT 1")
    fun getMembership(): Flow<SavedMembership?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembership(membership: SavedMembership)
}
