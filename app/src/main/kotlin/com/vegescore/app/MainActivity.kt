package com.vegescore.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import com.vegescore.app.ui.*

class MainActivity : ComponentActivity() {
    private val vm: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            VegeScoreTheme {
                VegeScoreApp(vm)
            }
        }
    }
}

enum class Screen { MAIN, HISTORY, SETTINGS }

@Composable
fun VegeScoreApp(vm: MainViewModel) {
    var screen by remember { mutableStateOf(Screen.MAIN) }

    val score by vm.currentScore.collectAsState()
    val timeWindow by vm.timeWindow.collectAsState()
    val formulaId by vm.formulaId.collectAsState()
    val chartPoints by vm.chartPoints.collectAsState()
    val chartSpan by vm.chartSpan.collectAsState()
    val pendingUndo by vm.pendingUndo.collectAsState()
    val allEntries by vm.allEntries.collectAsState()

    when (screen) {
        Screen.MAIN -> MainScreen(
            score = score,
            timeWindow = timeWindow,
            formulaId = formulaId,
            chartPoints = chartPoints,
            chartSpan = chartSpan,
            pendingUndo = pendingUndo,
            onLog = { vm.logMeal(it) },
            onUndo = { vm.undoLastLog() },
            onChartSpanChange = { vm.setChartSpan(it) },
            onNavigateHistory = { screen = Screen.HISTORY },
            onNavigateSettings = { screen = Screen.SETTINGS }
        )
        Screen.HISTORY -> HistoryScreen(
            entries = allEntries,
            onDelete = { vm.deleteEntry(it) },
            onUpdate = { vm.updateEntry(it) },
            onBack = { screen = Screen.MAIN }
        )
        Screen.SETTINGS -> SettingsScreen(
            formulaId = formulaId,
            timeWindow = timeWindow,
            onFormulaChange = { vm.setFormula(it) },
            onWindowChange = { vm.setTimeWindow(it) },
            onBack = { screen = Screen.MAIN }
        )
    }
}
