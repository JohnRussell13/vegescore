package com.vegescore.app.scoring

import com.vegescore.app.data.MealEntry
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

enum class ChartSpan(val displayName: String, val days: Int) {
    TWO_WEEKS("2 weeks", 14),
    FOUR_MONTHS("4 months", 120),
    ONE_YEAR("1 year", 365)
}

data class ChartPoint(
    val label: String,
    val score: Double?,
    val mealCount: Int
)

object ChartBuilder {
    fun build(entries: List<MealEntry>, span: ChartSpan, formula: ScoreFormula): List<ChartPoint> {
        val zone = ZoneId.systemDefault()
        val now = Instant.now().atZone(zone)
        val startInstant = now.minusDays(span.days.toLong()).toInstant()
        val relevant = entries.filter { it.timestampMs >= startInstant.toEpochMilli() }

        return when {
            span.days <= 14 -> buildByDay(relevant, span, formula, zone)
            span.days <= 120 -> buildByWeek(relevant, span, formula, zone)
            else -> buildByMonth(relevant, span, formula, zone)
        }
    }

    private fun buildByDay(
        entries: List<MealEntry>, span: ChartSpan, formula: ScoreFormula, zone: ZoneId
    ): List<ChartPoint> {
        val now = Instant.now().atZone(zone).toLocalDate()
        return (span.days - 1 downTo 0).map { daysAgo ->
            val day = now.minusDays(daysAgo.toLong())
            val dayStart = day.atStartOfDay(zone).toInstant().toEpochMilli()
            val dayEnd = day.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val bucket = entries.filter { it.timestampMs in dayStart until dayEnd }
            ChartPoint(
                label = "${day.monthValue}/${day.dayOfMonth}",
                score = formula.compute(bucket),
                mealCount = bucket.size
            )
        }
    }

    private fun buildByWeek(
        entries: List<MealEntry>, span: ChartSpan, formula: ScoreFormula, zone: ZoneId
    ): List<ChartPoint> {
        val now = Instant.now().atZone(zone).toLocalDate()
        val weeks = span.days / 7
        return (weeks - 1 downTo 0).map { weeksAgo ->
            val weekEnd = now.minusDays((weeksAgo * 7).toLong()).plusDays(1)
            val weekStart = weekEnd.minusDays(7)
            val startMs = weekStart.atStartOfDay(zone).toInstant().toEpochMilli()
            val endMs = weekEnd.atStartOfDay(zone).toInstant().toEpochMilli()
            val bucket = entries.filter { it.timestampMs in startMs until endMs }
            ChartPoint(
                label = "W${weekStart.monthValue}/${weekStart.dayOfMonth}",
                score = formula.compute(bucket),
                mealCount = bucket.size
            )
        }
    }

    private fun buildByMonth(
        entries: List<MealEntry>, span: ChartSpan, formula: ScoreFormula, zone: ZoneId
    ): List<ChartPoint> {
        val now = Instant.now().atZone(zone).toLocalDate()
        val months = 12
        return (months - 1 downTo 0).map { monthsAgo ->
            val monthDate = now.minusMonths(monthsAgo.toLong())
            val monthStart = monthDate.withDayOfMonth(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val monthEnd = monthDate.withDayOfMonth(1).plusMonths(1).atStartOfDay(zone).toInstant().toEpochMilli()
            val bucket = entries.filter { it.timestampMs in monthStart until monthEnd }
            val monthNames = listOf("Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec")
            ChartPoint(
                label = monthNames[monthDate.monthValue - 1],
                score = formula.compute(bucket),
                mealCount = bucket.size
            )
        }
    }
}
