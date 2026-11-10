package com.vegescore.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vegescore.app.data.MealEntry
import com.vegescore.app.data.MealType
import com.vegescore.app.scoring.ChartPoint
import com.vegescore.app.scoring.ChartSpan
import com.vegescore.app.scoring.FormulaId
import com.vegescore.app.scoring.Formulas
import com.vegescore.app.scoring.TimeWindow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    score: Double?,
    timeWindow: TimeWindow,
    formulaId: FormulaId,
    chartPoints: List<ChartPoint>,
    chartSpan: ChartSpan,
    pendingUndo: MealEntry?,
    onLog: (MealType) -> Unit,
    onUndo: () -> Unit,
    onWindowChange: (TimeWindow) -> Unit,
    onChartSpanChange: (ChartSpan) -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("VegeScore") },
                actions = {
                    IconButton(onClick = onNavigateHistory) { Icon(Icons.Default.History, "History") }
                    IconButton(onClick = onNavigateSettings) { Icon(Icons.Default.Settings, "Settings") }
                }
            )
        },
        snackbarHost = {}
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            ScoreDisplay(
                score = score,
                timeWindow = timeWindow,
                onWindowChange = onWindowChange
            )

            Text(
                Formulas.byId(formulaId).displayName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            MealButtons(onLog = onLog, modifier = Modifier.fillMaxWidth())

            pendingUndo?.let {
                val label = when (it.type) {
                    MealType.VEGAN -> "Vegetables"
                    MealType.DAIRY_EGGS -> "Dairy/Eggs"
                    MealType.MEAT -> "Meat"
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Logged: $label", style = MaterialTheme.typography.bodyMedium)
                    TextButton(onClick = onUndo) { Text("UNDO") }
                }
            }

            Spacer(Modifier.height(4.dp))

            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Score history", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            ChartSpan.entries.forEach { span ->
                                FilterChip(
                                    selected = span == chartSpan,
                                    onClick = { onChartSpanChange(span) },
                                    label = { Text(span.displayName, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    LineChart(points = chartPoints, modifier = Modifier.fillMaxWidth())
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
