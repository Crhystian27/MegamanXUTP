package co.crhystian.xutp.data

import co.crhystian.xutp.domain.model.CharacterState
import co.crhystian.xutp.domain.model.Direction
import org.jetbrains.compose.resources.DrawableResource
import xutp.composeapp.generated.resources.*

/**
 * Mapea cada combinación de (estado, dirección) a su lista de frames de sprite.
 * Los sprites de reposo (rez0-5) miran a la izquierda; para derecha se usa flip horizontal.
 */
object ZeroSpriteRepository {

    data class SpriteAnimation(
        val frames: List<DrawableResource>,
        val needsFlip: Boolean = false,
    )

    fun getAnimation(state: CharacterState, direction: Direction): SpriteAnimation {
        return when (state) {
            CharacterState.IDLE -> when (direction) {
                Direction.LEFT -> SpriteAnimation(idleFrames, needsFlip = false)
                Direction.RIGHT -> SpriteAnimation(idleFrames, needsFlip = true)
            }
            CharacterState.RUNNING -> when (direction) {
                Direction.RIGHT -> SpriteAnimation(runRightFrames)
                Direction.LEFT -> SpriteAnimation(runLeftFrames)
            }
            else -> when (direction) {
                Direction.LEFT -> SpriteAnimation(idleFrames, needsFlip = false)
                Direction.RIGHT -> SpriteAnimation(idleFrames, needsFlip = true)
            }
        }
    }

    private val idleFrames = listOf(
        Res.drawable.rez0,
        Res.drawable.rez1,
        Res.drawable.rez2,
        Res.drawable.rez3,
        Res.drawable.rez4,
        Res.drawable.rez5,
    )

    private val runRightFrames = listOf(
        Res.drawable.mrd0,
        Res.drawable.mrd1,
        Res.drawable.mrd2,
        Res.drawable.mrd3,
        Res.drawable.mrd4,
        Res.drawable.mrd5,
        Res.drawable.mrd6,
        Res.drawable.mrd7,
        Res.drawable.mrd8,
        Res.drawable.mrd9,
        Res.drawable.mrd10,
        Res.drawable.mrd11,
        Res.drawable.mrd12,
    )

    private val runLeftFrames = listOf(
        Res.drawable.mrzd0,
        Res.drawable.mrzd1,
        Res.drawable.mrzd2,
        Res.drawable.mrzd3,
        Res.drawable.mrzd4,
        Res.drawable.mrzd5,
        Res.drawable.mrzd6,
        Res.drawable.mrzd7,
        Res.drawable.mrzd8,
        Res.drawable.mrzd9,
        Res.drawable.mrzd10,
        Res.drawable.mrzd11,
        Res.drawable.mrzd12,
    )
}
