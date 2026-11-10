package com.vegescore.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vegescore.app.data.MealType

@Composable
fun MealButtons(
    onLog: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        MealButton("🥦 Vegetables", VeganColor, MealType.VEGAN, onLog)
        MealButton("🧀 Dairy / Eggs", DairyColor, MealType.DAIRY_EGGS, onLog)
        MealButton("🥩 Meat", MeatColor, MealType.MEAT, onLog)
    }
}

@Composable
private fun MealButton(
    label: String,
    color: Color,
    type: MealType,
    onClick: (MealType) -> Unit
) {
    Button(
        onClick = { onClick(type) },
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(label, fontSize = 22.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
    }
}
