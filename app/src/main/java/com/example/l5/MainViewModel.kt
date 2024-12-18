package com.example.l5

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.lifecycle.viewModelScope
import com.example.l5.db.MainDb
import com.example.l5.utils.ListItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@HiltViewModel
class MainViewModel @Inject constructor(val mainDb: MainDb) : ViewModel() {
    val mainList: MutableState<List<ListItem>> = mutableStateOf(listOf())
    val favorites: MutableState<List<ListItem>> = mutableStateOf(listOf())
    private var job: Job? = null

    fun getAllItemsByCategory(cat: String) {
        job?.cancel()
        job = viewModelScope.launch {
            mainDb.dao.getAllItemsByCategory(cat).collect { list ->
                mainList.value = list
                updateFavorites()
            }
        }
    }

    fun getFavorites() {
        job?.cancel()
        job = viewModelScope.launch {
            mainDb.dao.getFavorites().collect { list ->
                favorites.value = list
            }
        }
    }

    fun toggleFavorite(item: ListItem) = viewModelScope.launch {
        val updatedItem = item.copy(isfav = !item.isfav)
        mainDb.dao.insertItem(updatedItem)
        updateFavorites()
        getAllItemsByCategory(item.category)
    }

    fun updateFavorites() = viewModelScope.launch {
        mainDb.dao.getFavorites().collect { list ->
            favorites.value = list
        }
    }
}

