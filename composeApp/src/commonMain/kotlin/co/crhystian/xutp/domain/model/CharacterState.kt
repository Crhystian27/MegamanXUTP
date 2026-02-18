package co.crhystian.xutp.domain.model

/**
 * Estados posibles de un personaje en el juego.
 * Cada estado determina qué animación se reproduce.
 */
enum class CharacterState {
    IDLE,
    RUNNING,
    JUMPING,
    FALLING,
    DASHING,
    CROUCHING,
    HURT,
    // Futuros: ATTACKING, SPECIAL, etc.
}
