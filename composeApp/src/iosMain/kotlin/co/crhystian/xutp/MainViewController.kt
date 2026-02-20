package co.crhystian.xutp

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import co.crhystian.xutp.game.audio.LocalSoundPlayer
import co.crhystian.xutp.game.audio.SoundPlayer

fun MainViewController() = ComposeUIViewController {
    val soundPlayer = SoundPlayer()
    
    CompositionLocalProvider(LocalSoundPlayer provides soundPlayer) {
        App()
    }
}