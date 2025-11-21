package com.demo.logistics.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.logistics.model.WorkDay
import com.demo.logistics.ui.components.BigButton
import com.demo.logistics.viewmodel.WorkDayViewModel
import java.time.format.DateTimeFormatter

@Composable
fun HistoryScreen(
    viewModel: WorkDayViewModel,
    onBack: () -> Unit
) {
    val selected = remember { mutableStateOf<Pair<String, WorkDay?>?>(null) }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Storico settimana", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, fill = false)) {
            items(viewModel.history()) { item ->
                val dateText = item.first.format(DateTimeFormatter.ofPattern("EEE dd MMM"))
                val status = if (item.second != null) "Compilata" else "Non compilata"
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selected.value = dateText to item.second },
                    colors = CardDefaults.cardColors()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = dateText, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text(text = status, fontSize = 16.sp)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        selected.value?.let { (date, workDay) ->
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Riepilogo $date", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                    if (workDay != null) {
                        Text(text = "Entrata: ${workDay.startTime} - Uscita: ${workDay.endTime}")
                        Text(text = "Pausa: ${workDay.breakMinutes} min")
                        Spacer(modifier = Modifier.height(6.dp))
                        workDay.slices.forEachIndexed { idx, slice ->
                            Text(text = "Fascia ${idx + 1}: ${slice.jobName}, ${slice.costCenterName} ${slice.startTime}-${slice.endTime}")
                        }
                    } else {
                        Text(text = "Nessun dato inserito")
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
        }
        BigButton(text = "Torna alla Home") { onBack() }
    }
}
