package co.crhystian.xutp.presentation.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Overlay de botones de acción para el lado derecho de la pantalla.
 * 
 * Layout (vista landscape):
 * ```
 *                               [A] (JUMP)
 *                               
 *           [X]                           
 *         (ATTACK)
 *                               
 *   [Y]                         [B] (DASH)
 * (SPECIAL)
 * ```
 * 
 * - A: Arriba derecha
 * - X: Centro, 40% más grande, equidistante a A, B, Y
 * - B: Abajo derecha (misma columna que A)
 * - Y: Abajo izquierda (misma fila que B)
 * 
 * @param onActionPressed Callback cuando se presiona una acción
 * @param onActionReleased Callback cuando se suelta una acción
 */
@Composable
fun ActionButtonsOverlay(
    onActionPressed: (ActionButtonType) -> Unit,
    onActionReleased: (ActionButtonType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val baseButtonSize = 56.dp
    val largeButtonSize = baseButtonSize * 1.2f
    val spacing = 2.dp // Tu valor original
    val edgePadding = 16.dp
    
    // Tamaño del contenedor para que X quede centrado y equidistante
    val containerWidth = baseButtonSize + spacing + largeButtonSize + spacing + baseButtonSize
    val containerHeight = baseButtonSize + spacing + largeButtonSize + spacing + baseButtonSize
    
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomEnd,
    ) {
        Box(
            modifier = Modifier
                .padding(end = edgePadding, bottom = edgePadding)
                .width(containerWidth)
                .height(containerHeight)
        ) {
            // Botón A - JUMP (arriba derecha)
            Box(
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                GameActionButton(
                    config = ActionButtonDefaults.jumpButton,
                    baseSize = baseButtonSize,
                    onPressed = { onActionPressed(ActionButtonType.JUMP) },
                    onReleased = { onActionReleased(ActionButtonType.JUMP) },
                )
            }
            
            // Botón X - ATTACK (centro, más grande)
            Box(
                modifier = Modifier.align(Alignment.Center)
            ) {
                GameActionButton(
                    config = ActionButtonDefaults.attackButton,
                    baseSize = baseButtonSize,
                    onPressed = { onActionPressed(ActionButtonType.ATTACK) },
                    onReleased = { onActionReleased(ActionButtonType.ATTACK) },
                )
            }
            
            // Botón B - DASH (abajo derecha)
            Box(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                GameActionButton(
                    config = ActionButtonDefaults.dashButton,
                    baseSize = baseButtonSize,
                    onPressed = { onActionPressed(ActionButtonType.DASH) },
                    onReleased = { onActionReleased(ActionButtonType.DASH) },
                )
            }
            
            // Botón Y - SPECIAL (abajo izquierda)
            Box(
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                GameActionButton(
                    config = ActionButtonDefaults.specialButton,
                    baseSize = baseButtonSize,
                    onPressed = { onActionPressed(ActionButtonType.SPECIAL) },
                    onReleased = { onActionReleased(ActionButtonType.SPECIAL) },
                )
            }
        }
    }
}

// ============== PREVIEWS ==============

/**
 * Preview del layout completo de botones de acción.
 */
@Preview
@Composable
private fun ActionButtonsOverlayPreview() {
    Box(
        modifier = Modifier
            .size(400.dp, 300.dp)
            .background(Color.DarkGray)
    ) {
        ActionButtonsOverlay(
            onActionPressed = {},
            onActionReleased = {},
        )
    }
}

/**
 * Preview en formato landscape simulando pantalla de juego.
 */
@Preview
@Composable
private fun ActionButtonsLandscapePreview() {
    Box(
        modifier = Modifier
            .size(600.dp, 350.dp)
            .background(Color(0xFF2D2D2D))
    ) {
        ActionButtonsOverlay(
            onActionPressed = {},
            onActionReleased = {},
        )
    }
}
