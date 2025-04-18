package com.mu54omd.ullama.ui.screen.chat.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp

fun Modifier.colorMatrixWithContent(matrix: ColorMatrix): Modifier = this.then(
    Modifier.drawWithContent {
        val colorFilter = ColorFilter.colorMatrix(matrix)
        val paint = Paint().apply {
            this.colorFilter = colorFilter
        }
        val bounds = Rect(0f, 0f, size.width, size.height)

        drawIntoCanvas { canvas ->
            canvas.saveLayer(bounds, paint)
            drawContent()
            canvas.restore()
        }
    }
)

fun alphaFilterColorMatrix() = ColorMatrix(
    floatArrayOf(
        1f, 0f, 0f, 0f, 0f,
        0f, 1f, 0f, 0f, 0f,
        0f, 0f, 1f, 0f, 0f,
        0f, 0f, 0f, 39f, -5000f
    )
)

fun Modifier.outlineBlur(
    blurRadius: Dp,
    shape: Shape,
    color: Color = Color.Black,
): Modifier = this.then(
    Modifier.drawWithCache {
        val nativePaint = android.graphics.Paint().apply {
            isAntiAlias = true
            this.color = color.toArgb()
            maskFilter = android.graphics.BlurMaskFilter(
                blurRadius.toPx(),
                android.graphics.BlurMaskFilter.Blur.NORMAL
            )
        }
        val outline = shape.createOutline(size, layoutDirection, this)
        val shapePath = Path().apply {
            addOutline(outline)
        }

        onDrawWithContent {
            drawIntoCanvas { canvas ->
                canvas.save()
                canvas.clipPath(shapePath, ClipOp.Difference)
                canvas.nativeCanvas.drawPath(shapePath.asAndroidPath(), nativePaint)
                canvas.restore()
            }
            drawContent()
        }
    }
)