package com.demo.logistics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.demo.logistics.navigation.Screen
import com.demo.logistics.ui.screens.HistoryScreen
import com.demo.logistics.ui.screens.HomeScreen
import com.demo.logistics.ui.screens.LoginScreen
import com.demo.logistics.ui.screens.ShiftStep1Screen
import com.demo.logistics.ui.screens.ShiftStep2Screen
import com.demo.logistics.ui.theme.LogisticsDemoTheme
import com.demo.logistics.viewmodel.AuthViewModel
import com.demo.logistics.viewmodel.WorkDayViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LogisticsDemoTheme {
                Surface {
                    LogisticsApp()
                }
            }
        }
    }
}

@Composable
fun LogisticsApp() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val workDayViewModel: WorkDayViewModel = viewModel()

    LaunchedEffect(authViewModel.isLoggedIn) {
        if (!authViewModel.isLoggedIn) {
            navController.navigate(Screen.Login.route) {
                popUpTo(0)
            }
        }
    }

    Scaffold { padding ->
        NavGraph(
            navController = navController,
            authViewModel = authViewModel,
            workDayViewModel = workDayViewModel,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    workDayViewModel: WorkDayViewModel,
    modifier: Modifier = Modifier
) {
    NavHost(navController = navController, startDestination = Screen.Login.route, modifier = modifier) {
        composable(Screen.Login.route) {
            LoginScreen(viewModel = authViewModel) {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        }
        composable(Screen.Home.route) {
            HomeScreen(
                onInsertToday = {
                    workDayViewModel.resetForToday()
                    navController.navigate(Screen.ShiftStep1.route)
                },
                onHistory = { navController.navigate(Screen.History.route) },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0)
                    }
                }
            )
        }
        composable(Screen.ShiftStep1.route) {
            ShiftStep1Screen(
                viewModel = workDayViewModel,
                onBack = { navController.popBackStack() },
                onNext = { navController.navigate(Screen.ShiftStep2.route) }
            )
        }
        composable(Screen.ShiftStep2.route) {
            ShiftStep2Screen(
                viewModel = workDayViewModel,
                onConfirm = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Home.route) { inclusive = true }
                    }
                },
                onAddSlice = { workDayViewModel.addSlice() }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(viewModel = workDayViewModel, onBack = { navController.popBackStack() })
        }
    }
}
