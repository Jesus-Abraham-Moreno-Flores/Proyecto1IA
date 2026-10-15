# Proyecto IA: avance individual con JADE

## Ejecutar

Se requiere JDK 17 o superior. Abrir `ejecutar.cmd`, o importar el `pom.xml` raíz en IntelliJ, activar el perfil del integrante y ejecutar su clase Main:

| Integrante | Perfil Maven | Main |
|---|---|---|
| Emiliano | `emiliano` | `puzzle.emiliano.Main` |
| Diego | `diego` | `puzzle.diego.Main` |
| Abraham | `abraham` | `puzzle.abraham.Main` |

Cada Main arranca una plataforma JADE real y los agentes necesarios. La consola muestra la versión JADE y el mensaje “JADE iniciado”. JADE viene incluido en `lib/`. La ejecución con los `.cmd` no necesita Maven ni internet. No abrir dos demostraciones a la vez: usan el puerto 1099.

## Interfaz mínima

- Título: **Proyecto IA**.
- Un tablero y un botón por jugador.
- Emiliano: **Nuevo puzzle** genera un tablero soluble. **Siguiente paso** recorre una muestra legal guardada en memoria; no ejecuta agentes jugadores. La pantalla indica “Ejemplo en memoria”.
- Diego y Abraham: cada **Siguiente iteración** realiza una extracción real de la frontera Greedy y procesa sus sucesores. La búsqueda espera el próximo clic. **Iniciar** permite reiniciar con el tablero escrito.

El tablero inicial de jugadores es `123406758`, de dos movimientos, para una demostración breve. Una iteración de Greedy puede seleccionar otro nodo de la frontera que no sea vecino del nodo anterior: la interfaz muestra exploración del algoritmo, no una animación de la ruta de solución. La ruta completa se valida al encontrar la meta.

## Probar

`probar.cmd` ejecuta las pruebas de lógica y mensajería. En PowerShell también se puede usar:

```powershell
.\build.ps1 -Mode test
.\build.ps1 -Mode smoke
```

El modo `smoke` completa la búsqueda automáticamente por consola; la GUI normal siempre espera clics. `-Mode capture` prueba los botones visibles y guarda una captura. No hay animación automática, estadísticas ni registro técnico dentro de la ventana.

Cada entrega incluye `common/` y solo el módulo propio. El soporte de Diego y Abraham permite probar sus jugadores independientemente; su motor de referencia y sus herramientas compartidas están identificados en el código. No se presentan como trabajo del otro integrante. Ver `<integrante>/README.md` para el alcance individual y `GIT.md` para las ramas.

Sin Maven, también se puede ejecutar el `.cmd` desde la terminal de IntelliJ. Para una configuración Application manual, marcar `common/src/main/java` y `<integrante>/src/main/java` como fuentes, agregar `lib/jade-4.3.jar` y elegir la Main indicada. Las evidencias de ejecución están en `<integrante>/evidencias/`.
