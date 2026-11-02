# Guía Maestra de Sustentación Técnica: Unifind (Jetpack Compose + Ktor)

**Materia:** Desarrollo Móvil — Semestre 6  
**Institución:** Corporación Universitaria Autónoma del Cauca (Popayán)  
**Proyecto:** Unifind (Red de Contacto y Objetos Perdidos)  
**Stack Tecnológico Moderno:**  
- **UI:** **Jetpack Compose + Material Design 3** (Estándar Oficial Declarativo de Google)
- **Lenguaje:** **Kotlin 2.0+**
- **Arquitectura UI:** **Single-Activity Architecture + Unidirectional Data Flow (UDF)**
- **Networking:** **Ktor Client 3.x (Motor CIO)** con **Corrutinas y Serialización Kotlinx JSON**
- **Gestión de Estado:** `remember`, `mutableStateOf`, `mutableStateListOf`

---

## 1. Por qué este Stack responde al 100% al feedback de la profesora

| Tecnología Antigua (Legacy) | Nueva Tecnología Moderna Implementada | Argumento de Sustentación para la Profesora |
| :--- | :--- | :--- |
| **XML Views tradicionales** | **Jetpack Compose + Material 3** | Google declaró Compose como el estándar oficial *Compose-First*. Es 100% declarativo, elimina la barrera entre XML y Kotlin, y reduce el código boilerplate en más de un 60%. |
| **`findViewById` / ViewBinding** | **State Hoisting & Composable Functions** | Ya no se manipulan referencias directas de vistas ni árboles pesados de jerarquía XML. La UI es una función matemática directa del estado: `UI = f(State)`. |
| **Múltiples Activities pesadas** | **Single-Activity Architecture (`MainActivity`)** | La aplicación corre sobre una única actividad liviana que orquesta la navegación mediante composición reactiva (`Crossfade` y backstack en memoria). |
| **Retrofit / OkHttp (basado en Java)** | **Ktor Client (Motor CIO)** | Cliente HTTP asíncrono y multiplataforma diseñado específicamente por JetBrains para Kotlin, impulsado de forma nativa por Corrutinas no bloqueantes. |

---

## 2. Preguntas Clave de la Profesora y Respuestas Exactas

### Pregunta 1: *"¿Por qué migraron de XML a Jetpack Compose y qué ventajas técnicas les da?"*
> **Respuesta:**  
> "Migramos a **Jetpack Compose** porque es el estándar moderno recomendado por Google para todo proyecto Android nuevo.  
> Con el modelo tradicional en XML, el paradigma era **imperativo**: teníamos que inflar el layout XML y luego modificar manualmente cada propiedad (`binding.textView.text = ...`).  
> Con Compose, pasamos al paradigma **declarativo**: simplemente describimos cómo debe verse la interfaz para un estado dado. Cuando el estado cambia, Compose ejecuta una **recomposición inteligente**, actualizando únicamente los nodos que sufrieron cambios en el árbol de renderizado, lo que optimiza enormemente el consumo de memoria y la tasa de refresco (60-120 FPS)."

### Pregunta 2: *"¿Qué es Ktor y por qué lo eligieron sobre Retrofit para consumir APIs?"*
> **Respuesta:**  
> "Elegimos **Ktor Client** porque es una solución *Kotlin-First* desarrollada por JetBrains.  
> A diferencia de Retrofit (que es una librería Java que usa reflexión y adaptadores de llamadas), Ktor está construido desde sus cimientos sobre **Corrutinas de Kotlin** y motores asíncronos ligeros (como **CIO - Coroutine I/O**).  
> Además, Ktor utiliza el plugin oficial de `kotlinx.serialization` para convertir JSON a objetos de datos (`ItemReport`, `ChatMessage`) de forma instantánea sin el overhead de bibliotecas basadas en reflexión como Gson."

### Pregunta 3: *"¿Cómo manejan la navegación entre pantallas en Compose?"*
> **Respuesta:**  
> "Adoptamos la arquitectura **Single-Activity**:  
> `MainActivity` es el contenedor host que ejecuta `UnifindApp()`.  
> La navegación se gestiona con una pila reactiva (`backStack`) basada en la clase sellada `Screen` (`Screen.Feed`, `Screen.Detail`, `Screen.Report`, `Screen.Messages`, `Screen.Chat`).  
> Además, integramos `BackHandler` de AndroidX Compose, lo que garantiza que al presionar el botón físico o gesto de 'Atrás' del sistema operativo, el usuario regrese ordenadamente a la pantalla previa sin cerrar la app."

