package com.vegescore.app.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vegescore.app.scoring.ChartPoint
import kotlin.math.abs

@Composable
fun LineChart(
    points: List<ChartPoint>,
    modifier: Modifier = Modifier
) {
    var tappedIndex by remember { mutableStateOf<Int?>(null) }
    val lineColor = VeganColor

    Column(modifier = modifier) {
        tappedIndex?.let { idx ->
            val p = points.getOrNull(idx)
            if (p != null) {
                val scoreText = if (p.score == null) "no data" else "${(p.score * 100).toInt()}%"
                Text(
                    "${p.label}: $scoreText  (${p.mealCount} meals)",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .pointerInput(points) {
                    detectTapGestures { tap ->
                        if (points.isEmpty()) return@detectTapGestures
                        val step = size.width.toFloat() / (points.size - 1).coerceAtLeast(1)
                        val idx = ((tap.x / step).toInt()).coerceIn(0, points.lastIndex)
                        tappedIndex = if (tappedIndex == idx) null else idx
                    }
                }
        ) {
            if (points.isEmpty()) return@Canvas

            val padH = 8.dp.toPx()
            val padV = 16.dp.toPx()
            val chartW = size.width - padH * 2
            val chartH = size.height - padV * 2
            val n = points.size

            val step = if (n > 1) chartW / (n - 1) else chartW / 2

            fun xOf(i: Int) = padH + i * step
            fun yOf(score: Double) = padV + chartH * (1.0 - score).toFloat()

            drawLine(
                color = Color.LightGray,
                start = Offset(padH, padV + chartH * 0.5f),
                end = Offset(padH + chartW, padV + chartH * 0.5f),
                strokeWidth = 1.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f))
            )

            val path = Path()
            var drawing = false

            for (i in points.indices) {
                val p = points[i]
                val score = p.score ?: continue
                val x = xOf(i)
                val y = yOf(score)
                if (!drawing) { path.moveTo(x, y); drawing = true }
                else path.lineTo(x, y)
            }

            drawPath(path, color = lineColor, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))

            for (i in points.indices) {
                val p = points[i]
                val score = p.score ?: continue
                val x = xOf(i)
                val y = yOf(score)
                drawCircle(color = lineColor, radius = 5.dp.toPx(), center = Offset(x, y))
                if (tappedIndex == i) {
                    drawCircle(color = Color.White, radius = 3.dp.toPx(), center = Offset(x, y))
                }
            }
        }

        Spacer(Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val stride = if (points.size <= 8) 1 else points.size / 7
            points.filterIndexed { i, _ -> i % stride == 0 || i == points.lastIndex }.forEach { p ->
                Text(p.label, fontSize = 10.sp, textAlign = TextAlign.Center, color = Color.Gray)
            }
        }
    }
}
