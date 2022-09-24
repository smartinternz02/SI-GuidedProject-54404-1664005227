package dev.jahidhasanco.groceryapp.domain.viewmodel

import androidx.lifecycle.ViewModel
import dev.jahidhasanco.groceryapp.data.local.model.GroceryItems
import dev.jahidhasanco.groceryapp.domain.repository.GroceryRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GroceryViewModel (private val repository: GroceryRepository) : ViewModel() {

    fun insert(items: GroceryItems) = GlobalScope.launch {
        repository.insert(items)
    }


    fun delete(items: GroceryItems) = GlobalScope.launch {
        repository.delete(items)
    }

    fun getAllGroceryItems() = repository.getAllItems()

}