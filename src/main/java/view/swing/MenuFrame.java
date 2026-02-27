package view.swing;

import controller.ControllerResult;
import controller.GameController;
import controller.PlayerController;
import model.Player;

import javax.swing.*;
import java.awt.*;

public class MenuFrame extends JFrame {

    private final GameController gameController;
    private final PlayerController playerController;

    public static void show(GameController gameController, PlayerController playerController) {
        SwingUtilities.invokeLater(() -> new MenuFrame(gameController, playerController).setVisible(true));
    }

    public MenuFrame(GameController gameController, PlayerController playerController) {
        this.gameController = gameController;
        this.playerController = playerController;

        setTitle("Reverse Dots - Menú");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 240);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JButton btnNew = new JButton("Nueva partida");
        JButton btnLoad = new JButton("Cargar partida");
        JButton btnPlayers = new JButton("Ver jugadores");
        JButton btnExit = new JButton("Salir");

        btnNew.addActionListener(e -> onNewGame());
        btnLoad.addActionListener(e -> onLoadGame());
        btnPlayers.addActionListener(e -> onListPlayers());
        btnExit.addActionListener(e -> onExit());

        panel.add(btnNew);
        panel.add(btnLoad);
        panel.add(btnPlayers);
        panel.add(btnExit);

        setContentPane(panel);
    }

    private void onNewGame() {
        Player p1 = askOrRegisterPlayer("Jugador 1 (nombre)");
        if (p1 == null) return;

        Player p2 = askOrRegisterPlayer("Jugador 2 (nombre distinto)");
        if (p2 == null) return;

        if (p1.getNombre().equalsIgnoreCase(p2.getNombre())) {
            JOptionPane.showMessageDialog(this, "Los jugadores deben ser distintos.");
            return;
        }

        Integer size = askBoardSize();
        if (size == null) return;

        ControllerResult res = gameController.startNewGame(size, p1, p2);
        if (res != ControllerResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, "No se pudo iniciar la partida (tamaño inválido o datos inválidos).");
            return;
        }

        setVisible(false);
        GameFrame.show(gameController, playerController, this);
    }

    private void onLoadGame() {
        String id = JOptionPane.showInputDialog(this, "ID/Nombre de partida a cargar:", "Cargar", JOptionPane.QUESTION_MESSAGE);
        if (id == null) return;

        ControllerResult res = gameController.loadGame(id);

        if (res == ControllerResult.LOAD_NOT_FOUND) {
            JOptionPane.showMessageDialog(this, "No existe una partida con ese ID.");
            return;
        }
        if (res == ControllerResult.PERSISTENCE_ERROR) {
            JOptionPane.showMessageDialog(this, "Error al cargar (archivo dañado, ruta inválida o permisos).");
            return;
        }
        if (res != ControllerResult.SUCCESS) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar la partida.");
            return;
        }

        setVisible(false);
        GameFrame.show(gameController, playerController, this);
    }

    private void onListPlayers() {
        Player[] players = playerController.listPlayers();

        if (players.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay jugadores registrados.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Player p : players) {
            sb.append(p.getNombre())
                    .append(" | Ganadas: ").append(p.getGanadas())
                    .append(" | Perdidas: ").append(p.getPerdidas())
                    .append("\n");
        }

        JTextArea area = new JTextArea(sb.toString(), 12, 40);
        area.setEditable(false);
        JOptionPane.showMessageDialog(this, new JScrollPane(area), "Jugadores", JOptionPane.INFORMATION_MESSAGE);
    }

    private void onExit() {
        int r = JOptionPane.showConfirmDialog(this, "¿Seguro que deseas salir?", "Confirmar salida", JOptionPane.YES_NO_OPTION);
        if (r == JOptionPane.YES_OPTION) {
            dispose();
        }
    }

    private Player askOrRegisterPlayer(String prompt) {
        while (true) {
            String name = JOptionPane.showInputDialog(this, prompt, "Jugador", JOptionPane.QUESTION_MESSAGE);
            if (name == null) return null;

            name = name.trim();
            if (name.isBlank()) {
                JOptionPane.showMessageDialog(this, "Nombre vacío. Intente de nuevo.");
                continue;
            }

            // buscar directo
            Player found = playerController.getPlayer(name);
            if (found != null) return found;

            // si no existe, ofrecer registrar
            int r = JOptionPane.showConfirmDialog(this,
                    "No existe el jugador \"" + name + "\". ¿Deseas registrarlo?",
                    "Registrar", JOptionPane.YES_NO_OPTION);

            if (r != JOptionPane.YES_OPTION) {
                continue;
            }

            Player created = playerController.registerPlayer(name);
            if (created != null) return created;

            JOptionPane.showMessageDialog(this, "No se pudo registrar (posiblemente ya existe o nombre inválido).");
        }
    }

    private Integer askBoardSize() {
        while (true) {
            String s = JOptionPane.showInputDialog(this, "Tamaño del tablero N (par y >= 4):", "8");
            if (s == null) return null;
            s = s.trim();
            try {
                int n = Integer.parseInt(s);
                if (n >= 4 && n % 2 == 0) return n;
                JOptionPane.showMessageDialog(this, "N debe ser par y >= 4.");
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingresa un número válido.");
            }
        }
    }
}