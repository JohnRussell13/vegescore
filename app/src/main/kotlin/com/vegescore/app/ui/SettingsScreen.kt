package com.vegescore.app.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.vegescore.app.data.BackupManager
import com.vegescore.app.data.AppDatabase
import com.vegescore.app.scoring.FormulaId
import com.vegescore.app.scoring.Formulas
import com.vegescore.app.scoring.TimeWindow
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    formulaId: FormulaId,
    timeWindow: TimeWindow,
    onFormulaChange: (FormulaId) -> Unit,
    onWindowChange: (TimeWindow) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var statusMsg by remember { mutableStateOf("") }

    val backupManager = remember {
        BackupManager(context, AppDatabase.getInstance(context).mealDao())
    }

    val exportLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/json")
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                backupManager.exportToUri(it).fold(
                    onSuccess = { count -> statusMsg = "Exported $count entries." },
                    onFailure = { e -> statusMsg = "Export failed: ${e.message}" }
                )
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            scope.launch {
                backupManager.importFromUri(it).fold(
                    onSuccess = { count -> statusMsg = "Imported $count entries." },
                    onFailure = { e -> statusMsg = "Import failed: ${e.message}" }
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            SectionLabel("Score formula")
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Formulas.all.forEach { formula ->
                    Row {
                        RadioButton(selected = formula.id == formulaId, onClick = { onFormulaChange(formula.id) })
                        Spacer(Modifier.width(8.dp))
                        Text(formula.displayName, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically))
                    }
                }
            }

            SectionLabel("Default time window")
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                TimeWindow.entries.forEach { w ->
                    Row {
                        RadioButton(selected = w == timeWindow, onClick = { onWindowChange(w) })
                        Spacer(Modifier.width(8.dp))
                        Text(w.displayName, modifier = Modifier.align(androidx.compose.ui.Alignment.CenterVertically))
                    }
                }
            }

            SectionLabel("Backup")
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { exportLauncher.launch("vegescore_backup.json") }) {
                    Text("Export")
                }
                OutlinedButton(onClick = { importLauncher.launch("application/json") }) {
                    Text("Import")
                }
            }
            if (statusMsg.isNotEmpty()) {
                Text(statusMsg, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
    HorizontalDivider()
}
