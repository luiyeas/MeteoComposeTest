[English](README.md) | [Español](README.es.md)

# FeverWeather

Solución Android para una prueba técnica basada en una sola pantalla que obtiene la meteorología actual de una ubicación aleatoria válida y permite refrescar la experiencia bajo demanda.

## Acceso Rápido

- APK: [artifacts/app-debug.apk](artifacts/app-debug.apk)
- Capturas: [documentation/screenshots](documentation/screenshots)
- La compilación desde código fuente requiere `OPEN_WEATHER_API_KEY`.
- `MAPS_API_KEY` no se incluye en el repositorio de forma intencionada. Si falta, la card de localización muestra un placeholder visual en lugar de un mapa real.
- El APK debug incluido permite revisar la experiencia completa sin necesidad de configurar claves localmente.

## Resumen

El reto pide una app de una sola pantalla que:

- genere una latitud y longitud aleatorias válidas
- obtenga la meteorología para esa ubicación usando OpenWeather
- muestre la información del clima junto con el contexto de la localización
- permita refrescar el contenido para empezar de nuevo con una nueva ubicación aleatoria

Esta implementación ofrece:

- carga automática al abrir la app
- refresh mediante un floating action button
- estados de loading, error bloqueante y error recuperable
- resumen meteorológico y cuatro métricas secundarias
- soporte para light mode, dark mode y landscape
- una card de mapa visual usando Google Maps Lite Mode cuando existe una key de Maps

## Capturas

| Light mode | Dark mode | Landscape |
| --- | --- | --- |
| ![Light mode](documentation/screenshots/Screenshot_20260313_110855.png) | ![Dark mode](documentation/screenshots/Screenshot_20260313_110759.png) | ![Landscape](documentation/screenshots/Screenshot_20260313_110930.png) |

## APK

El repositorio incluye una build debug lista para instalar en [artifacts/app-debug.apk](artifacts/app-debug.apk).

Instalación:

```bash
adb install -r artifacts/app-debug.apk
```

Esto facilita la revisión porque el repositorio no incluye la Google Maps API key. El APK permite validar el flujo completo, incluido el mapa real de la card, sin editar `local.properties`.

## Stack Técnico

- Kotlin
- Jetpack Compose
- Material 3
- MVVM con flujo de datos unidireccional
- `StateFlow` para el estado de pantalla
- Dagger Hilt para inyección de dependencias
- Retrofit + Gson + OkHttp
- Coroutines
- Google Maps Compose usando Google Maps Lite Mode
- Unit tests con JUnit4
- UI tests con Compose
- Tests de integración instrumentados con Hilt

## Arquitectura

La app se mantiene en un solo módulo para que la solución sea proporcional al scope del reto, pero sigue una separación por capas orientada a un entorno de producción.

```text
UI -> ViewModel -> Repository -> OpenWeather API
```

Decisiones principales:

- `WeatherViewModel` gestiona el estado de pantalla y expone un `StateFlow<WeatherUiState>` inmutable.
- La UI solo renderiza estado y emite `WeatherUiAction`.
- `WeatherRepository` encapsula los detalles de transporte y mapea la respuesta remota a modelos de la app.
- Hilt resuelve el grafo desde un módulo central, manteniendo dependencias explícitas y testeables.
- Un guard de carga en el `ViewModel` evita peticiones duplicadas ante refresh concurrentes.

## Estructura del Proyecto

```text
app/src/main/java/com/luisnavarro/fevertest/
  core/              # modelos compartidos, dispatchers y flags de test
  data/              # generador aleatorio, repositorio, Retrofit API y DTOs remotos
  di/                # módulos y bindings de Hilt
  feature/weather/   # route, screen, components, previews, state, actions y ViewModel
  ui/theme/          # theme de Compose, colores y tipografía
app/src/test/        # unit tests
app/src/androidTest/ # UI tests e integration tests
documentation/       # instrucciones del challenge y capturas
artifacts/           # APK instalable
```

