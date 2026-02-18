package co.crhystian.xutp.game

import co.crhystian.xutp.domain.model.Character
import co.crhystian.xutp.domain.model.CharacterState
import co.crhystian.xutp.domain.model.Direction
import co.crhystian.xutp.domain.model.GameConstants

/**
 * Motor de física puro (sin side effects).
 * Recibe el estado actual + input + deltaTime y retorna el nuevo estado.
 *
 * Usa aceleración/desaceleración para que el movimiento se sienta gradual:
 * - Al presionar una tecla, Zero acelera progresivamente hasta RUN_SPEED.
 * - Al soltar, desacelera hasta detenerse.
 * - Esto evita que un "medio pulso" mueva mucha distancia.
 */
object PhysicsEngine {

    fun update(character: Character, input: InputState, deltaTime: Float): Character {
        var c = character

        // Determinar velocidad objetivo según input
        val targetVelocityX = when {
            input.left && !input.right -> -GameConstants.RUN_SPEED
            input.right && !input.left -> GameConstants.RUN_SPEED
            else -> 0f
        }

        // Aplicar aceleración/desaceleración suave
        val currentVx = c.velocityX
        val newVelocityX = if (targetVelocityX != 0f) {
            // Acelerando hacia la velocidad objetivo
            moveTowards(currentVx, targetVelocityX, GameConstants.ACCELERATION * deltaTime)
        } else {
            // Frenando: desacelerar hacia 0
            moveTowards(currentVx, 0f, GameConstants.DECELERATION * deltaTime)
        }
        c = c.copy(velocityX = newVelocityX)

        // Actualizar dirección solo cuando hay input activo
        c = when {
            input.left && !input.right -> c.copy(direction = Direction.LEFT)
            input.right && !input.left -> c.copy(direction = Direction.RIGHT)
            else -> c
        }

        // Aplicar gravedad
        var newVelocityY = c.velocityY + GameConstants.GRAVITY * deltaTime
        if (newVelocityY > GameConstants.MAX_FALL_SPEED) {
            newVelocityY = GameConstants.MAX_FALL_SPEED
        }

        // Salto: solo si está en el suelo
        if (input.jump && c.isOnGround) {
            newVelocityY = GameConstants.JUMP_IMPULSE
            c = c.copy(isOnGround = false)
        }
        c = c.copy(velocityY = newVelocityY)

        // Actualizar posición
        var newX = c.x + c.velocityX * deltaTime
        var newY = c.y + c.velocityY * deltaTime

        // Colisión con el suelo (plataforma)
        if (newY >= GameConstants.PLATFORM_Y) {
            newY = GameConstants.PLATFORM_Y
            c = c.copy(velocityY = 0f, isOnGround = true)
        }

        // Limitar a los bordes del escenario
        newX = newX.coerceIn(0f, GameConstants.ORIGINAL_WIDTH)

        c = c.copy(x = newX, y = newY)

        // Determinar estado de animación
        // Usar un umbral pequeño para evitar que velocidades residuales
        // de la desaceleración mantengan el estado RUNNING
        val isMoving = kotlin.math.abs(c.velocityX) > 5f
        val newState = when {
            !c.isOnGround && c.velocityY < 0 -> CharacterState.JUMPING
            !c.isOnGround && c.velocityY >= 0 -> CharacterState.FALLING
            isMoving -> CharacterState.RUNNING
            else -> CharacterState.IDLE
        }
        c = c.copy(state = newState)

        // Resetear frame si cambió de estado
        if (newState != character.state) {
            c = c.copy(currentFrame = 0, frameTimeAccumulator = 0L)
        }

        return c
    }

    /**
     * Mueve [current] hacia [target] en incrementos de [maxDelta].
     * Nunca sobrepasa el target.
     */
    private fun moveTowards(current: Float, target: Float, maxDelta: Float): Float {
        return when {
            target > current -> minOf(current + maxDelta, target)
            target < current -> maxOf(current - maxDelta, target)
            else -> target
        }
    }
}
