package com.vegescore.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vegescore.app.data.MealType

@Composable
fun MealButtons(
    onLog: (MealType) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MealButton("🥦", "Log vegetables", VeganColor, MealType.VEGAN, onLog)
        MealButton("🧀", "Log dairy or eggs", DairyColor, MealType.DAIRY_EGGS, onLog)
        MealButton("🥩", "Log meat", MeatColor, MealType.MEAT, onLog)
    }
}

@Composable
private fun RowScope.MealButton(
    icon: String,
    description: String,
    color: Color,
    type: MealType,
    onClick: (MealType) -> Unit
) {
    Button(
        onClick = { onClick(type) },
        modifier = Modifier
            .weight(1f)
            .aspectRatio(1f)
            .semantics { contentDescription = description },
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(icon, fontSize = 40.sp)
    }
}
