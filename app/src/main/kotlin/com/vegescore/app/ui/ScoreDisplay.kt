package com.vegescore.app.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vegescore.app.scoring.TimeWindow
import kotlin.math.roundToInt

@Composable
fun ScoreDisplay(
    score: Double?,
    timeWindow: TimeWindow,
    onWindowChange: (TimeWindow) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (score == null) {
            Text(
                text = "no data",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            TimeWindow.entries.forEach { w ->
                FilterChip(
                    selected = w == timeWindow,
                    onClick = { onWindowChange(w) },
                    label = { Text(w.displayName, fontSize = 12.sp) }
                )
            }
        }
    }
}
