package com.vegescore.app.scoring

import com.vegescore.app.data.MealEntry
import com.vegescore.app.data.MealType

enum class FormulaId { PLANT_BASED_SHARE, VEGAN_SHARE, WEIGHTED }

data class ScoreFormula(
    val id: FormulaId,
    val displayName: String,
    val compute: (List<MealEntry>) -> Double?
)

object Formulas {
    val all: List<ScoreFormula> = listOf(
        ScoreFormula(FormulaId.PLANT_BASED_SHARE, "Plant-based share") { entries ->
            if (entries.isEmpty()) null
            else (entries.count { it.type == MealType.VEGAN || it.type == MealType.DAIRY_EGGS }).toDouble() / entries.size
        },
        ScoreFormula(FormulaId.VEGAN_SHARE, "Vegan share") { entries ->
            if (entries.isEmpty()) null
            else entries.count { it.type == MealType.VEGAN }.toDouble() / entries.size
        },
        ScoreFormula(FormulaId.WEIGHTED, "Weighted") { entries ->
            if (entries.isEmpty()) null
            else {
                val score = entries.sumOf {
                    when (it.type) {
                        MealType.VEGAN -> 1.0
                        MealType.DAIRY_EGGS -> 0.5
                        MealType.MEAT -> 0.0
                    }
                }
                score / entries.size
            }
        }
    )

    fun byId(id: FormulaId): ScoreFormula = all.first { it.id == id }
}
