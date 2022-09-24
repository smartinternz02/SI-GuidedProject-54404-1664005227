package dev.jahidhasanco.groceryapp.data.local.db

import androidx.lifecycle.LiveData
import androidx.room.*
import dev.jahidhasanco.groceryapp.data.local.model.GroceryItems

@Dao
interface GroceryDao {

    @Query("SELECT * FROM grocery_items")
    fun getAllGroceryItems(): LiveData<List<GroceryItems>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: GroceryItems)

    @Delete
    suspend fun delete(item: GroceryItems)

}