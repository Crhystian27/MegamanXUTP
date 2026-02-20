package co.crhystian.xutp.presentation

import androidx.lifecycle.ViewModel
import co.crhystian.xutp.data.ZeroSpriteRepository
import co.crhystian.xutp.domain.model.Character
import co.crhystian.xutp.domain.model.Direction
import co.crhystian.xutp.domain.model.GameConstants
import co.crhystian.xutp.game.AnimationController
import co.crhystian.xutp.game.GameKey
import co.crhystian.xutp.game.InputState
import co.crhystian.xutp.game.PhysicsEngine
import co.crhystian.xutp.game.audio.GameSound
import co.crhystian.xutp.game.audio.SoundPlayer
import co.crhystian.xutp.presentation.controls.ActionButtonType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel : ViewModel() {

    private val _zero = MutableStateFlow(
        Character(
            x = GameConstants.ZERO_START_X,
            y = GameConstants.PLATFORM_Y,
            direction = Direction.LEFT,
        )
    )
    val zero: StateFlow<Character> = _zero.asStateFlow()

    private val _input = MutableStateFlow(InputState())
    
    // Sound player - se inicializa desde la plataforma
    private var soundPlayer: SoundPlayer? = null
    
    fun setSoundPlayer(player: SoundPlayer) {
        soundPlayer = player
    }

    fun onGameTick(deltaTimeNanos: Long) {
        val deltaSeconds = deltaTimeNanos / 1_000_000_000f
        val deltaMs = deltaTimeNanos / 1_000_000

        val currentZero = _zero.value
        val currentInput = _input.value

        // 1. Física
        var updated = PhysicsEngine.update(currentZero, currentInput, deltaSeconds)

        // 2. Animación
        val animation = ZeroSpriteRepository.getAnimation(updated.state, updated.direction)
        updated = AnimationController.advanceFrame(updated, deltaMs, animation.frames.size)

        // 3. Sonidos (basados en eventos del frame)
        processSoundEvents(updated)

        _zero.value = updated
    }
    
    // ==================== SOUND EVENTS ====================
    
    private fun processSoundEvents(character: Character) {
        if (character.justJumped) {
            soundPlayer?.play(GameSound.JUMP)
        }
        if (character.justLanded) {
            soundPlayer?.play(GameSound.LANDING)
        }
        if (character.justDashed) {
            soundPlayer?.play(GameSound.DASH)
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        soundPlayer?.release()
    }

    /**
     * Input desde teclado físico (desktop/hardware keyboard).
     */
    fun onKeyDown(key: GameKey) {
        _input.value = when (key) {
            GameKey.LEFT -> _input.value.copy(left = true)
            GameKey.RIGHT -> _input.value.copy(right = true)
            GameKey.JUMP -> _input.value.copy(jump = true)
            GameKey.DASH -> _input.value.copy(dash = true)
            GameKey.CROUCH -> _input.value.copy(crouch = true)
        }
    }

    fun onKeyUp(key: GameKey) {
        _input.value = when (key) {
            GameKey.LEFT -> _input.value.copy(left = false)
            GameKey.RIGHT -> _input.value.copy(right = false)
            GameKey.JUMP -> _input.value.copy(jump = false)
            GameKey.DASH -> _input.value.copy(dash = false)
            GameKey.CROUCH -> _input.value.copy(crouch = false)
        }
    }

    /**
     * Input desde el D-pad virtual táctil.
     * Solo maneja movimiento horizontal (izquierda/derecha).
     */
    fun onDpadInput(left: Boolean, right: Boolean) {
        _input.value = _input.value.copy(
            left = left,
            right = right,
        )
    }

    /**
     * Input desde los botones de acción táctiles.
     * 
     * @param action Tipo de acción (JUMP, DASH, ATTACK, SPECIAL)
     * @param pressed true si se presionó, false si se soltó
     */
    fun onActionButton(action: ActionButtonType, pressed: Boolean) {
        _input.value = when (action) {
            ActionButtonType.JUMP -> _input.value.copy(jump = pressed)
            ActionButtonType.DASH -> _input.value.copy(dash = pressed)
            // ATTACK y SPECIAL no tienen efecto por ahora
            ActionButtonType.ATTACK -> _input.value
            ActionButtonType.SPECIAL -> _input.value
        }
    }
}
