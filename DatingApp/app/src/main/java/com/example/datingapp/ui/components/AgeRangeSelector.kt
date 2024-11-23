import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.*
import com.example.datingapp.ui.theme.MediumPink


@Composable
fun AgeRangeSelector(
    onRangeSelected: (startAge: Int, endAge: Int) -> Unit
) {
    var ageRange by remember { mutableStateOf(0f..100f) }

    Column(modifier = Modifier.fillMaxWidth()) {
        val startAge = ageRange.start.toInt()
        val endAge = ageRange.endInclusive.toInt()

        Box(modifier = Modifier.fillMaxWidth().height(56.dp)) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val startX = size.width * (ageRange.start / 100f)
                val endX = size.width * (ageRange.endInclusive / 100f)

                drawContext.canvas.nativeCanvas.apply {
                    val paint = Paint().asFrameworkPaint().apply {
                        isAntiAlias = true
                        textSize = 14.sp.toPx()
                        color = android.graphics.Color.GRAY
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText(
                        startAge.toString(),
                        startX,
                        -10f,
                        paint
                    )
                    drawText(
                        endAge.toString(),
                        endX,
                        -10f,
                        paint
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
                colors = SliderDefaults.colors(
                    thumbColor = MediumPink,
                    activeTrackColor = MediumPink
                )
            )
        }
    }
}
