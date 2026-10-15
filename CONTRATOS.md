# Contratos y ruta de integración

## Frontera de cada avance

| Área | Propietario | Archivos principales |
|---|---|---|
| GUI definitiva y generación soluble | Emiliano | `emiliano/.../GuiAgent`, `GeneratorPanel`, `GeneratorAgent`, `GenerationService` |
| Jugadores H1/H2 y su comprobación | Diego | `diego/.../PlayerAgent`, `Heuristic`, `Verification` |
| Motor definitivo y jugador H3 | Abraham | `abraham/.../GreedySearch`, `LinearConflict`, `Player3Agent`, `Verification` |
| Datos, contratos y banco de prueba | Común | `common/.../Board`, `PathValidator`, `Protocol`, `SearchSession`, `support/*` |

Las copias de `common/` y de los archivos raíz son idénticas entre las entregas. Si hay que cambiar un mensaje o la representación del tablero, acordarlo y actualizar la base común antes de usar el nuevo contrato en varias ramas.

## Datos

`Board` es inmutable; usa una permutación de nueve caracteres, `0` como hueco y meta fija `123456780`. `Protocol` define mensajes serializables sobre la ontología `8puzzle-v1` y un identificador de conversación por prueba. Los jugadores filtran emisor y conversación.

`Generate` solicita mezcla legal con cantidad de pasos y semilla. `Inspect` valida un tablero. `Generated` devuelve tablero, inversiones y grado inicial. Se usan en el avance de Emiliano.

`Start` inicia la búsqueda con tablero y límite. `Expand` solicita sucesores y `Successors` los devuelve. `Candidate` propone la ruta completa; `Approved` la autoriza después de verificar movimientos y estados únicos. `Progress` comunica métricas y resultado al coordinador. Se usan en las pruebas de jugadores.

## Sustituir el soporte al integrar

1. Mantener `Board`, `Protocol`, `PathValidator` y `SearchSession` como contratos comunes.
2. En una rama de integración, ampliar el generador de Emiliano para aceptar `Start`, atender `Expand` de los jugadores y validar `Candidate`, siguiendo la interacción ya comprobada en `GraphFixtureAgent`. El soporte es una referencia del contrato; no hay que ejecutarlo junto con el generador definitivo usando el mismo nombre.
3. Hacer que su GUI arranque y escuche a los tres jugadores, despache `Start` después de generar y muestre `Progress` y las rutas aprobadas.
4. En Diego, sustituir `ReferenceGreedy` por `new GreedySearch(tablero, heuristic::estimate)` de Abraham. Agregar una dependencia del módulo `diego` al módulo `abraham`, o extraer posteriormente el motor a un módulo propio. Actualmente ambos motores satisfacen `SearchSession`.
5. Conservar las demostraciones independientes para comprobar regresiones. La GUI de jugadores solicita una iteración mediante `Step` y espera `LISTO` antes de habilitar otro clic. `Start.manual` activa este comportamiento; el modo de pruebas por consola usa ejecución automática.

## Solubilidad y no repetición

Los movimientos legales del 3x3 conservan la paridad. El generador rechaza inversiones impares respecto de la meta fija. Evitar repetir estados en una búsqueda no equivale a eliminar todos esos estados del grafo físico para siempre: la búsqueda conserva alternativas en su frontera y reconstruye una ruta simple al final.

La GUI de jugadores muestra nodos extraídos de la frontera, no jugadas físicas. Al llegar a meta, se aprueba la ruta completa sin repeticiones. Cada sufijo aprobado demuestra una continuación a meta sin retroceder. Si se exige tomar decisiones físicas irrevocables durante la búsqueda, deberá agregarse una comprobación del grafo residual antes de cada jugada; no basta la paridad.
