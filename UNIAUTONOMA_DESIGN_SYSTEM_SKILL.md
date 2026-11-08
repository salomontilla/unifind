---
name: uniautonoma-design
description: >-
  Sistema oficial de diseño e identidad institucional de la Corporación Universitaria Autónoma del Cauca.
  Aplica para aplicaciones móviles Android (Jetpack Compose / XML) y plataformas web (Next.js / React / Tailwind CSS).
  Define la paleta de colores corporativa, la familia tipográfica Museo Sans Rounded, iconografía, patrones UX/UI,
  componentes certificados (Dock Bar, BottomSheets, Snackbars institucionales, Shimmer Skeletons), reglas anti-obsolescencia
  y directrices de accesibilidad y contraste.
---

# 🏛️ Sistema Oficial de Diseño Institucional: Uniautónoma del Cauca
## Guía de Arquitectura Visual, Tokens de Diseño y Componentes Certificados

Este manual define la especificación canónica de identidad visual y experiencia de usuario (UX/UI) de la **Corporación Universitaria Autónoma del Cauca (Popayán)** para proyectos móviles (Android Jetpack Compose / XML) y proyectos web (Next.js / React / Tailwind CSS).

---

## 1. Filosofía de Diseño y Principios Rectores

Toda interfaz construida bajo la marca Uniautónoma debe transmitir:
1. **Confianza Institucional:** Sobriedad académica, estabilidad y seguridad en el tratamiento de información.
2. **Modernidad y Vanguardia:** Estética *mobile-first*, contenedores con esquinas redondeadas suaves, micro-interacciones fluidas y componentes sin bordes toscos.
3. **Simplicidad Funcional:** Flujos rápidos (< 30 segundos), reducción radical de la carga cognitiva y erradicación de elementos decorativos innecesarios.
4. **Sentido de Pertenencia:** Integración armónica de los símbolos universitarios (el Grifo, el azul marino de la institución y el naranja de bienestar).

### 🚫 Reglas de Oro (Do's & Don'ts)

| Lo que NUNCA se debe hacer (Don'ts) | Lo que SIEMPRE se debe hacer (Do's) |
| :--- | :--- |
| **❌ NUNCA usar Toasts nativos del SO ni AlertDialogs toscos:** Rompen la experiencia visual e interrumpen el flujo del usuario. | **✅ Usar `ModalBottomSheet` para modales y `UniautonomaSnackbar` o validaciones inline:** Animaciones suaves y hojas deslizables desde abajo. |
| **❌ NUNCA usar emojis informales en botones o títulos de acción:** Emojis como 🔍, 🚨, 📦, 🙋‍♂️ abaratan la interfaz. | **✅ Usar iconografía vectorial consistente:** Material Icons en Compose y Lucide Icons en Web, coloreados con la paleta oficial. |
| **❌ NUNCA usar gradientes multicolores o estirar logotipos:** Deforma la identidad corporativa. | **✅ Respetar relaciones de aspecto:** Usar `ContentScale.Fit` para logos y `ContentScale.Crop` o `cover` para fondos. |
| **❌ NUNCA usar negro puro (`#000000`) para textos o fondos:** Produce contrastes agresivos que fatigan la vista. | **✅ Usar Azul Marino Institucional (`#062653`) o Azul Texto (`#142B4D`):** Jerarquía visual elegante y profesional. |

---

## 2. Paleta Oficial de Colores (Color Tokens)

La paleta cromática se compone de dos tonos institucionales dominantes, acentos energéticos de bienestar y superficies neutras limpias:

```
┌────────────────────────────────────────────────────────────────────────┐
│  PRIMARIOS:                                                            │
│  [ #062653 ] Azul Marino Institucional (Navy)                          │
│  [ #0756A5 ] Azul Corporativo Uniautónoma (Blue Primary)              │
│  [ #2476C5 ] Azul Complementario / Interactivo (Blue Light)           │
│                                                                        │
│  ACENTOS Y ESTADOS:                                                    │
│  [ #F58220 ] Naranja Institucional Bienestar (Orange Accent)           │
│  [ #10B981 ] Verde Éxito Institucional (Green Success)                 │
│  [ #EF4444 ] Rojo Alerta / Error Crítico (Red Alert)                  │
│                                                                        │
│  SUPERFICIES Y TEXTOS:                                                 │
│  [ #F5F7FC ] Fondo Claro del Lienzo (Screen Background)                │
│  [ #FFFFFF ] Blanco Superficie Puro (Surface White)                    │
│  [ #142B4D ] Texto Principal (Text Primary)                            │
│  [ #718096 ] Texto Secundario / Desactivado (Text Muted)               │
│  [ #E2E8F0 ] Borde Sutil de Tarjetas (Card Stroke)                     │
└────────────────────────────────────────────────────────────────────────┘
```

