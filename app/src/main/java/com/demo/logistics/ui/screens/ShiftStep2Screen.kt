package com.demo.logistics.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.demo.logistics.ui.components.BigButton
import com.demo.logistics.viewmodel.WorkDayViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShiftStep2Screen(
    viewModel: WorkDayViewModel,
    onConfirm: () -> Unit,
    onAddSlice: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Step 2 – Fasce orarie", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "Aggiungi fasce semplici", fontSize = 18.sp)
        Spacer(modifier = Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f, fill = false)) {
            itemsIndexed(viewModel.slices) { index, slice ->
                val expandedJob = remember { mutableStateOf(false) }
                val expandedCenter = remember { mutableStateOf(false) }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(text = "Fascia ${index + 1}", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedJob.value,
                        onExpandedChange = { expandedJob.value = !expandedJob.value }
                    ) {
                        TextField(
                            value = slice.jobName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Commessa") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedJob.value) }
                        )
                        DropdownMenu(
                            expanded = expandedJob.value,
                            onDismissRequest = { expandedJob.value = false }
                        ) {
                            viewModel.availableJobs().forEach { job ->
                                DropdownMenuItem(text = { Text(job) }, onClick = {
                                    viewModel.updateSlice(index) { it.copy(jobName = job) }
                                    expandedJob.value = false
                                })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = expandedCenter.value,
                        onExpandedChange = { expandedCenter.value = !expandedCenter.value }
                    ) {
                        TextField(
                            value = slice.costCenterName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Centro di costo") },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCenter.value) }
                        )
                        DropdownMenu(
                            expanded = expandedCenter.value,
                            onDismissRequest = { expandedCenter.value = false }
                        ) {
                            viewModel.availableCostCenters().forEach { center ->
                                DropdownMenuItem(text = { Text(center) }, onClick = {
                                    viewModel.updateSlice(index) { it.copy(costCenterName = center) }
                                    expandedCenter.value = false
                                })
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = slice.startTime,
                            onValueChange = { text -> viewModel.updateSlice(index) { it.copy(startTime = text) } },
                            label = { Text("Dalle") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = slice.endTime,
                            onValueChange = { text -> viewModel.updateSlice(index) { it.copy(endTime = text) } },
                            label = { Text("Alle") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        viewModel.stepError?.let {
            Text(text = it, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }
        Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
            BigButton(text = "Aggiungi fascia") { onAddSlice() }
            BigButton(text = "Conferma giornata") {
                if (viewModel.saveWorkDay()) {
                    onConfirm()
                }
            }
        }
    }
}
