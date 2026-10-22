# Proyecto IA: Abraham por consola

Un solo proyecto Maven, sin interfaz gráfica ni dependencia JavaFX. Se requiere JDK 17 o superior. JADE se incluye como biblioteca y dependencia Maven.

## Ejecutar desde IntelliJ

Abrir el `pom.xml` raíz, pulsar **Reload All Maven Projects** y ejecutar la Main de la demostración deseada. No hay perfiles ni módulos que activar.

| Clase | Qué demuestra |
|---|---|
| `puzzle.abraham.DemoHeuristica` | Tablero, Manhattan, conflictos independientes y resultado H3 |
| `puzzle.abraham.DemoGreedy` | Cada estado extraído, su H3, sucesores y ruta final, sin JADE |
| `puzzle.abraham.Main` | Jugador3 real en JADE: REQUEST, pensar, razonar, actuar e INFORM |

Los resultados aparecen en la consola Run de IntelliJ. Para usar la ventana Terminal integrada también se pueden ejecutar estos comandos:

```powershell
.\build.ps1 -Mode heuristica
.\build.ps1 -Mode greedy
.\build.ps1 -Mode agente
```

El ejemplo heurístico es `213456870`: Manhattan 4, dos conflictos independientes y H3 = 8. Greedy y el agente usan `123406758`, que encuentra una ruta de dos movimientos mediante tres extracciones.

## Estructura simple

Todas las clases están en `src/main/java`:

- `DemoHeuristica`: muestra el cálculo e imprime tableros.
- `DemoGreedy`: muestra una búsqueda por iteraciones y se reutiliza desde jugador3.
- `GreedySearch`: algoritmo Java estándar, sin JADE ni componentes gráficos.
- `LinearConflict`: cálculo H3, separado en métodos legibles.
- `Player3Agent`: interpreta REQUEST en `pensar`, ejecuta Greedy en `razonar` y responde en `actuar`.
- `AgenteConsola`: solicita la resolución, verifica el resultado y termina la demostración. También tiene pensar, razonar y actuar.
- `Main`: inicia JADE y los agentes consola/jugador3.
- `ResultadoBusqueda`: datos serializables de la respuesta.
- `Board`: tablero inmutable, vecinos legales y solubilidad.
- `PathValidator`: verifica movimientos, meta y ausencia de estados repetidos.

## Funcionamiento

`consola --REQUEST--> jugador3 --INFORM/FAILURE--> consola`

La petición lleva el tablero y el límite. Jugador3 utiliza el mismo Greedy Java que la demostración independiente. En esta versión individual obtiene los vecinos de `Board` directamente; no arranca un agente grafo adicional. La conexión con el generador del equipo queda para la integración posterior.

Greedy selecciona el menor h de toda la frontera y desempata por orden de llegada. Los vecinos repetidos no vuelven a insertarse. Los nodos mostrados son exploración, no necesariamente jugadas consecutivas. La ruta final sí se valida como una secuencia legal sin repeticiones. Greedy no garantiza el camino más corto.

## Probar

```powershell
.\build.ps1 -Mode motor
.\build.ps1 -Mode test
.\verificar-integracion.ps1
```

`motor` compila y ejecuta sin agregar JADE al classpath. `test` revisa H3 en los 181440 estados alcanzables. La integración comprueba caso normal, meta inicial, insoluble y límite. Los dos últimos deben devolver código 2; un límite no prueba insolubilidad.

Para cambiar el ejemplo del agente, usar Program arguments en IntelliJ: `--board=123406758 --limit=181440`. En las dos demos independientes se admite un tablero como primer argumento, por ejemplo `213456870`.

`ejecutar.cmd` abre la demostración del agente y conserva la terminal visible. `probar.cmd` ejecuta las comprobaciones. La consola no abre RMA ni otras ventanas. No iniciar dos plataformas JADE simultáneamente en el puerto 1099.
