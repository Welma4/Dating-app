package com.example.datingapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.datingapp.ui.theme.MediumPink

@Suppress("ktlint:standard:function-naming")
@Composable
fun AgeRangeSelector(
    initialRange: Pair<Int, Int>,
    onRangeSelected: (startAge: Int, endAge: Int) -> Unit,
) {
    var ageRange by remember { mutableStateOf(initialRange.first.toFloat()..initialRange.second.toFloat()) }

    Column(modifier = Modifier.fillMaxWidth()) {
        val startAge = ageRange.start.toInt()
        val endAge = ageRange.endInclusive.toInt()

        Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val startX = size.width * (ageRange.start / 100f)
                val endX = size.width * (ageRange.endInclusive / 100f)

                drawContext.canvas.nativeCanvas.apply {
                    val paint =
                        Paint().asFrameworkPaint().apply {
                            isAntiAlias = true
                            textSize = 14.sp.toPx()
                            color = android.graphics.Color.GRAY
                            textAlign = android.graphics.Paint.Align.CENTER
                        }
                    drawText(
                        startAge.toString(),
                        startX,
                        -10f,
                        paint,
                    )
                    drawText(
                        endAge.toString(),
                        endX,
                        -10f,
                        paint,
                    )
                }
            }

            RangeSlider(
                value = ageRange,
                steps = 99,
                onValueChange = { range -> ageRange = range },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth(),
                onValueChangeFinished = {
                    onRangeSelected(startAge, endAge)
                },
                colors =
                SliderDefaults.colors(
                    thumbColor = MediumPink,
                    activeTrackColor = MediumPink,
                ),
            )
        }
    }
}
