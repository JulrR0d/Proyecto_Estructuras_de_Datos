package grupo1.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import grupo1.Clases.Paciente;
import grupo1.Estructuras.ColaTriage;

public class SalaEsperaGUI {
    private static final Color BG = new Color(233, 236, 241);
    private static final Color CARD = new Color(233, 236, 241);
    private static final Color TEXT = new Color(40, 48, 57);
    private static final Color TEXT_ACCENT = new Color(16, 105, 120);

    private final ColaTriage colaTriage;
    private final JFrame frame;
    private final JLabel reloj;
    private final JLabel principal;
    private final JLabel[] proximos;

    public SalaEsperaGUI(ColaTriage colaTriage) {
        this.colaTriage = colaTriage;

        frame = new JFrame("Sala de Espera");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1100, 680);
        frame.setMinimumSize(new Dimension(820, 560));
        frame.setLocation(980, 90);

        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JLabel titulo = new JLabel("Panel de Sala de Espera");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        titulo.setForeground(TEXT);

        reloj = new JLabel("--:--:--", SwingConstants.RIGHT);
        reloj.setFont(new Font("Segoe UI", Font.BOLD, 25));
        reloj.setForeground(TEXT);

        header.add(titulo, BorderLayout.WEST);
        header.add(reloj, BorderLayout.EAST);

        JPanel ahoraPanel = new JPanel(new BorderLayout(8, 8));
        ahoraPanel.setBackground(CARD);
        ahoraPanel.setBorder(crearRelieveExterno());

        JLabel ahoraTitulo = new JLabel("PACIENTE EN ATENCIÓN. PASAR AHORA", SwingConstants.CENTER);
        ahoraTitulo.setFont(new Font("Segoe UI", Font.BOLD, 30));
        ahoraTitulo.setForeground(TEXT);
        ahoraTitulo.setBorder(BorderFactory.createEmptyBorder(12, 8, 0, 8));

        principal = new JLabel("Sin paciente por ahora", SwingConstants.CENTER);
        principal.setFont(new Font("Segoe UI", Font.BOLD, 44));
        principal.setForeground(TEXT_ACCENT);
        principal.setOpaque(true);
        principal.setBackground(CARD);
        principal.setBorder(BorderFactory.createCompoundBorder(
                crearRelieveInterno(),
                BorderFactory.createEmptyBorder(24, 12, 24, 12)));

        ahoraPanel.add(ahoraTitulo, BorderLayout.NORTH);
        ahoraPanel.add(principal, BorderLayout.CENTER);

        JPanel proximosPanel = new JPanel(new BorderLayout(8, 8));
        proximosPanel.setOpaque(false);

        JLabel siguientesTitulo = new JLabel("PROXIMOS 3", SwingConstants.CENTER);
        siguientesTitulo.setFont(new Font("Segoe UI", Font.BOLD, 23));
        siguientesTitulo.setForeground(TEXT);

        JPanel filas = new JPanel(new GridLayout(3, 1, 10, 10));
        filas.setOpaque(false);

        proximos = new JLabel[3];
        for (int i = 0; i < proximos.length; i++) {
            JLabel label = new JLabel("-", SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 28));
            label.setForeground(TEXT);
            label.setOpaque(true);
            label.setBackground(CARD);
            label.setBorder(BorderFactory.createCompoundBorder(
                    crearRelieveExterno(),
                    BorderFactory.createEmptyBorder(12, 8, 12, 8)));
            proximos[i] = label;
            filas.add(label);
        }

        proximosPanel.add(siguientesTitulo, BorderLayout.NORTH);
        proximosPanel.add(filas, BorderLayout.CENTER);

        root.add(header, BorderLayout.NORTH);
        root.add(ahoraPanel, BorderLayout.CENTER);
        root.add(proximosPanel, BorderLayout.SOUTH);
        frame.add(root);

        Timer timer = new Timer(1000, e -> refrescar());
        timer.start();
        refrescar();
    }

    public void mostrar() {
        frame.setVisible(true);
    }

    private void refrescar() {
        reloj.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        Paciente[] top = colaTriage.obtenerSiguientesPacientes(4);
        if (top.length == 0) {
            principal.setText("Sin paciente por ahora");
        } else {
            principal.setText(formatear(top[0]));
        }

        for (int i = 0; i < proximos.length; i++) {
            int index = i + 1;
            if (index < top.length) {
                proximos[i].setText(formatear(top[index]));
            } else {
                proximos[i].setText("-");
            }
        }
    }

    private String formatear(Paciente paciente) {
        return paciente.getNombre() + "  |  T" + paciente.getNivelTriage() + "  |  ID " + paciente.getId();
    }

    private Border crearRelieveExterno() {
        return BorderFactory.createBevelBorder(
                BevelBorder.RAISED,
                new Color(255, 255, 255),
                new Color(246, 249, 254),
                new Color(193, 199, 210),
                new Color(205, 211, 222));
    }

    private Border crearRelieveInterno() {
        return BorderFactory.createBevelBorder(
                BevelBorder.LOWERED,
                new Color(255, 255, 255),
                new Color(246, 249, 254),
                new Color(193, 199, 210),
                new Color(205, 211, 222));
    }
}