## Configuración

### Requisitos

- Android Studio con Android SDK instalado
- JDK 11
- Emulador o dispositivo Android 12+ (`minSdk = 31`)

### local.properties

Crea o actualiza `local.properties` en la raíz del proyecto:

```properties
OPEN_WEATHER_API_KEY=your_openweather_key
MAPS_API_KEY=your_google_maps_key
```

Notas de configuración:

- `OPEN_WEATHER_API_KEY` es obligatoria y la build falla de forma explícita si no está configurada.
- Puedes usar la key del challenge descrita en [documentation/Instructions.md](documentation/Instructions.md) o una propia de OpenWeather.
- `MAPS_API_KEY` es opcional para compilar desde código fuente.
- Si falta `MAPS_API_KEY`, la card de localización usa el placeholder diseñado en lugar del mapa real.
- Si generas tu propia Google Maps key, conviene restringirla al package `com.luisnavarro.fevertest` y a tu SHA-1 de firma.

## Build y Ejecución

```bash
./gradlew app:assembleDebug
./gradlew app:installDebug
./gradlew app:testDebugUnitTest
./gradlew app:connectedDebugAndroidTest
./gradlew app:lintDebug
```

Notas útiles:

- `connectedDebugAndroidTest` requiere un emulador arrancado o un dispositivo conectado.
- Los logs HTTP de debug están disponibles en Logcat con el tag `OpenWeatherHttp`.

## Testing

El proyecto incluye tres capas de testing complementarias.

### Unit tests

Ubicados en `app/src/test`.

Cubren:

- límites válidos del generador aleatorio de coordenadas
- formateo del UI model y reglas de fallback del título
- `WeatherViewModel` en success, refresh, failure y guard de concurrencia
- mapeo de repositorio y manejo de payloads mal formados

### UI tests

Ubicados en `app/src/androidTest`, especialmente en `WeatherScreenTest`.

Cubren:

- renderizado del estado de loading
- renderizado del error bloqueante y dispatch de retry
- renderizado del contenido y dispatch del refresh

### Integration tests

Ubicados en `app/src/androidTest`, especialmente en `MainActivityWeatherTest`.

Cubren el flujo Android real:

```text
MainActivity -> Hilt -> WeatherViewModel -> fake repository/location generator -> UI
```

Por estabilidad, los UI tests fuerzan el uso del placeholder en la card en lugar de renderizar un Google Map real.

## Decisiones Técnicas / Trade-offs

- Se eligió Hilt frente a DI manual porque es más fácil de justificar en una base Android escalable revisada por varios ingenieros.
- La app se mantiene en un solo módulo porque el reto es de una sola pantalla y modularizar no aportaba valor inmediato.
- Se eligió Google Maps Lite Mode en lugar de un mapa interactivo porque el diseño solo requiere contexto visual.
- `Local time` sustituye a `UV index` porque el endpoint `current weather` de OpenWeather no expone ese dato.
- La OpenWeather key ya no está embebida en el repositorio. Compilar desde código fuente requiere configuración local.

## Mejoras Futuras

- mejorar la generación aleatoria para distribuir puntos uniformemente por la superficie terrestre
- añadir timeouts explícitos en OkHttp y documentar la política de reintentos
- mapear códigos de país a nombres localizados
- ampliar la cobertura instrumentada para más flujos de error
- generar un artefacto release firmado en lugar de un APK debug

## Uso de IA

Se utilizó asistencia de IA durante el desarrollo mediante OpenAI Codex / ChatGPT para:

- planificar el backlog y el orden de implementación
- contrastar trade-offs de arquitectura
- redactar y refinar la documentación del proyecto
- acelerar refactors acotados y scaffolding de tests

Todas las sugerencias se revisaron, adaptaron y validaron manualmente. El código final, las decisiones de arquitectura y la cobertura de tests se comprobaron localmente mediante comandos de Gradle.
