package co.crhystian.xutp.game

/**
 * Estado de las teclas presionadas en un momento dado.
 * Desacoplado de la fuente de input (teclado físico, D-pad virtual, etc.)
 * para cumplir con el principio de inversión de dependencias.
 */
data class InputState(
    val left: Boolean = false,
    val right: Boolean = false,
    val jump: Boolean = false,
    val dash: Boolean = false,
    val crouch: Boolean = false,
)
