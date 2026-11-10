package com.vegescore.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vegescore.app.data.MealEntry
import com.vegescore.app.data.MealType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    entries: List<MealEntry>,
    onDelete: (MealEntry) -> Unit,
    onUpdate: (MealEntry) -> Unit,
    onBack: () -> Unit
) {
    var editEntry by remember { mutableStateOf<MealEntry?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal history") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(contentPadding = padding) {
            items(entries, key = { it.id }) { entry ->
                EntryRow(
                    entry = entry,
                    onDelete = { onDelete(entry) },
                    onEdit = { editEntry = entry }
                )
                HorizontalDivider()
            }
        }
    }

    editEntry?.let { entry ->
        EditEntryDialog(
            entry = entry,
            onDismiss = { editEntry = null },
            onConfirm = { updated ->
                onUpdate(updated)
                editEntry = null
            }
        )
    }
}

@Composable
private fun EntryRow(entry: MealEntry, onDelete: () -> Unit, onEdit: () -> Unit) {
    val fmt = DateTimeFormatter.ofPattern("EEE d MMM, HH:mm")
    val time = Instant.ofEpochMilli(entry.timestampMs).atZone(ZoneId.systemDefault()).format(fmt)
    val (emoji, label) = when (entry.type) {
        MealType.VEGAN -> "🥦" to "Vegetables"
        MealType.DAIRY_EGGS -> "🧀" to "Dairy / Eggs"
        MealType.MEAT -> "🥩" to "Meat"
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$emoji  $label", modifier = Modifier.weight(1f))
        Text(time, style = MaterialTheme.typography.bodySmall)
        Spacer(Modifier.width(8.dp))
        IconButton(onClick = onEdit) { Icon(Icons.Default.Edit, "Edit") }
        IconButton(onClick = onDelete) { Icon(Icons.Default.Delete, "Delete") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditEntryDialog(
    entry: MealEntry,
    onDismiss: () -> Unit,
    onConfirm: (MealEntry) -> Unit
) {
    var selectedType by remember { mutableStateOf(entry.type) }
    val dateTime = remember(entry.timestampMs) {
        Instant.ofEpochMilli(entry.timestampMs).atZone(ZoneId.systemDefault())
    }
    var year by remember { mutableStateOf(dateTime.year.toString()) }
    var month by remember { mutableStateOf(dateTime.monthValue.toString()) }
    var day by remember { mutableStateOf(dateTime.dayOfMonth.toString()) }
    var hour by remember { mutableStateOf(dateTime.hour.toString()) }
    var minute by remember { mutableStateOf(dateTime.minute.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit entry") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Meal type", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MealType.entries.forEach { t ->
                        val (emoji, lbl) = when (t) {
                            MealType.VEGAN -> "🥦" to "Veg"
                            MealType.DAIRY_EGGS -> "🧀" to "Dairy"
                            MealType.MEAT -> "🥩" to "Meat"
                        }
                        FilterChip(selected = t == selectedType, onClick = { selectedType = t },
                            label = { Text("$emoji $lbl") })
                    }
                }
                Text("Date & time", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    OutlinedTextField(year, { year = it }, label = { Text("Year") }, modifier = Modifier.weight(1.5f))
                    OutlinedTextField(month, { month = it }, label = { Text("Mo") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(day, { day = it }, label = { Text("Day") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(hour, { hour = it }, label = { Text("Hr") }, modifier = Modifier.weight(1f))
                    OutlinedTextField(minute, { minute = it }, label = { Text("Min") }, modifier = Modifier.weight(1f))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                try {
                    val zone = ZoneId.systemDefault()
                    val newTs = java.time.LocalDateTime.of(
                        year.toInt(), month.toInt(), day.toInt(), hour.toInt(), minute.toInt()
                    ).atZone(zone).toInstant().toEpochMilli()
                    onConfirm(entry.copy(type = selectedType, timestampMs = newTs))
                } catch (_: Exception) { onDismiss() }
            }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
