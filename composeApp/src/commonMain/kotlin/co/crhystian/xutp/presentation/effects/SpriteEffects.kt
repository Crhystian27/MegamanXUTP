package co.crhystian.xutp.presentation.effects

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize

/**
 * Configuración para el efecto de trail de un personaje.
 */
data class TrailEffectConfig(
    val trailColor: Color = Color.Red,
    val borderColor: Color = Color(0xFF8B0000),
    val maxOpacity: Float = 0.6f,
    val minOpacity: Float = 0.05f,
    val borderSize: Float = 2f,
)

/**
 * Colores predefinidos para diferentes personajes.
 */
object TrailColors {
    val ZERO_RED = Color.Red
    val ZERO_BORDER = Color(0xFF8B0000)
    val MEGAMAN_BLUE = Color(0xFF00BFFF)
    val MEGAMAN_BORDER = Color(0xFF00008B)
}

/**
 * Dibuja un trail de siluetas sólidas con borde.
 */
fun DrawScope.drawSpriteTrail(
    image: ImageBitmap,
    trailPositions: List<Float>,
    currentY: Float,
    scaleX: Float,
    scaleY: Float,
    flip: Boolean = false,
    fadeMultiplier: Float = 1f,
    config: TrailEffectConfig = TrailEffectConfig(),
) {
    if (trailPositions.isEmpty()) return
    
    val spriteScale = minOf(scaleX, scaleY) * 1.2f
    val spriteW = (image.width * spriteScale).toInt()
    val spriteH = (image.height * spriteScale).toInt()
    val drawY = (currentY * scaleY - spriteH).toInt()
    val borderOffset = (config.borderSize * spriteScale).toInt()
    
    trailPositions.forEachIndexed { index, trailX ->
        val alpha = calculateAlpha(index, trailPositions.size, config, fadeMultiplier)
        val centerX = trailX * scaleX
        val drawX = (centerX - spriteW / 2f).toInt()
        
        // Borde
        drawGhost(image, drawX - borderOffset/2, drawY - borderOffset/2, 
            spriteW + borderOffset, spriteH + borderOffset, centerX, flip,
            config.borderColor.copy(alpha = alpha * 0.8f))
        
        // Silueta principal
        drawGhost(image, drawX, drawY, spriteW, spriteH, centerX, flip,
            config.trailColor.copy(alpha = alpha))
    }
}

private fun calculateAlpha(
    index: Int, 
    total: Int, 
    config: TrailEffectConfig,
    fadeMultiplier: Float
): Float {
    val progress = if (total > 1) index.toFloat() / (total - 1) else 0f
    val baseAlpha = config.maxOpacity - (config.maxOpacity - config.minOpacity) * progress
    return baseAlpha * fadeMultiplier
}

private fun DrawScope.drawGhost(
    image: ImageBitmap,
    drawX: Int,
    drawY: Int,
    width: Int,
    height: Int,
    centerX: Float,
    flip: Boolean,
    color: Color,
) {
    val colorFilter = ColorFilter.tint(color, BlendMode.SrcIn)
    
    val draw = {
        drawImage(
            image = image,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(image.width, image.height),
            dstOffset = IntOffset(drawX, drawY),
            dstSize = IntSize(width, height),
            filterQuality = FilterQuality.Low,
            colorFilter = colorFilter,
        )
    }
    
    if (flip) {
        scale(-1f, 1f, Offset(centerX, drawY + height / 2f)) { draw() }
    } else {
        draw()
    }
}
