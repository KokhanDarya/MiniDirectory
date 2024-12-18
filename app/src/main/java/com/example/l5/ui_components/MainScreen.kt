package com.example.l5.ui_components

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.l5.MainViewModel
import com.example.l5.utils.DrawerEvents
import com.example.l5.utils.ListItem
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(mainViewModel: MainViewModel = hiltViewModel(), onClick: (ListItem)->Unit) {
    var topBarTitle = rememberSaveable { mutableStateOf("Action") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val mainList = mainViewModel.mainList
    val favorites = mainViewModel.favorites
    LaunchedEffect(Unit) {
        mainViewModel.getAllItemsByCategory(topBarTitle.value)
    }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet() {
                DrawerMenu(){ event ->
                    when(event) {
                        is DrawerEvents.OnItemClick -> {
                            topBarTitle.value=event.title
                            mainViewModel.getAllItemsByCategory(event.title)
                        }
                    }
                    scope.launch {
                        drawerState.close()
                    }
                }
            }
        },
        content = {
            Scaffold(
                topBar = {
                    MainTopBar(title = topBarTitle.value, drawerState = drawerState)
                    {
                        topBarTitle.value = "Избранное"
                        mainViewModel.getFavorites()
                    }
                }
            )
            {innerPadding ->
                LazyColumn(modifier= Modifier.padding(innerPadding).fillMaxSize()){
                    items(if (topBarTitle.value == "Избранное") favorites.value else mainList.value) {item ->
                        MainListItem(item = item) {listItem -> onClick(listItem)}
                    }
                }
            }
        }
    )
}