### Pregunta 4: *"¿Dónde está implementado el modal de valoración de entrega?"*
> **Respuesta:**  
> "La Pantalla 7 se implementó como un **`ModalBottomSheet` nativo de Material 3** en `RatingModal.kt`.  
> Se despliega sobre la vista actual cuando el usuario presiona 'Confirmar Entrega de Objeto' en el chat o desde la pestaña 'Honor', permitiendo seleccionar estrellas de calificación, asignar reconocimientos y acreditar los +100 Puntos de Honor con animaciones fluidas."

---

## 3. Guion de Demostración en Vivo para la Profesora (3 Minutos)

1. **Minuto 1: Inicio en Feed (`FeedScreen.kt`)**
   - Muestra la app abriendo instantáneamente en el Feed de Unifind.
   - Explica el encabezado institucional Uniautónoma en Navy (`#1B223C`), la barra de búsqueda y el toggle reactivo **"Perdidos (14)"** vs **"Encontrados (28)"**.
   - Muestra los chips de categorías (`ItemCategory.values()`).

2. **Minuto 1.5: Detalle y Anti-Fraude (`DetailScreen.kt`)**
   - Toca **"Verificar Propiedad →"** en la tarjeta de la calculadora Casio.
   - Explica la ficha técnica de 6 atributos y destaca el **Filtro Anti-Estafas**: muestra que la respuesta correcta es confidencial y solo se desbloquea el chat cuando el usuario ingresa la respuesta de validación.

3. **Minuto 2: Chat Seguro & Punto Seguro Kime (`ChatScreen.kt`)**
   - Ingresa al chat y muestra la tarjeta superior con la cita fijada: *Punto Seguro Kime (Cafetería Bloque A / Bienestar Institucional)*.
   - Envía un mensaje en la barra inferior para evidenciar la reactividad de Compose (`messages.add(...)`).
   - Toca **"Confirmar Entrega de Objeto"**.

4. **Minuto 2.5: Modal de Valoración (`RatingModal.kt`)**
   - Se despliega el `ModalBottomSheet` con el medallón de celebración y las 5 estrellas interactivas.
   - Muestra la adjudicación de **+100 Puntos de Honor** y el nivel alcanzado en campus.

5. **Minuto 3: Publicar Reporte (`ReportScreen.kt`)**
   - Vuelve al Feed y pulsa el FAB flotante **"+ Reportar Objeto"**.
   - Muestra el asistente modular con barra de progreso, atributos del objeto y la configuración de la pregunta anti-fraude.

---

## 4. Estructura de Paquetes en el Proyecto

```text
com.salomontilla.frontunifind/
│
├── MainActivity.kt               <-- Single-Activity (Compose Entrypoint)
│
├── data/
│   ├── model/
│   │   └── Models.kt             <-- Modelos de datos serializables con Kotlinx
│   └── network/
│       └── KtorClient.kt         <-- Cliente HTTP Ktor (CIO Engine + ContentNegotiation)
│
└── ui/
    ├── components/
    │   └── UnifindBottomBar.kt   <-- Barra de navegación inferior con badges reactivos
    ├── navigation/
    │   ├── Screen.kt             <-- Sealed class con las rutas de navegación
    │   └── UnifindApp.kt         <-- Orquestador de Backstack y transiciones
    ├── screens/
    │   ├── FeedScreen.kt         <-- Pantalla 2 (Feed & Búsqueda)
    │   ├── DetailScreen.kt       <-- Pantalla 3 (Ficha Técnica & Anti-Fraude)
    │   ├── ReportScreen.kt       <-- Pantalla 4 (Asistente de Publicación Wizard)
    │   ├── MessagesScreen.kt     <-- Pantalla 5 (Bandeja de Entrada & Filtros)
    │   ├── ChatScreen.kt         <-- Pantalla 6 (Chat Privado & Coordinación Kime)
    │   ├── RatingModal.kt        <-- Pantalla 7 (ModalBottomSheet de Honor)
    │   └── LoginScreen.kt        <-- Pantalla 1 (Onboarding Institucional)
    └── theme/
        ├── Color.kt              <-- Paleta Uniautónoma (Navy, Blue, Gold, Green)
        ├── Theme.kt              <-- UnifindTheme (Material 3 LightColorScheme)
        └── Type.kt               <-- Tipografía jerárquica
```
