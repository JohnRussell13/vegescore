package com.vegescore.app.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.vegescore.app.scoring.ChartSpan
import com.vegescore.app.scoring.FormulaId
import com.vegescore.app.scoring.TimeWindow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesRepository(private val context: Context) {
    companion object {
        private val KEY_FORMULA = stringPreferencesKey("formula")
        private val KEY_WINDOW = stringPreferencesKey("time_window")
        private val KEY_CHART_SPAN = stringPreferencesKey("chart_span")
    }

    val formulaId: Flow<FormulaId> = context.dataStore.data.map { prefs ->
        prefs[KEY_FORMULA]?.let { runCatching { FormulaId.valueOf(it) }.getOrNull() }
            ?: FormulaId.PLANT_BASED_SHARE
    }

    val timeWindow: Flow<TimeWindow> = context.dataStore.data.map { prefs ->
        prefs[KEY_WINDOW]?.let { runCatching { TimeWindow.valueOf(it) }.getOrNull() }
            ?: TimeWindow.ALL_TIME
    }

    val chartSpan: Flow<ChartSpan> = context.dataStore.data.map { prefs ->
        prefs[KEY_CHART_SPAN]?.let { runCatching { ChartSpan.valueOf(it) }.getOrNull() }
            ?: ChartSpan.TWO_WEEKS
    }

    suspend fun setFormula(id: FormulaId) {
        context.dataStore.edit { it[KEY_FORMULA] = id.name }
    }

    suspend fun setTimeWindow(w: TimeWindow) {
        context.dataStore.edit { it[KEY_WINDOW] = w.name }
    }

    suspend fun setChartSpan(s: ChartSpan) {
        context.dataStore.edit { it[KEY_CHART_SPAN] = s.name }
    }
}
