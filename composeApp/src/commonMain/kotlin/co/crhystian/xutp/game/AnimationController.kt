package co.crhystian.xutp.game

import co.crhystian.xutp.domain.model.Character
import co.crhystian.xutp.domain.model.CharacterState
import co.crhystian.xutp.domain.model.GameConstants

/**
 * Avanza el frame de animación según el tiempo acumulado.
 * Usa duraciones distintas por estado para que cada animación se sienta natural.
 */
object AnimationController {

    fun advanceFrame(character: Character, deltaTimeMs: Long, totalFrames: Int): Character {
        if (totalFrames <= 1) return character.copy(currentFrame = 0)

        val frameDuration = getFrameDuration(character.state)
        val newAccumulator = character.frameTimeAccumulator + deltaTimeMs

        return if (newAccumulator >= frameDuration) {
            // Solo avanzar 1 frame por tick, evitar saltar frames
            val nextFrame = (character.currentFrame + 1) % totalFrames
            character.copy(
                currentFrame = nextFrame,
                frameTimeAccumulator = newAccumulator - frameDuration
            )
        } else {
            character.copy(frameTimeAccumulator = newAccumulator)
        }
    }

    private fun getFrameDuration(state: CharacterState): Long {
        return when (state) {
            CharacterState.IDLE -> GameConstants.IDLE_FRAME_DURATION_MS
            CharacterState.RUNNING -> GameConstants.RUN_FRAME_DURATION_MS
            CharacterState.DASHING -> GameConstants.DASH_FRAME_DURATION_MS
            else -> GameConstants.RUN_FRAME_DURATION_MS
        }
    }
}
