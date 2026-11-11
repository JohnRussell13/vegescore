package com.vegescore.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

@Composable
fun ScoreDisplay(
    score: Double?,
    caption: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        if (score == null) {
            Text(
                text = "no data",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray
            )
        } else {
            val pct = (score * 100).roundToInt()
            val color = when {
                pct >= 80 -> VeganColor
                pct >= 50 -> DairyColor
                else -> MeatColor
            }
            Text(
                text = "$pct%",
                fontSize = 72.sp,
                fontWeight = FontWeight.ExtraBold,
                color = color
            )
        }
        Text(caption, fontSize = 12.sp, color = Color(0xFF999999))
    }
}
