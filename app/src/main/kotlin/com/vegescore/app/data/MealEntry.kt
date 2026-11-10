package com.vegescore.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

enum class MealType { VEGAN, DAIRY_EGGS, MEAT }

@Entity(tableName = "meal_entries")
@Serializable
data class MealEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: MealType,
    val timestampMs: Long
)
