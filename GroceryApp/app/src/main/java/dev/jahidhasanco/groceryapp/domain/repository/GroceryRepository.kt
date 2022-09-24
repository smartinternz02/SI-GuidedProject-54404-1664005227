package dev.jahidhasanco.groceryapp.domain.repository

import dev.jahidhasanco.groceryapp.data.local.db.GroceryDatabase
import dev.jahidhasanco.groceryapp.data.local.model.GroceryItems

class GroceryRepository(private val db: GroceryDatabase) {

    suspend fun insert(items: GroceryItems) = db.getGroceryDao().insert(items)
    suspend fun delete(items: GroceryItems) = db.getGroceryDao().delete(items)

    fun getAllItems() = db.getGroceryDao().getAllGroceryItems()
}