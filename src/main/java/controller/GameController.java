package controller;

import model.Game;
import model.GameState;
import model.PieceColor;
import model.Player;
import repository.GameRepository;

public class GameController {

    private final GameRepository gameRepository;
    private Game currentGame;

    public GameController(GameRepository gameRepository) {
        if (gameRepository == null) {
            throw new IllegalArgumentException("gameRepository no puede ser null");
        }
        this.gameRepository = gameRepository;
    }

    public ControllerResult startNewGame(int n, Player p1, Player p2) {
        if (p1 == null || p2 == null) return ControllerResult.INVALID_INPUT;

        currentGame = new Game(n, p1, p2);
        return ControllerResult.SUCCESS;
    }

    public ControllerResult readState(GameState out) {

        if (out == null) {
            return ControllerResult.INVALID_INPUT;
        }

        if (currentGame == null) {
            out.setBoard(null);
            out.setBlack(null);
            out.setWhite(null);
            out.setTurn(PieceColor.EMPTY);
            out.setGameOver(false);
            out.setBlackCount(0);
            out.setWhiteCount(0);
            out.setResultText("");
            return ControllerResult.NO_ACTIVE_GAME;
        }

        out.setBoard(currentGame.getTablero());
        out.setBlack(currentGame.getJugadorNegro());
        out.setWhite(currentGame.getJugadorBlanco());
        out.setTurn(currentGame.getTurno());
        out.setGameOver(currentGame.juegoTerminado());
        out.setBlackCount(currentGame.contarNegras());
        out.setWhiteCount(currentGame.contarBlancas());

        if (currentGame.juegoTerminado()) {
            out.setResultText(currentGame.resultadoFinal());
        } else {
            out.setResultText("");
        }

        return ControllerResult.SUCCESS;
    }

    public ControllerResult move(int row, int col) {

        if (currentGame == null) return ControllerResult.NO_ACTIVE_GAME;
        if (currentGame.juegoTerminado()) return ControllerResult.GAME_OVER;

        // Detecta TURN_SKIPPED comparando turno antes y después
        PieceColor turnBefore = currentGame.getTurno();

        boolean ok = currentGame.jugar(row, col);
        if (!ok) return ControllerResult.INVALID_MOVE;

        if (currentGame.juegoTerminado()) return ControllerResult.GAME_OVER;

        PieceColor turnAfter = currentGame.getTurno();
        if (turnAfter == turnBefore) return ControllerResult.TURN_SKIPPED;

        return ControllerResult.SUCCESS;
    }

    public ControllerResult passTurn() {

        if (currentGame == null) return ControllerResult.NO_ACTIVE_GAME;
        if (currentGame.juegoTerminado()) return ControllerResult.GAME_OVER;

        PieceColor turnBefore = currentGame.getTurno();

        if (currentGame.getTablero().hayMovimientos(turnBefore)) {
            return ControllerResult.PASS_NOT_ALLOWED;
        }

        currentGame.pasarTurno();

        if (currentGame.juegoTerminado()) return ControllerResult.GAME_OVER;

        PieceColor turnAfter = currentGame.getTurno();
        if (turnAfter == turnBefore) return ControllerResult.TURN_SKIPPED;

        return ControllerResult.SUCCESS;
    }

    public ControllerResult saveGame(String id) {
        if (currentGame == null) return ControllerResult.NO_ACTIVE_GAME;
        if (id == null || id.isBlank()) return ControllerResult.INVALID_INPUT;

        try {
            gameRepository.guardar(id, currentGame);
            return ControllerResult.SUCCESS;
        } catch (RuntimeException ex) {
            return ControllerResult.PERSISTENCE_ERROR;
        }
    }

    public ControllerResult loadGame(String id) {
        if (id == null || id.isBlank()) return ControllerResult.INVALID_INPUT;

        try {
            Game g = gameRepository.buscarPorId(id);
            if (g == null) return ControllerResult.LOAD_NOT_FOUND;

            currentGame = g;
            return ControllerResult.SUCCESS;
        } catch (RuntimeException ex) {
            return ControllerResult.PERSISTENCE_ERROR;
        }
    }

    // ✅ acceso simple a la partida actual
    public Game getCurrentGame() {
        return currentGame;
    }
}