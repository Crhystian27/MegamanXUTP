package co.crhystian.xutp.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import co.crhystian.xutp.data.ZeroSpriteRepository
import co.crhystian.xutp.domain.model.Character
import co.crhystian.xutp.domain.model.CharacterState
import co.crhystian.xutp.domain.model.GameConstants
import co.crhystian.xutp.game.GameKey
import co.crhystian.xutp.game.audio.LocalSoundPlayer
import co.crhystian.xutp.presentation.controls.ActionButtonsOverlay
import co.crhystian.xutp.presentation.controls.DpadOverlay
import co.crhystian.xutp.presentation.effects.TrailColors
import co.crhystian.xutp.presentation.effects.TrailEffectConfig
import co.crhystian.xutp.presentation.effects.drawSpriteTrail
import org.jetbrains.compose.resources.imageResource
import xutp.composeapp.generated.resources.Res
import xutp.composeapp.generated.resources.escenario_de_pelea
import xutp.composeapp.generated.resources.plataforma

/**
 * Pantalla principal del juego.
 * 
 * Responsabilidades:
 * - Renderizar el canvas del juego (fondo, plataforma, personaje)
 * - Manejar el game loop con delta time
 * - Coordinar inputs de teclado y controles táctiles
 * 
 * Los controles táctiles están separados en componentes independientes
 * siguiendo el principio de responsabilidad única.
 */
@Composable
fun GameScreen(viewModel: GameViewModel = viewModel { GameViewModel() }) {
    val zero by viewModel.zero.collectAsState()
    val focusRequester = remember { FocusRequester() }
    
    // Obtener SoundPlayer del CompositionLocal y pasarlo al ViewModel
    val soundPlayer = LocalSoundPlayer.current
    LaunchedEffect(soundPlayer) {
        soundPlayer?.let { viewModel.setSoundPlayer(it) }
    }

    // Game loop
    LaunchedEffect(Unit) {
        var lastFrameNanos = 0L
        while (true) {
            withFrameNanos { frameTimeNanos ->
                if (lastFrameNanos != 0L) {
                    val delta = frameTimeNanos - lastFrameNanos
                    // Clamp delta para evitar saltos enormes (ej: al pausar/resumir)
                    val clampedDelta = delta.coerceAtMost(50_000_000L) // max 50ms
                    viewModel.onGameTick(clampedDelta)
                }
                lastFrameNanos = frameTimeNanos
            }
        }
    }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Precargar imágenes
    val backgroundImage = imageResource(Res.drawable.escenario_de_pelea)
    val platformImage = imageResource(Res.drawable.plataforma)

    // Frame actual de Zero
    val animation = ZeroSpriteRepository.getAnimation(zero.state, zero.direction)
    val frameIndex = zero.currentFrame.coerceIn(0, animation.frames.size - 1)
    val zeroImage = imageResource(animation.frames[frameIndex])
    val needsFlip = animation.needsFlip

    // Sprite para el trail del dash (último frame del dash durante el fade)
    val dashTrailAnimation = ZeroSpriteRepository.getAnimation(
        CharacterState.DASHING,
        zero.lastDashDirection
    )
    val dashTrailFrameIndex = zero.lastDashFrame.coerceIn(0, dashTrailAnimation.frames.size - 1)
    val dashTrailImage = imageResource(dashTrailAnimation.frames[dashTrailFrameIndex])
    val dashTrailFlip = dashTrailAnimation.needsFlip

    Box(modifier = Modifier.fillMaxSize()) {
        // Canvas del juego
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusRequester)
                .focusable()
                .onPreviewKeyEvent { event ->
                    val gameKey = mapKeyToGameKey(event.key)
                    if (gameKey != null) {
                        when (event.type) {
                            KeyEventType.KeyDown -> viewModel.onKeyDown(gameKey)
                            KeyEventType.KeyUp -> viewModel.onKeyUp(gameKey)
                        }
                        true
                    } else {
                        false
                    }
                }
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height
            val scaleX = canvasWidth / GameConstants.ORIGINAL_WIDTH
            val scaleY = canvasHeight / GameConstants.ORIGINAL_HEIGHT

            // Fondo
            drawImage(
                image = backgroundImage,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(backgroundImage.width, backgroundImage.height),
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(canvasWidth.toInt(), canvasHeight.toInt()),
                filterQuality = FilterQuality.Low,
            )

            // Plataforma
            val platH = (GameConstants.ORIGINAL_HEIGHT - GameConstants.PLATFORM_Y) * scaleY
            drawImage(
                image = platformImage,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(platformImage.width, platformImage.height),
                dstOffset = IntOffset(0, (GameConstants.PLATFORM_Y * scaleY).toInt()),
                dstSize = IntSize(canvasWidth.toInt(), platH.toInt()),
                filterQuality = FilterQuality.Low,
            )

            // Dash trail
            val showTrail = (zero.isDashing || zero.trailFadeRemaining > 0) && zero.dashTrail.isNotEmpty()
            if (showTrail) {
                val trailImage = if (zero.isDashing) zeroImage else dashTrailImage
                val trailFlip = if (zero.isDashing) needsFlip else dashTrailFlip
                val fadeMultiplier = if (zero.isDashing) 1f 
                    else zero.trailFadeRemaining.toFloat() / GameConstants.TRAIL_FADE_DURATION_MS
                
                drawSpriteTrail(
                    image = trailImage,
                    trailPositions = zero.dashTrail,
                    currentY = zero.y,
                    scaleX = scaleX,
                    scaleY = scaleY,
                    flip = trailFlip,
                    fadeMultiplier = fadeMultiplier,
                    config = TrailEffectConfig(
                        trailColor = TrailColors.ZERO_RED,
                        borderColor = TrailColors.ZERO_BORDER,
                    ),
                )
            }

            // Zero (sprite principal, siempre encima del trail)
            drawCharacterSprite(zero, zeroImage, scaleX, scaleY, needsFlip)
        }

        // D-pad virtual (lado izquierdo) - solo movimiento horizontal
        DpadOverlay(
            onDirectionChange = { left, right ->
                viewModel.onDpadInput(left, right)
            }
        )

        // Botones de acción (lado derecho) - A: salto, B: dash, X: ataque, Y: especial
        ActionButtonsOverlay(
            onActionPressed = { action ->
                viewModel.onActionButton(action, pressed = true)
            },
            onActionReleased = { action ->
                viewModel.onActionButton(action, pressed = false)
            }
        )
    }
}

