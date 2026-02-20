package co.crhystian.xutp

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import co.crhystian.xutp.game.audio.LocalSoundPlayer
import co.crhystian.xutp.game.audio.SoundPlayer

class MainActivity : ComponentActivity() {
    private var soundPlayer: SoundPlayer? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Pantalla completa inmersiva para el juego
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val controller = WindowInsetsControllerCompat(window, window.decorView)
        controller.hide(WindowInsetsCompat.Type.systemBars())
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Mantener pantalla encendida durante el juego
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        // Inicializar SoundPlayer
        soundPlayer = SoundPlayer().apply { initialize(this@MainActivity) }

        setContent {
            CompositionLocalProvider(LocalSoundPlayer provides soundPlayer) {
                App()
            }
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        soundPlayer?.release()
    }
}
