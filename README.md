# Reverse Dots (Java)

## Descripcion
Juego de tablero para 2 jugadores tipo Reversi. Cada turno se coloca una ficha y se voltean fichas del oponente en las 8 direcciones cuando quedan encerradas. Gana quien tenga mas fichas al final.

## Reglas basicas
- Tablero N x N (N par, >= 4).
- Negro inicia.
- Jugada valida: encierra fichas del oponente en linea recta (horizontal, vertical, diagonal).
- Si un jugador no tiene movimientos, pierde el turno.
- Finaliza cuando el tablero esta lleno o ninguno puede jugar.

## Arquitectura (MVC por capas)
- **Model**: `Board`, `Game`, `Player`, `Disc`, `PieceColor`, `GameState`.
- **Repository**: interfaces `GameRepository`, `PlayerRepository` y persistencia JSON (`JsonGameRepository`, `JsonPlayerRepository`).
- **Controller**: `GameController`, `PlayerController` con resultados en `ControllerResult`.
- **View**: Swing (`MenuFrame`, `GameFrame`).

## Funcionalidades principales
- Menu con: nueva partida, cargar partida, ver jugadores, salir.
- Partida: jugar, pasar turno, ver estadisticas, guardar, salir.
- Guardado/carga en JSON.
- Registro de jugadores con ganadas/perdidas.

## Estructura de carpetas
- `src/main/java/model`: dominio del juego.
- `src/main/java/repository`: persistencia y acceso a datos.
- `src/main/java/controller`: flujo del juego y reglas.
- `src/main/java/view/swing`: interfaz grafica.
- `src/test/java`: pruebas unitarias.
- `data/`: archivos de jugadores y partidas (ejemplo).

## Requisitos
- Java (segun `pom.xml`, Java 25).
- Maven.

## Ejecutar
Desde la raiz del proyecto:

```powershell
mvn -q -DskipTests package
```

Luego ejecuta la clase `app.Main` desde tu IDE (JetBrains).

## Tests
Para correr pruebas unitarias:

```powershell
mvn test
```

## Guardado y carga
- Guardado: se almacena en JSON con estado de tablero, turno y jugadores.
- Carga: restaura el estado exacto de la partida guardada.

## Notas
- Los controladores no dependen de la UI.
- La vista maneja mensajes y errores para el usuario.