private fun DrawScope.drawCharacterSprite(
    character: Character,
    image: androidx.compose.ui.graphics.ImageBitmap,
    scaleX: Float,
    scaleY: Float,
    flip: Boolean,
) {
    val spriteScale = minOf(scaleX, scaleY) * 1.2f
    val spriteW = (image.width * spriteScale).toInt()
    val spriteH = (image.height * spriteScale).toInt()

    val centerX = character.x * scaleX
    val drawX = (centerX - spriteW / 2f).toInt()
    val drawY = (character.y * scaleY - spriteH).toInt()

    if (flip) {
        scale(
            scaleX = -1f,
            scaleY = 1f,
            pivot = Offset(centerX, (drawY + spriteH / 2f))
        ) {
            drawImage(
                image = image,
                srcOffset = IntOffset.Zero,
                srcSize = IntSize(image.width, image.height),
                dstOffset = IntOffset(drawX, drawY),
                dstSize = IntSize(spriteW, spriteH),
                filterQuality = FilterQuality.Low,
            )
        }
    } else {
        drawImage(
            image = image,
            srcOffset = IntOffset.Zero,
            srcSize = IntSize(image.width, image.height),
            dstOffset = IntOffset(drawX, drawY),
            dstSize = IntSize(spriteW, spriteH),
            filterQuality = FilterQuality.Low,
        )
    }
}

private fun mapKeyToGameKey(key: Key): GameKey? {
    return when (key) {
        Key.DirectionLeft, Key.A -> GameKey.LEFT
        Key.DirectionRight, Key.D -> GameKey.RIGHT
        Key.DirectionUp, Key.W, Key.K, Key.Spacebar -> GameKey.JUMP
        Key.DirectionDown, Key.S -> GameKey.CROUCH
        Key.J, Key.ShiftLeft -> GameKey.DASH
        else -> null
    }
}
