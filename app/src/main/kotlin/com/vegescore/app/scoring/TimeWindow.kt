package com.vegescore.app.scoring

enum class TimeWindow(val displayName: String, val days: Int?) {
    DAYS_7("Last 7 days", 7),
    DAYS_30("Last 30 days", 30),
    DAYS_90("Last 90 days", 90),
    ALL_TIME("All time", null)
}
