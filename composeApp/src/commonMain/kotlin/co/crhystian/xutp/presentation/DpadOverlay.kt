package co.crhystian.xutp.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEventType
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
 * Implementado con awaitPointerEventScope para respuesta inmediata al toque
 * (sin touch slop de detectDragGestures). Esto es crítico para controles de juego
 * donde cualquier latencia se siente mal.
 *
 * Funcionamiento:
 * 1. Al tocar dentro del círculo, se calcula inmediatamente la dirección.
 * 2. Al mover el dedo, se actualiza la dirección en tiempo real.
 * 3. Al soltar, se resetea a neutro.
 * 4. Zona muerta del 30% del radio para evitar inputs accidentales.
 * 5. El knob visual sigue al dedo, clampeado al radio máximo.
 */
@Composable
fun DpadOverlay(
    onDirectionChange: (left: Boolean, right: Boolean, up: Boolean, down: Boolean) -> Unit,
) {
    val density = LocalDensity.current
    val outerRadiusDp = 70.dp
    val outerRadiusPx = with(density) { outerRadiusDp.toPx() }
    val deadZone = outerRadiusPx * 0.3f

    var knobOffset by remember { mutableStateOf(Offset.Zero) }
    var isTouching by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomStart,
    ) {
        Canvas(
            modifier = Modifier
                .padding(start = 24.dp, bottom = 24.dp)
                .size(outerRadiusDp * 2)
                .semantics { contentDescription = "Joystick de movimiento" }
                .pointerInput(Unit) {
                    val center = Offset(outerRadiusPx, outerRadiusPx)

                    awaitPointerEventScope {
                        while (true) {
                            val event = awaitPointerEvent()
                            val pointer = event.changes.firstOrNull() ?: continue

                            when (event.type) {
                                PointerEventType.Press, PointerEventType.Move -> {
                                    pointer.consume()
                                    isTouching = true
                                    val delta = pointer.position - center
                                    val dist = sqrt(delta.x * delta.x + delta.y * delta.y)
                                    val clamped = if (dist > outerRadiusPx) {
                                        delta * (outerRadiusPx / dist)
                                    } else delta
                                    knobOffset = clamped
                                    emitDirection(clamped, deadZone, onDirectionChange)
                                }
                                PointerEventType.Release -> {
                                    pointer.consume()
                                    isTouching = false
                                    knobOffset = Offset.Zero
                                    onDirectionChange(false, false, false, false)
                                }
                            }
                        }
                    }
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
 * Determina la dirección cardinal según el offset del knob.
 * Solo emite si el dedo está fuera de la zona muerta.
 */
private fun emitDirection(
    offset: Offset,
    deadZone: Float,
    onDirectionChange: (Boolean, Boolean, Boolean, Boolean) -> Unit,
) {
    val dist = sqrt(offset.x * offset.x + offset.y * offset.y)
    if (dist < deadZone) {
        onDirectionChange(false, false, false, false)
        return
    }

    val angle = atan2(offset.y, offset.x)
    val degrees = (angle * 180f / PI).toFloat()

    val right = degrees in -45f..45f
    val down = degrees in 45f..135f
    val left = degrees > 135f || degrees < -135f
    val up = degrees in -135f..-45f

    onDirectionChange(left, right, up, down)
}

// ============== PREVIEWS ==============

/**
 * Preview del DpadOverlay en estado neutral (sin tocar).
 * Muestra el joystick con el knob centrado.
 */
@Preview
@Composable
private fun DpadOverlayPreview() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
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
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        DpadJoystickVisual(
            knobOffset = Offset(50f, 0f),
            isTouching = true,
        )
    }
}

/**
 * Preview del DpadOverlay simulando input hacia arriba-izquierda.
 */
@Preview
@Composable
private fun DpadOverlayUpLeftPreview() {
    Box(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        DpadJoystickVisual(
            knobOffset = Offset(-35f, -35f),
            isTouching = true,
        )
    }
}

/**
 * Componente visual del joystick extraído para previews.
 * Permite mostrar diferentes estados sin necesidad de interacción.
 */
@Composable
private fun DpadJoystickVisual(
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
