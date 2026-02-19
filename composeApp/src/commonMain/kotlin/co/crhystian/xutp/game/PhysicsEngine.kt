package co.crhystian.xutp.game

import co.crhystian.xutp.domain.model.Character
import co.crhystian.xutp.domain.model.CharacterState
import co.crhystian.xutp.domain.model.Direction
import co.crhystian.xutp.domain.model.GameConstants
import kotlin.math.abs

/**
 * Motor de física puro (sin side effects).
 * Cada método es una función pura que transforma el estado.
 */
object PhysicsEngine {

    fun update(character: Character, input: InputState, deltaTime: Float): Character {
        val deltaMs = (deltaTime * 1000).toLong()
        
        return character
            .updateTimers(deltaMs)
            .processDashInput(input)
            .processDash(deltaMs)
            .processMovement(input, deltaTime)
            .processGravity(deltaTime)
            .processJump(input)
            .updatePosition(deltaTime)
            .resolveCollisions()
            .updateAnimationState(character.state)
    }

    // ==================== TIMERS ====================
    
    private fun Character.updateTimers(deltaMs: Long): Character {
        val newCooldown = (dashCooldown - deltaMs).coerceAtLeast(0L)
        val newFade = if (trailFadeRemaining > 0 && !isDashing) {
            (trailFadeRemaining - deltaMs).coerceAtLeast(0L)
        } else trailFadeRemaining
        
        return copy(
            dashCooldown = newCooldown,
            trailFadeRemaining = newFade,
            dashTrail = if (newFade <= 0 && !isDashing) emptyList() else dashTrail
        )
    }

    // ==================== DASH ====================
    
    private fun Character.processDashInput(input: InputState): Character {
        // Reset input consumed cuando se suelta el botón
        val resetConsumed = !input.dash && dashInputConsumed
        
        // Iniciar dash si es posible
        val canDash = input.dash && !dashInputConsumed && dashCooldown <= 0 && !isDashing
        
        return when {
            canDash -> copy(
                isDashing = true,
                dashTimeRemaining = GameConstants.DASH_DURATION_MS,
                dashInputConsumed = true,
                currentFrame = 0,
                frameTimeAccumulator = 0L,
            )
            resetConsumed -> copy(dashInputConsumed = false)
            else -> this
        }
    }

    private fun Character.processDash(deltaMs: Long): Character {
        if (!isDashing) return this
        
        val newDashTime = dashTimeRemaining - deltaMs
        
        return if (newDashTime <= 0) {
            endDash()
        } else {
            continueDash(newDashTime)
        }
    }

    private fun Character.endDash(): Character {
        // Velocidad 0 al terminar dash - el movimiento normal se encargará si hay input
        return copy(
            isDashing = false,
            dashTimeRemaining = 0L,
            dashCooldown = GameConstants.DASH_COOLDOWN_MS,
            velocityX = 0f,
            trailFadeRemaining = GameConstants.TRAIL_FADE_DURATION_MS,
            lastDashFrame = currentFrame,
            lastDashDirection = direction,
            currentFrame = 0,
            frameTimeAccumulator = 0L,
        )
    }

    private fun Character.continueDash(newDashTime: Long): Character {
        val dashVelocity = direction.toVelocity(GameConstants.DASH_SPEED)
        val newTrail = (listOf(x) + dashTrail).take(GameConstants.DASH_TRAIL_COUNT)
        
        return copy(
            dashTimeRemaining = newDashTime,
            velocityX = dashVelocity,
            dashTrail = newTrail,
        )
    }

    // ==================== MOVEMENT ====================
    
    private fun Character.processMovement(input: InputState, deltaTime: Float): Character {
        if (isDashing) return this
        
        val targetVelocity = input.toTargetVelocityX()
        val newVelocityX = velocityX.moveTowards(targetVelocity, deltaTime)
        val newDirection = input.toDirection() ?: direction
        
        return copy(velocityX = newVelocityX, direction = newDirection)
    }

    private fun InputState.toTargetVelocityX(): Float = when {
        left && !right -> -GameConstants.RUN_SPEED
        right && !left -> GameConstants.RUN_SPEED
        else -> 0f
    }

    private fun InputState.toDirection(): Direction? = when {
        left && !right -> Direction.LEFT
        right && !left -> Direction.RIGHT
        else -> null
    }

    private fun Float.moveTowards(target: Float, deltaTime: Float): Float {
        val rate = if (target != 0f) GameConstants.ACCELERATION else GameConstants.DECELERATION
        val maxDelta = rate * deltaTime
        return when {
            target > this -> minOf(this + maxDelta, target)
            target < this -> maxOf(this - maxDelta, target)
            else -> target
        }
    }

    // ==================== GRAVITY & JUMP ====================
    
    private fun Character.processGravity(deltaTime: Float): Character {
        if (isDashing) return copy(velocityY = 0f)
        
        val newVelocityY = (velocityY + GameConstants.GRAVITY * deltaTime)
            .coerceAtMost(GameConstants.MAX_FALL_SPEED)
        
        return copy(velocityY = newVelocityY)
    }

    private fun Character.processJump(input: InputState): Character {
        if (!input.jump || !isOnGround || isDashing) return this
        
        return copy(velocityY = GameConstants.JUMP_IMPULSE, isOnGround = false)
    }

    // ==================== POSITION & COLLISION ====================
    
    private fun Character.updatePosition(deltaTime: Float): Character = copy(
        x = x + velocityX * deltaTime,
        y = y + velocityY * deltaTime,
    )

    private fun Character.resolveCollisions(): Character {
        val groundedY = if (y >= GameConstants.PLATFORM_Y) GameConstants.PLATFORM_Y else y
        val clampedX = x.coerceIn(0f, GameConstants.ORIGINAL_WIDTH)
        
        return copy(
            x = clampedX,
            y = groundedY,
            velocityY = if (groundedY == GameConstants.PLATFORM_Y) 0f else velocityY,
            isOnGround = groundedY == GameConstants.PLATFORM_Y,
        )
    }

    // ==================== ANIMATION STATE ====================
    
    private fun Character.updateAnimationState(previousState: CharacterState): Character {
        val isMoving = abs(velocityX) > 5f
        val newState = when {
            isDashing -> CharacterState.DASHING
            !isOnGround && velocityY < 0 -> CharacterState.JUMPING
            !isOnGround && velocityY >= 0 -> CharacterState.FALLING
            isMoving -> CharacterState.RUNNING
            else -> CharacterState.IDLE
        }
        
        val resetFrame = newState != previousState
        return copy(
            state = newState,
            currentFrame = if (resetFrame) 0 else currentFrame,
            frameTimeAccumulator = if (resetFrame) 0L else frameTimeAccumulator,
        )
    }

    // ==================== HELPERS ====================
    
    private fun Direction.toVelocity(speed: Float): Float = when (this) {
        Direction.RIGHT -> speed
        Direction.LEFT -> -speed
    }
}
