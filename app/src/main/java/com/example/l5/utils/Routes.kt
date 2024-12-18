package com.example.l5.utils

sealed class Routes(val route: String) {
    object MAIN_SCREEN : Routes("main_screen")
    object INFO_SCREEN : Routes("info_screen")
}