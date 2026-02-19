package co.crhystian.xutp.presentation.controls

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * D-pad virtual tipo joystick analógico.
 * 
 * Solo maneja movimiento horizontal (izquierda/derecha).
 * El salto se maneja con el botón A en ActionButtonsOverlay.
 *
 * Usa detectDragGestures para compatibilidad multiplataforma (Android/iOS).
 * 
 * Funcionamiento:
 * 1. Al tocar dentro del círculo, se calcula inmediatamente la dirección.
 * 2. Al mover el dedo, se actualiza la dirección en tiempo real.
 * 3. Al soltar, se resetea a neutro.
 * 4. Zona muerta del 30% del radio para evitar inputs accidentales.
 * 5. El knob visual sigue al dedo, clampeado al radio máximo.
 * 
 * @param onDirectionChange Callback con el estado de direcciones (left, right)
 */
@Composable
fun DpadOverlay(
    onDirectionChange: (left: Boolean, right: Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val outerRadiusDp = 70.dp
    val outerRadiusPx = with(density) { outerRadiusDp.toPx() }
    val deadZone = outerRadiusPx * 0.3f

    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    var isTouching by remember { mutableStateOf(false) }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart,
    ) {
        Canvas(
            modifier = Modifier
                .padding(start = 24.dp, bottom = 24.dp)
                .size(outerRadiusDp * 2)
                .semantics { contentDescription = "Joystick de movimiento" }
                .pointerInput(Unit) {
                    val center = Offset(outerRadiusPx, outerRadiusPx)
                    
                    detectDragGestures(
                        onDragStart = { startOffset ->
                            isTouching = true
                            val delta = startOffset - center
                            val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                            val clamped = if (dist > outerRadiusPx) {
                                delta * (outerRadiusPx / dist)
                            } else delta
                            knobOffset = clamped
                            emitDirection(clamped, deadZone, onDirectionChange)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            val delta = change.position - center
                            val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                            val clamped = if (dist > outerRadiusPx) {
                                delta * (outerRadiusPx / dist)
                            } else delta
                            knobOffset = clamped
                            emitDirection(clamped, deadZone, onDirectionChange)
                        },
                        onDragEnd = {
                            isTouching = false
                            knobOffset = Offset.Zero
                            onDirectionChange(false, false)
                        },
                        onDragCancel = {
                            isTouching = false
                            knobOffset = Offset.Zero
                            onDirectionChange(false, false)
                        }
                    )
                }
        ) {
            val center = Offset(outerRadiusPx, outerRadiusPx)

            // Círculo exterior
            drawCircle(
                color = Color.White.copy(alpha = 0.15f),
                radius = outerRadiusPx,
                center = center,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.3f),
                radius = outerRadiusPx,
                center = center,
                style = Stroke(width = 2f),
            )

            // Knob
            val knobRadius = outerRadiusPx * 0.35f
            val knobCenter = center + knobOffset
            drawCircle(
                color = Color.White.copy(alpha = if (isTouching) 0.5f else 0.25f),
                radius = knobRadius,
                center = knobCenter,
            )
            drawCircle(
                color = Color.White.copy(alpha = if (isTouching) 0.7f else 0.4f),
                radius = knobRadius,
                center = knobCenter,
                style = Stroke(width = 2f),
            )
        }
    }
}

/**
 * Determina la dirección horizontal según el offset del knob.
 * Solo emite si el dedo está fuera de la zona muerta.
 */
private fun emitDirection(
    offset: Offset,
    deadZone: Float,
    onDirectionChange: (Boolean, Boolean) -> Unit,
) {
    val dist = sqrt(offset.x * offset.x + offset.y * offset.y)
    if (dist < deadZone) {
        onDirectionChange(false, false)
        return
    }

    val angle = atan2(offset.y, offset.x)
    val degrees = (angle * 180f / PI).toFloat()

    // Solo direcciones horizontales
    val right = degrees in -67.5f..67.5f
    val left = degrees > 112.5f || degrees < -112.5f

    onDirectionChange(left, right)
}

// ============== PREVIEWS ==============

/**
 * Preview del DpadOverlay en estado neutral (sin tocar).
 */
@Preview
@Composable
private fun DpadOverlayPreview() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        DpadJoystickVisual(
            knobOffset = Offset.Zero,
            isTouching = false,
        )
    }
}

/**
 * Preview del DpadOverlay simulando input hacia la derecha.
 */
@Preview
@Composable
private fun DpadOverlayRightPreview() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        DpadJoystickVisual(
            knobOffset = Offset(50f, 0f),
            isTouching = true,
        )
    }
}

/**
 * Preview del DpadOverlay simulando input hacia la izquierda.
 */
@Preview
@Composable
private fun DpadOverlayLeftPreview() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center,
    ) {
        DpadJoystickVisual(
            knobOffset = Offset(-50f, 0f),
            isTouching = true,
        )
    }
}

/**
 * Componente visual del joystick extraído para previews.
 */
@Composable
internal fun DpadJoystickVisual(
    knobOffset: Offset,
    isTouching: Boolean,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val outerRadiusDp = 70.dp
    val outerRadiusPx = with(density) { outerRadiusDp.toPx() }

    Canvas(
        modifier = modifier.size(outerRadiusDp * 2)
    ) {
        val center = Offset(outerRadiusPx, outerRadiusPx)

        // Círculo exterior
        drawCircle(
            color = Color.White.copy(alpha = 0.15f),
            radius = outerRadiusPx,
            center = center,
        )
        drawCircle(
            color = Color.White.copy(alpha = 0.3f),
            radius = outerRadiusPx,
            center = center,
            style = Stroke(width = 2f),
        )

        // Knob
        val knobRadius = outerRadiusPx * 0.35f
        val knobCenter = center + knobOffset
        drawCircle(
            color = Color.White.copy(alpha = if (isTouching) 0.5f else 0.25f),
            radius = knobRadius,
            center = knobCenter,
        )
        drawCircle(
            color = Color.White.copy(alpha = if (isTouching) 0.7f else 0.4f),
            radius = knobRadius,
            center = knobCenter,
            style = Stroke(width = 2f),
        )
    }
}
