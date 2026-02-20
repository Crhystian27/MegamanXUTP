@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package co.crhystian.xutp.game.audio

import platform.AVFAudio.AVAudioPlayer
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.setActive
import platform.Foundation.NSBundle
import platform.Foundation.NSURL

/**
 * Implementación iOS usando AVAudioPlayer.
 */
actual class SoundPlayer {
    private val players = mutableMapOf<GameSound, AVAudioPlayer?>()
    
    init {
        // Configurar sesión de audio
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, null)
        session.setActive(true, null)
        
        // Precargar sonidos
        GameSound.entries.forEach { sound ->
            val url = NSBundle.mainBundle.URLForResource(
                name = sound.fileName.removeSuffix(".wav"),
                withExtension = "wav",
                subdirectory = "compose-resources/files/sounds"
            )
            if (url != null) {
                players[sound] = AVAudioPlayer(url, null)?.apply {
                    prepareToPlay()
                }
            }
        }
    }
    
    actual fun play(sound: GameSound) {
        players[sound]?.let { player ->
            if (player.isPlaying()) {
                player.currentTime = 0.0
            }
            player.play()
        }
    }
    
    actual fun release() {
        players.values.forEach { it?.stop() }
        players.clear()
    }
}
