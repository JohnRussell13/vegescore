package com.vegescore.app

import com.vegescore.app.data.MealEntry
import com.vegescore.app.data.MealType
import com.vegescore.app.scoring.Formulas
import com.vegescore.app.scoring.FormulaId
import org.junit.Assert.*
import org.junit.Test

class ScoringTest {

    private fun entry(type: MealType) = MealEntry(type = type, timestampMs = 0L)

    @Test fun `plant based share - all vegan`() {
        val entries = listOf(entry(MealType.VEGAN), entry(MealType.VEGAN))
        val result = Formulas.byId(FormulaId.PLANT_BASED_SHARE).compute(entries)
        assertEquals(1.0, result!!, 0.001)
    }

    @Test fun `plant based share - mixed`() {
        val entries = listOf(entry(MealType.VEGAN), entry(MealType.DAIRY_EGGS), entry(MealType.MEAT))
        val result = Formulas.byId(FormulaId.PLANT_BASED_SHARE).compute(entries)
        assertEquals(2.0 / 3.0, result!!, 0.001)
    }

    @Test fun `plant based share - empty returns null`() {
        assertNull(Formulas.byId(FormulaId.PLANT_BASED_SHARE).compute(emptyList()))
    }

    @Test fun `vegan share - only meat returns 0`() {
        val entries = listOf(entry(MealType.MEAT), entry(MealType.MEAT))
        val result = Formulas.byId(FormulaId.VEGAN_SHARE).compute(entries)
        assertEquals(0.0, result!!, 0.001)
    }

    @Test fun `vegan share - half vegan`() {
        val entries = listOf(entry(MealType.VEGAN), entry(MealType.MEAT))
        val result = Formulas.byId(FormulaId.VEGAN_SHARE).compute(entries)
        assertEquals(0.5, result!!, 0.001)
    }

    @Test fun `weighted - all vegan is 1`() {
        val entries = listOf(entry(MealType.VEGAN), entry(MealType.VEGAN))
        val result = Formulas.byId(FormulaId.WEIGHTED).compute(entries)
        assertEquals(1.0, result!!, 0.001)
    }

    @Test fun `weighted - vegan dairy meat averages correctly`() {
        val entries = listOf(entry(MealType.VEGAN), entry(MealType.DAIRY_EGGS), entry(MealType.MEAT))
        val result = Formulas.byId(FormulaId.WEIGHTED).compute(entries)
        assertEquals((1.0 + 0.5 + 0.0) / 3.0, result!!, 0.001)
    }

    @Test fun `weighted - all meat is 0`() {
        val entries = listOf(entry(MealType.MEAT))
        val result = Formulas.byId(FormulaId.WEIGHTED).compute(entries)
        assertEquals(0.0, result!!, 0.001)
    }

    @Test fun `all formulas return null for empty list`() {
        Formulas.all.forEach { formula ->
            assertNull("${formula.id} should return null for empty", formula.compute(emptyList()))
        }
    }
}
