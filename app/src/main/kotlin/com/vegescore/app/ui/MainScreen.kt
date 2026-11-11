package com.vegescore.app.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    onChartSpanChange: (ChartSpan) -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text("VegeScore") },
                    actions = {
                        IconButton(onClick = onNavigateHistory) { Icon(Icons.Default.History, "History") }
                        IconButton(onClick = onNavigateSettings) { Icon(Icons.Default.Settings, "Settings") }
                    }
                )
                HorizontalDivider(color = DividerColor)
            }
        }
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
                caption = "${Formulas.byId(formulaId).displayName} · ${timeWindow.displayName}"
            )

            MealButtons(onLog = onLog, modifier = Modifier.fillMaxWidth())

            pendingUndo?.let {
                val icon = when (it.type) {
                    MealType.VEGAN -> "🥦"
                    MealType.DAIRY_EGGS -> "🧀"
                    MealType.MEAT -> "🥩"
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(start = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Logged: $icon", fontSize = 13.sp, color = Color(0xFF333333))
                    TextButton(onClick = onUndo) { Text("UNDO", fontWeight = FontWeight.Bold) }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.outlinedCardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, Color(0xFFE8E8E8))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Score history", style = MaterialTheme.typography.titleSmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            ChartSpan.entries.forEach { span ->
                                FilterChip(
                                    selected = span == chartSpan,
                                    onClick = { onChartSpanChange(span) },
                                    label = { Text(span.shortName, fontSize = 12.sp) }
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
