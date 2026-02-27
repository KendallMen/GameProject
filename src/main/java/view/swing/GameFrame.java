package view.swing;

import controller.ControllerResult;
import controller.GameController;
import controller.PlayerController;
import model.GameState;
import model.PieceColor;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class GameFrame extends JFrame {

    private final GameController gameController;
    private final PlayerController playerController;
    private final JFrame menuToReturn;

    private JPanel boardPanel;
    private JButton[][] cells;

    private final JLabel lblTurn = new JLabel("Turno: ");
    private final JLabel lblScore = new JLabel("Marcador: ");

    public static void show(GameController gameController, PlayerController playerController, JFrame menuToReturn) {
        SwingUtilities.invokeLater(() -> new GameFrame(gameController, playerController, menuToReturn).setVisible(true));
    }

    public GameFrame(GameController gameController, PlayerController playerController, JFrame menuToReturn) {
        this.gameController = gameController;
        this.playerController = playerController;
        this.menuToReturn = menuToReturn;

        setTitle("Reverse Dots - Partida");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setSize(720, 640);
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override public void windowClosing(java.awt.event.WindowEvent e) { onExit(); }
        });

        JPanel top = new JPanel(new GridLayout(2, 1));
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        top.add(lblTurn);
        top.add(lblScore);

        boardPanel = new JPanel();
        boardPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton btnPass = new JButton("Pasar");
        JButton btnStats = new JButton("Estadísticas");
        JButton btnSave = new JButton("Guardar");
        JButton btnExit = new JButton("Salir");

        btnPass.addActionListener(e -> onPass());
        btnStats.addActionListener(e -> onStats());
        btnSave.addActionListener(e -> onSave());
        btnExit.addActionListener(e -> onExit());

        bottom.add(btnPass);
        bottom.add(btnStats);
        bottom.add(btnSave);
        bottom.add(btnExit);

        setLayout(new BorderLayout());
        add(top, BorderLayout.NORTH);
        add(new JScrollPane(boardPanel), BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);

        initBoardOnce();
        refreshUI();
    }

    private void initBoardOnce() {
        GameState state = new GameState();
        ControllerResult r = gameController.readState(state);
        if (r != ControllerResult.SUCCESS || state.getBoard() == null) {
            JOptionPane.showMessageDialog(this, "No hay partida activa.");
            returnToMenu();
            return;
        }

        int n = state.getBoard().getTamano();
        cells = new JButton[n][n];
        boardPanel.setLayout(new GridLayout(n, n, 2, 2));

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                final int row = i;
                final int col = j;

                JButton b = new JButton();
                b.setMargin(new Insets(0, 0, 0, 0));
                b.setFocusable(false);
                b.setOpaque(true);
                b.setBorder(new LineBorder(Color.GRAY, 1));
                b.addActionListener(e -> onMove(row, col));

                cells[i][j] = b;
                boardPanel.add(b);
            }
        }
    }

    private void refreshUI() {
        GameState state = new GameState();
        ControllerResult r = gameController.readState(state);
        if (r != ControllerResult.SUCCESS || state.getBoard() == null) {
            JOptionPane.showMessageDialog(this, "No hay partida activa.");
            returnToMenu();
            return;
        }

        int n = state.getBoard().getTamano();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                PieceColor color = state.getBoard().ficha(i, j);

                cells[i][j].setText("");

                if (color == PieceColor.BLACK) {
                    cells[i][j].setBackground(Color.BLACK);
                } else if (color == PieceColor.WHITE) {
                    cells[i][j].setBackground(Color.WHITE);
                } else {
                    cells[i][j].setBackground(Color.LIGHT_GRAY);
                }
            }
        }

        PieceColor turn = state.getTurn();
        String turnoTxt = (turn == PieceColor.BLACK) ? "Negro" : "Blanco";
        lblTurn.setText("Turno: " + turnoTxt);

        String blackName = (state.getBlack() != null) ? state.getBlack().getNombre() : "Negro";
        String whiteName = (state.getWhite() != null) ? state.getWhite().getNombre() : "Blanco";
        lblScore.setText("Marcador: " + blackName + " (Negro) = " + state.getBlackCount() +
                " | " + whiteName + " (Blanco) = " + state.getWhiteCount());

        if (state.isGameOver()) {
            onGameOver(state.getResultText());
        }
    }

    private void onMove(int row, int col) {
        ControllerResult r = gameController.move(row, col);

        if (r == ControllerResult.INVALID_MOVE) {
            JOptionPane.showMessageDialog(this, "Movimiento inválido.");
        } else if (r == ControllerResult.TURN_SKIPPED) {
            JOptionPane.showMessageDialog(this, "El turno del oponente fue saltado (no tenía movimientos).");
        } else if (r == ControllerResult.GAME_OVER) {
            refreshUI();
            return;
        } else if (r != ControllerResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, "No se pudo realizar el movimiento.");
        }

        refreshUI();
    }

    private void onPass() {
        ControllerResult r = gameController.passTurn();

        if (r == ControllerResult.PASS_NOT_ALLOWED) {
            JOptionPane.showMessageDialog(this, "No puedes pasar si tienes movimientos disponibles.");
            return;
        }

        if (r == ControllerResult.TURN_SKIPPED) {
            JOptionPane.showMessageDialog(this, "Turno pasado / saltado.");
        } else if (r == ControllerResult.GAME_OVER) {
            refreshUI();
            return;
        } else if (r != ControllerResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, "No se pudo pasar el turno.");
        }

        refreshUI();
    }

    private void onStats() {
        GameState state = new GameState();
        ControllerResult r = gameController.readState(state);
        if (r != ControllerResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, "No se pudo leer el estado.");
            return;
        }

        String blackName = (state.getBlack() != null) ? state.getBlack().getNombre() : "Negro";
        String whiteName = (state.getWhite() != null) ? state.getWhite().getNombre() : "Blanco";
        String turnoTxt = (state.getTurn() == PieceColor.BLACK) ? "Negro" : "Blanco";

        String msg = "Turno: " + turnoTxt + "\n" +
                blackName + " (Negro): " + state.getBlackCount() + "\n" +
                whiteName + " (Blanco): " + state.getWhiteCount() + "\n";
        JOptionPane.showMessageDialog(this, msg, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onSave() {
        String id = JOptionPane.showInputDialog(this, "Nombre/ID para guardar:", "Guardar", JOptionPane.QUESTION_MESSAGE);
        if (id == null) return;
        id = id.trim();
        if (id.isBlank()) {
            JOptionPane.showMessageDialog(this, "ID vacío.");
            return;
        }

        Path defaultDir = Path.of("data").resolve("games");
        Path fileGuess = (id.contains("/") || id.contains("\\") || Path.of(id).isAbsolute())
                ? ensureJson(Path.of(id))
                : defaultDir.resolve(id + ".json");

        if (Files.exists(fileGuess)) {
            int r = JOptionPane.showConfirmDialog(
                    this,
                    "El archivo ya existe:\n" + fileGuess + "\n¿Deseas sobrescribir?",
                    "Sobrescribir",
                    JOptionPane.YES_NO_OPTION
            );
            if (r != JOptionPane.YES_OPTION) return;
        }

        ControllerResult r = gameController.saveGame(id);
        if (r == ControllerResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, "Partida guardada.");
        } else if (r == ControllerResult.PERSISTENCE_ERROR) {
            JOptionPane.showMessageDialog(this, "Error al guardar (ruta inválida, permisos o problema de archivos).");
        } else {
            JOptionPane.showMessageDialog(this, "No se pudo guardar.");
        }
    }

    private static Path ensureJson(Path p) {
        String name = p.getFileName().toString();
        if (name.endsWith(".json")) return p;
        Path parent = p.getParent();
        return (parent == null) ? Path.of(name + ".json") : parent.resolve(name + ".json");
    }

    private void onExit() {
        int r = JOptionPane.showConfirmDialog(this, "¿Salir de la partida y volver al menú?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
            returnToMenu();
        }
    }

    private void onGameOver(String resultText) {
        try {
            if (gameController.getCurrentGame() != null) {
                playerController.updateStats(gameController.getCurrentGame());
            }
        } catch (RuntimeException ignored) {}

        JOptionPane.showMessageDialog(
                this,
                "Juego terminado.\nResultado: " + (resultText == null ? "" : resultText),
                "GAME OVER",
                JOptionPane.INFORMATION_MESSAGE
        );

        dispose();
        returnToMenu();
    }

    private void returnToMenu() {
        if (menuToReturn != null) {
            menuToReturn.setVisible(true);
        }
    }
}
