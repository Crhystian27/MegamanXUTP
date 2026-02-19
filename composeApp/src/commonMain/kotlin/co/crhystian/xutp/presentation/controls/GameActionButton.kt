package co.crhystian.xutp.presentation.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Botón de acción circular reutilizable.
 * 
 * Diseñado siguiendo principios de:
 * - SRP: Solo renderiza y detecta toques
 * - OCP: Configurable via parámetros sin modificar código
 * - DIP: Depende de abstracciones (callbacks) no implementaciones
 * 
 * @param config Configuración del botón (tipo, label, tamaño)
 * @param baseSize Tamaño base del botón
 * @param onPressed Callback cuando se presiona el botón
 * @param onReleased Callback cuando se suelta el botón
 * @param modifier Modificador opcional
 */
@Composable
fun GameActionButton(
    config: ActionButtonConfig,
    baseSize: Dp = 56.dp,
    onPressed: () -> Unit,
    onReleased: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val actualSize = baseSize * config.sizeMultiplier
    val radiusPx = with(density) { (actualSize / 2).toPx() }
    
    var isPressed by remember { mutableStateOf(false) }
    val textMeasurer = rememberTextMeasurer()

    Canvas(
        modifier = modifier
            .size(actualSize)
            .semantics { contentDescription = "Botón ${config.label}" }
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        onPressed()
                        tryAwaitRelease()
                        isPressed = false
                        onReleased()
                    }
                )
            }
    ) {
        val center = Offset(radiusPx, radiusPx)
        
        // Fondo del botón
        drawCircle(
            color = Color.White.copy(alpha = if (isPressed) 0.4f else 0.15f),
            radius = radiusPx,
            center = center,
        )
        
        // Borde del botón
        drawCircle(
            color = Color.White.copy(alpha = if (isPressed) 0.8f else 0.4f),
            radius = radiusPx,
            center = center,
            style = Stroke(width = 3f),
        )
        
        // Label del botón
        val textStyle = TextStyle(
            color = Color.White.copy(alpha = if (isPressed) 1f else 0.7f),
            fontSize = (actualSize.value * 0.4f).sp,
            fontWeight = FontWeight.Bold,
        )
        
        val textLayoutResult = textMeasurer.measure(
            text = config.label,
            style = textStyle,
        )
        
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft = Offset(
                x = center.x - textLayoutResult.size.width / 2,
                y = center.y - textLayoutResult.size.height / 2,
            ),
        )
    }
}

// ============== PREVIEWS ==============

/**
 * Preview del botón A (JUMP) - tamaño normal.
 */
@Preview
@Composable
private fun JumpButtonPreview() {
    Box(
        modifier = Modifier
            .size(80.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        GameActionButton(
            config = ActionButtonDefaults.jumpButton,
            onPressed = {},
            onReleased = {},
        )
    }
}

/**
 * Preview del botón X (ATTACK) - 40% más grande.
 */
@Preview
@Composable
private fun AttackButtonPreview() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        GameActionButton(
            config = ActionButtonDefaults.attackButton,
            onPressed = {},
            onReleased = {},
        )
    }
}

/**
 * Preview comparativo de todos los botones.
 */
@Preview
@Composable
private fun AllButtonsPreview() {
    Box(
        modifier = Modifier
            .size(300.dp, 100.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            GameActionButton(
                config = ActionButtonDefaults.specialButton,
                onPressed = {},
                onReleased = {},
            )
            GameActionButton(
                config = ActionButtonDefaults.attackButton,
                onPressed = {},
                onReleased = {},
            )
            GameActionButton(
                config = ActionButtonDefaults.jumpButton,
                onPressed = {},
                onReleased = {},
            )
            GameActionButton(
                config = ActionButtonDefaults.dashButton,
                onPressed = {},
                onReleased = {},
            )
        }
    }
}
