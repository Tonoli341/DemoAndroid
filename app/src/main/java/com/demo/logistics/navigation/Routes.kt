package com.demo.logistics.navigation

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object Home : Screen("home")
    data object ShiftStep1 : Screen("shift_step_1")
    data object ShiftStep2 : Screen("shift_step_2")
    data object History : Screen("history")
}
