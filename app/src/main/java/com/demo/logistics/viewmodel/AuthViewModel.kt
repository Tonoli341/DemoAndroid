package com.demo.logistics.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class AuthViewModel : ViewModel() {
    var username by mutableStateOf("")
        private set
    var pin by mutableStateOf("")
        private set
    var isLoggedIn by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun updateUsername(value: String) {
        username = value
        errorMessage = null
    }

    fun updatePin(value: String) {
        pin = value
        errorMessage = null
    }

    fun login() {
        if (username.isNotBlank() && pin.isNotBlank()) {
            isLoggedIn = true
            errorMessage = null
        } else {
            errorMessage = "Inserisci username e PIN"
        }
    }

    fun logout() {
        isLoggedIn = false
        username = ""
        pin = ""
        errorMessage = null
    }
}
