package co.crhystian.xutp.game.audio

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Tipos de sonidos del juego.
 * Cada sonido corresponde a un archivo en composeResources/files/sounds/
 */
enum class GameSound(val fileName: String) {
    JUMP("jumpZero.wav"),
    LANDING("landingZero.wav"),
    DASH("dashZero.wav"),
}

/**
 * Interfaz para reproducir sonidos del juego.
 * Implementación específica por plataforma via expect/actual.
 */
expect class SoundPlayer() {
    /**
     * Reproduce un sonido una vez.
     * No bloquea - el sonido se reproduce en background.
     */
    fun play(sound: GameSound)
    
    /**
     * Libera recursos del reproductor.
     * Llamar cuando ya no se necesite.
     */
    fun release()
}

/**
 * CompositionLocal para acceder al SoundPlayer desde cualquier Composable.
 * Cada plataforma provee su implementación.
 */
val LocalSoundPlayer = staticCompositionLocalOf<SoundPlayer?> { null }
