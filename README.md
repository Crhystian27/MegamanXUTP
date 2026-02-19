# XUTP — Megaman X4 Fighting Game (KMP)

Port de un juego de pelea 2D estilo Megaman X4, originalmente desarrollado en Java con Greenfoot como proyecto universitario. Ahora portado a Android e iOS con Kotlin Multiplatform y Compose Multiplatform.

<p style="text-align: center;">
  <img src="docs/images/escenario.png" alt="Escenario de pelea" style="width: 500px; max-width: 100%;" />
</p>

## ¿Qué puedes hacer?

### Movimiento fluido
Controla a Zero con el D-pad táctil o teclado. El personaje acelera y desacelera de forma gradual, dando una sensación de peso y control real, no movimientos robóticos.

### Salto con física real
Zero salta con impulso inicial y cae con gravedad. Puedes controlar la altura según cuánto mantengas presionado el botón.

### Dash con efecto visual
Presiona el botón B para ejecutar un dash rápido. Zero se desplaza a alta velocidad dejando un rastro de siluetas rojas que se desvanecen gradualmente, creando un efecto de movimiento dinámico y visualmente atractivo.

```
   [····]  [···]  [··]  [·]  [ZERO] →
   siluetas rojas con opacidad decreciente
```

El efecto de trail está diseñado para ser reutilizable: cada personaje puede tener su propio color característico (rojo para Zero, azul para Megaman, etc.).

### Controles táctiles optimizados
D-pad virtual en el lado izquierdo para movimiento, botones de acción en el lado derecho siguiendo el layout clásico de consolas:
- **A** (arriba): Saltar
- **B** (abajo derecha): Dash
- **X** (centro): Ataque (próximamente)
- **Y** (abajo izquierda): Especial (próximamente)

## Controles

| Input | Acción |
|---|---|
| ← → / A D | Mover |
| ↑ / W / K / Espacio | Saltar |
| J / Shift | Dash |
| D-pad táctil | Mover (pantalla táctil) |
| Botón A | Saltar (táctil) |
| Botón B | Dash (táctil) |

## Plataformas

- **Android** (minSdk 29) — APK listo para instalar
- **iOS** (arm64) — Compilar desde Xcode

Ambas plataformas comparten el 100% del código de juego gracias a Kotlin Multiplatform.

## Stack técnico

| Tecnología | Uso |
|---|---|
| Kotlin 2.3.0 | Lenguaje principal |
| Compose Multiplatform 1.10.0 | UI y Canvas 2D |
| KMP | Código compartido Android / iOS |

## Build

```bash
# Android APK
./gradlew :composeApp:assembleDebug

# iOS (requiere Xcode)
# Abrir iosApp/iosApp.xcodeproj
```

## Sprites incluidos

| Animación | Descripción |
|---|---|
| Reposo | Zero en posición de espera, respirando |
| Correr | Ciclo completo de carrera en ambas direcciones |
| Dash | Movimiento rápido con pose aerodinámica |

Escenario original: 511×384 px, escalado automáticamente a cualquier pantalla.

---

*Proyecto educativo — sprites y concepto original de Capcom (Megaman X4)*
