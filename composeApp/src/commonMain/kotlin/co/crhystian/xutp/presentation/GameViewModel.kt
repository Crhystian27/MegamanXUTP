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

        _zero.value = updated
    }

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
     * Reemplaza completamente el estado de input horizontal.
     */
    fun onDpadInput(left: Boolean, right: Boolean, up: Boolean, down: Boolean) {
        _input.value = _input.value.copy(
            left = left,
            right = right,
            jump = up,
            crouch = down,
        )
    }
}
