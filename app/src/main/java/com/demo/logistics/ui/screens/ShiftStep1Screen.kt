package com.demo.logistics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.logistics.ui.components.BigButton
import com.demo.logistics.viewmodel.WorkDayViewModel

@Composable
fun ShiftStep1Screen(
    viewModel: WorkDayViewModel,
    onBack: () -> Unit,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Step 1 – Turno", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Inserisci orari del turno", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = viewModel.startTimeText,
            onValueChange = { viewModel.updateStartTime(it) },
            label = { Text("Ora entrata (HH:MM)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = viewModel.endTimeText,
            onValueChange = { viewModel.updateEndTime(it) },
            label = { Text("Ora uscita (HH:MM)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = viewModel.breakMinutesText,
            onValueChange = { viewModel.updateBreakMinutes(it) },
            label = { Text("Pausa (minuti)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(keyboardType = KeyboardType.Number)
        )
        Spacer(modifier = Modifier.height(20.dp))
        viewModel.stepError?.let {
            Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            BigButton(text = "Indietro", modifier = Modifier.weight(1f)) { onBack() }
            BigButton(text = "Avanti", modifier = Modifier.weight(1f)) {
                if (viewModel.validateStep1()) onNext()
            }
        }
    }
}
