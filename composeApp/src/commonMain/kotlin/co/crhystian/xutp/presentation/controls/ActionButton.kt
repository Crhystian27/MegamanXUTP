package co.crhystian.xutp.presentation.controls


enum class ActionButtonType {
    JUMP,    // A - Salto (funcional)
    DASH,    // B - Dash (solo UI por ahora)
    ATTACK,  // X - Ataque (solo UI por ahora)
    SPECIAL  // Y - Ataque especial (solo UI por ahora)
}

data class ActionButtonConfig(
    val type: ActionButtonType,
    val label: String,
    val sizeMultiplier: Float = 1f,
)

object ActionButtonDefaults {
    val jumpButton = ActionButtonConfig(
        type = ActionButtonType.JUMP,
        label = "A",
        sizeMultiplier = 1f,
    )
    
    val dashButton = ActionButtonConfig(
        type = ActionButtonType.DASH,
        label = "B",
        sizeMultiplier = 1f,
    )
    
    val attackButton = ActionButtonConfig(
        type = ActionButtonType.ATTACK,
        label = "X",
        sizeMultiplier = 1.4f, // 40% más grande
    )
    
    val specialButton = ActionButtonConfig(
        type = ActionButtonType.SPECIAL,
        label = "Y",
        sizeMultiplier = 1f,
    )
}
