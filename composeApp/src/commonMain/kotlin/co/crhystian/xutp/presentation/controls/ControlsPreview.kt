package co.crhystian.xutp.presentation.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * Preview combinado de todos los controles del juego.
 * Muestra cómo se verán el D-pad (izquierda) y los botones de acción (derecha)
 * en una pantalla landscape.
 */
@Preview
@Composable
private fun FullControlsLayoutPreview() {
    Box(
        modifier = Modifier
            .size(800.dp, 400.dp)
            .background(Color(0xFF1A1A2E))
    ) {
        // D-pad (lado izquierdo)
        DpadOverlay(
            onDirectionChange = { _, _ -> }
        )
        
        // Botones de acción (lado derecho)
        ActionButtonsOverlay(
            onActionPressed = {},
            onActionReleased = {},
        )
    }
}

/**
 * Preview en tamaño de pantalla móvil típica (16:9 landscape).
 */
@Preview
@Composable
private fun MobileScreenControlsPreview() {
    Box(
        modifier = Modifier
            .size(640.dp, 360.dp)
            .background(Color(0xFF16213E))
    ) {
        DpadOverlay(
            onDirectionChange = { _, _ -> }
        )
        
        ActionButtonsOverlay(
            onActionPressed = {},
            onActionReleased = {},
        )
    }
}
