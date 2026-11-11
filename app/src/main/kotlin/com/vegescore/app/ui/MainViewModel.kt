package com.vegescore.app.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vegescore.app.data.AppDatabase
import com.vegescore.app.data.MealEntry
import com.vegescore.app.data.MealType
import com.vegescore.app.data.PreferencesRepository
import com.vegescore.app.scoring.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId

class MainViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.getInstance(app).mealDao()
    private val prefs = PreferencesRepository(app)

    val formulaId: StateFlow<FormulaId> = prefs.formulaId
        .stateIn(viewModelScope, SharingStarted.Eagerly, FormulaId.PLANT_BASED_SHARE)

    val timeWindow: StateFlow<TimeWindow> = prefs.timeWindow
        .stateIn(viewModelScope, SharingStarted.Eagerly, TimeWindow.ALL_TIME)

    val chartSpan: StateFlow<ChartSpan> = prefs.chartSpan
        .stateIn(viewModelScope, SharingStarted.Eagerly, ChartSpan.TWO_WEEKS)

    val allEntries: StateFlow<List<MealEntry>> = dao.getAllEntries()
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val currentScore: StateFlow<Double?> = combine(allEntries, formulaId, timeWindow) { entries, fId, window ->
        val formula = Formulas.byId(fId)
        val filtered = if (window.days == null) entries
        else {
            val cutoff = Instant.now().minusSeconds(window.days.toLong() * 86400).toEpochMilli()
            entries.filter { it.timestampMs >= cutoff }
        }
        formula.compute(filtered)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val chartPoints: StateFlow<List<ChartPoint>> = combine(allEntries, formulaId, chartSpan) { entries, fId, span ->
        ChartBuilder.build(entries, span, Formulas.byId(fId))
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val _pendingUndo = MutableStateFlow<MealEntry?>(null)
    val pendingUndo: StateFlow<MealEntry?> = _pendingUndo.asStateFlow()

    private var undoJob: Job? = null

    fun logMeal(type: MealType) {
        viewModelScope.launch {
            val entry = MealEntry(type = type, timestampMs = System.currentTimeMillis())
            val id = dao.insert(entry)
            val saved = entry.copy(id = id)
            _pendingUndo.value = saved
            undoJob?.cancel()
            undoJob = launch {
                delay(5000)
                _pendingUndo.value = null
            }
        }
    }

    fun undoLastLog() {
        viewModelScope.launch {
            val entry = _pendingUndo.value ?: return@launch
            dao.delete(entry)
            _pendingUndo.value = null
            undoJob?.cancel()
        }
    }

    fun deleteEntry(entry: MealEntry) {
        viewModelScope.launch { dao.delete(entry) }
    }

    fun updateEntry(entry: MealEntry) {
        viewModelScope.launch { dao.update(entry) }
    }

    fun setFormula(id: FormulaId) {
        viewModelScope.launch { prefs.setFormula(id) }
    }

    fun setTimeWindow(w: TimeWindow) {
        viewModelScope.launch { prefs.setTimeWindow(w) }
    }

    fun setChartSpan(s: ChartSpan) {
        viewModelScope.launch { prefs.setChartSpan(s) }
    }
}
