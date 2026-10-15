# Abraham: Greedy BFS y jugador3

Rama: `avance/abraham-greedy-h3`. Ejecutar `puzzle.abraham.Main` o `ejecutar.cmd` desde la raíz.

La Main arranca JADE y jugador3 con el motor Greedy BFS propio y la heurística H3 (Manhattan más conflicto lineal). El generador/ventana de soporte permite presentar el avance de forma independiente.

1. La ventana “Proyecto IA” muestra jugador3 y el tablero `123406758`.
2. Pulsar **Siguiente iteración**: se extrae un nodo por h, se incorporan sucesores sin duplicarlos y el agente espera otro clic.
3. La tercera iteración detecta la meta del ejemplo.
4. Usar **Iniciar** para repetir o probar otro tablero.

Código propio: `GreedySearch`, `LinearConflict`, `Player3Agent` y sus pruebas. El comparador del motor usa h y desempate FIFO, sin sumar profundidad. Las pruebas exhaustivas y las rutas quedan en consola y evidencias; no se agregan a la pantalla. Greedy no garantiza caminos óptimos.
