package co.crhystian.xutp.domain.model

/**
 * Constantes del juego basadas en el diseño original de 511x384.
 * Las posiciones se escalan proporcionalmente al tamaño real de pantalla.
 */
object GameConstants {
    // Dimensiones del escenario original (referencia para escalar)
    const val ORIGINAL_WIDTH = 511f
    const val ORIGINAL_HEIGHT = 384f

    // Velocidades en unidades del escenario original por segundo
    // Original: 8 px/frame a ~60fps = ~480 px/s, ajustado para sentirse bien
    const val RUN_SPEED = 80f
    // Dash: más rápido que correr, proporcional al original (12/8 = 1.5x)
    const val DASH_SPEED = 200f

    // Aceleración: cuánto tarda en llegar a velocidad máxima
    // A 400 px/s², llegar a 80 px/s toma 0.2 segundos (arranque gradual)
    const val ACCELERATION = 400f
    // Desaceleración: cuánto tarda en frenar al soltar la tecla
    const val DECELERATION = 600f

    // Gravedad y salto
    const val GRAVITY = 900f
    const val JUMP_IMPULSE = -350f
    const val MAX_FALL_SPEED = 600f

    // Posiciones iniciales (en coordenadas del escenario original)
    const val ZERO_START_X = 400f
    const val ZERO_START_Y = 280f

    // Plataforma
    const val PLATFORM_Y = 332f

    // Dash: 14 frames de animación, más rápido
    const val DASH_DURATION_MS = 500L
    const val DASH_COOLDOWN_MS = 200L
    const val DASH_TRAIL_COUNT = 9 // Número de ghosts en el trail
    const val TRAIL_FADE_DURATION_MS = 200L // Duración del fade out post-dash

    // Animación: duración de cada frame en milisegundos
    const val IDLE_FRAME_DURATION_MS = 150L
    const val RUN_FRAME_DURATION_MS = 65L
    const val DASH_FRAME_DURATION_MS = 36L // 14 frames en 500ms ≈ 36ms por frame
}