### Tabla de Equivalencias y Especificaciones Técnicas

| Token de Color | Hex | RGB | Jetpack Compose (Kotlin) | Tailwind CSS v4 / CSS | Uso Principal |
| :--- | :---: | :---: | :--- | :--- | :--- |
| **Navy Dark** | `#041A38` | `4, 26, 56` | `Color(0xFF041A38)` | `bg-[#041a38]` | Sidebars web, status bars profundas y contrastes altos. |
| **Navy Institucional** | `#062653` | `6, 38, 83` | `Color(0xFF062653)` | `bg-[#062653]`, `text-[#062653]` | Cabeceras de pantalla, títulos principales, snackbars flotantes. |
| **Azul Corporativo** | `#0756A5` | `7, 86, 165` | `Color(0xFF0756A5)` | `bg-[#0756a5]`, `text-[#0756a5]` | Botones primarios de acción, enlaces interactivos, tabs activos. |
| **Azul Complementario**| `#2476C5` | `36, 118, 197`| `Color(0xFF2476C5)` | `bg-[#2476c5]` | Píldoras activas, banners informativos, hovers interactivos. |
| **Naranja Institucional**| `#F58220` | `245, 130, 32`| `Color(0xFFF58220)` | `bg-[#f58220]`, `text-[#f58220]` | Acentos de marca, badges de custodia en portería, Puntos de Honor. |
| **Lienzo Claro** | `#F5F7FC` | `245, 247, 252`| `Color(0xFFF5F7FC)` | `bg-[#f5f7fc]` | Fondo global de pantallas móviles y vistas web. |
| **Blanco Puro** | `#FFFFFF` | `255, 255, 255`| `Color(0xFFFFFFFF)` | `bg-white`, `text-white` | Tarjetas de contenido, modal bottom sheets, contenedores elevados. |
| **Texto Primario** | `#142B4D` | `20, 43, 77` | `Color(0xFF142B4D)` | `text-[#142b4d]` | Cuerpo de texto, nombres de objetos, etiquetas de formularios. |
| **Texto Secundario** | `#718096` | `113, 128, 150`| `Color(0xFF718096)` | `text-[#718096]` | Metadatos (fechas, códigos secundarios, descripciones auxiliares). |
| **Verde Éxito** | `#10B981` | `16, 185, 129`| `Color(0xFF10B981)` | `bg-emerald-500`, `text-emerald-700` | Estados "Devuelto", confirmación de tickets, checks de validación. |
| **Borde Tarjeta** | `#E2E8F0` | `226, 232, 240`| `Color(0xFFE2E8F0)` | `border-slate-200` | Separadores y contornos sutiles de tarjetas. |

---

## 3. Tipografía Oficial: Museo Sans Rounded

La identidad institucional exige el uso exclusivo de la familia tipográfica geométrica de esquinas redondeadas **Museo Sans Rounded**, la cual combina formalidad corporativa con calidez ergonómica:

### 3.1 Pesos Oficiales y Jerarquía de Uso
1. **Thin (100) & Light (300):** Subtítulos etéreos o marcas de agua decorativas.
2. **Regular (500):** Cuerpo de texto estándar, descripciones de objetos y párrafos institucionales.
3. **Bold (700):** Títulos de secciones, etiquetas de botones primarios y encabezados de tarjetas.
4. **Black (900) & ExtraBlack (1000):** Saldo numérico de Puntos de Honor, tokens de tickets (`TICKET-XXXX-RET`) y logotipos tipográficos.

### 3.2 Implementación en Jetpack Compose (Android)

```kotlin
// app/src/main/java/.../ui/theme/Type.kt
val MuseoSansRounded = FontFamily(
    Font(R.font.museo_sans_rounded_100, FontWeight.Thin),
    Font(R.font.museo_sans_rounded_300, FontWeight.Light),
    Font(R.font.museo_sans_rounded_500, FontWeight.Normal),
    Font(R.font.museo_sans_rounded_700, FontWeight.Bold),
    Font(R.font.museo_sans_rounded_900, FontWeight.Black),
    Font(R.font.museo_sans_rounded_1000, FontWeight.ExtraBold)
)
```

---

## 4. Recursos Gráficos Institucionales y Fondo Oficial

### 4.1 Logotipos y Emblemas
1. **Logotipo Oficial Horizontal:** Debe ubicarse siempre con su margen de respeto, fondo transparente y proporción vectorial intacta (evitar estiramiento manual).
2. **Emblema del Grifo Universitario:** Utilizado en avatares, sellos de autenticación digital y favicons.

