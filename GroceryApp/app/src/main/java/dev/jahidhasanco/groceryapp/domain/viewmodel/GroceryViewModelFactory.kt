package dev.jahidhasanco.groceryapp.domain.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dev.jahidhasanco.groceryapp.domain.repository.GroceryRepository

class GroceryViewModelFactory (private val repository: GroceryRepository) : ViewModelProvider.NewInstanceFactory() {

    override fun <T: ViewModel> create(modelClass: Class<T>):T{
        return GroceryViewModel(repository) as T
    }
}