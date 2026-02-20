package co.crhystian.xutp.game.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool

/**
 * Implementación Android usando SoundPool.
 * SoundPool es ideal para efectos cortos de juegos.
 */
actual class SoundPlayer {
    private var soundPool: SoundPool? = null
    private val soundIds = mutableMapOf<GameSound, Int>()
    private var context: Context? = null
    
    actual fun play(sound: GameSound) {
        val id = soundIds[sound] ?: return
        soundPool?.play(id, 1f, 1f, 1, 0, 1f)
    }
    
    actual fun release() {
        soundPool?.release()
        soundPool = null
        soundIds.clear()
    }
    
    /**
     * Inicializa el SoundPool con el contexto de Android.
     * Debe llamarse antes de usar play().
     */
    fun initialize(context: Context) {
        this.context = context
        
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        
        soundPool = SoundPool.Builder()
            .setMaxStreams(4)
            .setAudioAttributes(audioAttributes)
            .build()
        
        // Cargar sonidos desde assets
        GameSound.entries.forEach { sound ->
            val assetFd = context.assets.openFd("files/sounds/${sound.fileName}")
            val id = soundPool?.load(assetFd, 1) ?: 0
            soundIds[sound] = id
        }
    }
}