### 4.2 Imagen de Fondo Institucional (`bg_login_uniautonoma.png`)
Diseñada específicamente para las pantallas de acceso seguro y bienvenida:
* **Composición:** Ondas azules institucionales, curvas naranjas de bienestar, marca de agua del emblema y arquitectura del campus en segundo plano.
* **Regla de Implementación Móvil (Compose):**
  ```kotlin
  Image(
      painter = painterResource(id = R.drawable.bg_login_uniautonoma),
      contentDescription = null,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop,
      alignment = Alignment.TopCenter
  )
  ```
* **Regla de Implementación Web (Tailwind):**
  ```css
  background-image: url('/assets/bg_login_uniautonoma.png');
  background-size: cover;
  background-position: center top;
  background-repeat: no-repeat;
  ```

---

## 5. Patrones y Componentes Certificados

### Componente 1: Cápsula Inferior Dock (`UnifindBottomBar`)
* **Propósito:** Barra de navegación flotante moderna que no bloquea el scroll ni genera recuadros grises inferiores.
* **Geometría:** `height = 68.dp`, `shape = RoundedCornerShape(34.dp)`, `color = SurfaceWhite`, `shadowElevation = 10.dp`, `tonalElevation = 0.dp` *(crítico para evitar el tinte grisáceo de M3)*.
* **Botón Central de Acción `(+)`:** Píldora o círculo en `UniautonomaBlue` con icono en `UniautonomaOrange` o blanco para disparar la acción principal de la pantalla.

### Componente 2: Sistema de Notificaciones Sin Popups (`UniautonomaFeedback`)
* **Propósito:** Erradicar definitivamente los Toasts de Android.
* **Snackbar Flotante:** Contenedor tipo píldora flotante con fondo `UniautonomaNavy` (`#062653`), borde sutil según el estado (`GreenSuccess`, `UniautonomaOrange` o `BlueLight`), sombra de 8.dp y texto en blanco.
* **ModalBottomSheet Informativo (`InstitutionInfoSheet`):** Para presentar textos reglamentarios (Ley 1581 de 2012, Puntos Seguros, Mesa de Ayuda) de forma deslizable con `RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)` y botón de cierre "Entendido".

### Componente 3: Ticket Digital de Portería (`TICKET-XXXX-RET`)
* **Propósito:** Protocolo visual de retiro de objetos bajo custodia oficial en portería.
* **Estructura:**
  1. Icono de ticket en círculo verde de verificación.
  2. Caja destacada en azul marino con texto en naranja: *"CÓDIGO DE ENTREGA VERIFICADO"*.
  3. Código grande en negrita (ej. `TICKET-4091-RET`).
  4. Indicador de ubicación de sede (ej. *Portería Principal • Sede La Casona*).
  5. Instrucción clara de portar el carné institucional para la entrega física.

### Componente 4: Estados de Carga Shimmer Skeleton
* **Propósito:** Eliminar spinners genéricos que congelan la pantalla.
* **Técnica:** Animación de degradado continuo con `rememberInfiniteTransition` y `Brush.linearGradient` entre `Slate200` y `Slate100`, dibujando la silueta geométrica de las tarjetas antes de que carguen los datos.

### Componente 5: Gamificación y Puntos de Honor
* **Fórmula de Honor:** `+100 Puntos` por cada objeto devuelto satisfactoriamente.
* **Rangos Cívicos Oficiales:**
  * **0 - 150 pts:** *Guardián Novato* (Badge azul sutil).
  * **151 - 350 pts:** *Custodio Solidario* (Badge naranja institucional).
  * **351+ pts:** *Héroe del Campus* (Badge púrpura / dorado de excelencia cívica).
* **Insignias Comunitarias:** *"Puntual"*, *"Custodio Confiable"*, *"Honestidad UAC"*, *"Comunidad Solidaria"*.

---

## 6. Checklist de Validación UX/UI Institucional

Antes de dar por finalizada cualquier pantalla o componente, verificar:
- [ ] ¿El fondo utiliza `ScreenBackground` (`#F5F7FC`) o blanco puro, sin tonos grises opacos?
- [ ] ¿Los botones principales usan `UniautonomaBlue` (`#0756A5`) o `UniautonomaNavy` (`#062653`) con texto blanco?
- [ ] ¿Los acentos y estados prioritarios usan `UniautonomaOrange` (`#F58220`)?
- [ ] ¿Los textos usan la fuente `MuseoSansRounded` con sus pesos apropiados?
- [ ] ¿Se eliminaron todos los `Toast.makeText` y `AlertDialog` intrusivos?
- [ ] ¿Los formularios muestran errores inline (borde naranja y texto de ayuda) en vez de alertas emergentes?
- [ ] ¿Las acciones de entrega o tickets se despliegan en `ModalBottomSheet` deslizables?
- [ ] ¿La imagen de fondo oficial conserva su proporción sin distorsión vertical u horizontal?
