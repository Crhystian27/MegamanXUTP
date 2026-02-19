package co.crhystian.xutp.domain.model

/**
 * Representa el estado mutable de un personaje en el juego.
 * Contiene posición, velocidad, estado de animación y dirección.
 *
 * Las coordenadas están en el espacio del escenario original (511x384).
 * El renderizado se encarga de escalar a la pantalla real.
 */
data class Character(
    val x: Float,
    val y: Float,
    val velocityX: Float = 0f,
    val velocityY: Float = 0f,
    val state: CharacterState = CharacterState.IDLE,
    val direction: Direction = Direction.RIGHT,
    val isOnGround: Boolean = true,
    val currentFrame: Int = 0,
    val frameTimeAccumulator: Long = 0L,
    // Dash state
    val isDashing: Boolean = false,
    val dashTimeRemaining: Long = 0L,
    val dashCooldown: Long = 0L,
    val dashInputConsumed: Boolean = false,
    // Trail effect
    val dashTrail: List<Float> = emptyList(),
    val trailFadeRemaining: Long = 0L,
    val lastDashFrame: Int = 0, // Último frame del dash para el fade
    val lastDashDirection: Direction = Direction.RIGHT, // Dirección del dash para el fade
)
