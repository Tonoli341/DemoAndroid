package com.demo.logistics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.logistics.ui.components.BigButton
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onInsertToday: () -> Unit,
    onHistory: () -> Unit,
    onLogout: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val todayText = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
            Text(text = "Benvenuto", fontSize = 22.sp)
            Text(text = "Oggi: $todayText", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(32.dp))
            BigButton(text = "Inserisci giornata di oggi") { onInsertToday() }
            Spacer(modifier = Modifier.height(16.dp))
            BigButton(text = "Storico settimana") { onHistory() }
            Spacer(modifier = Modifier.height(16.dp))
            BigButton(text = "Esci") { onLogout() }
        }
    }
}
