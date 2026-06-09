package grupo1.GUI;

import java.awt.*;
import javax.swing.*;

import grupo1.Estructuras.TablaHash;
import grupo1.Estructuras.TablaHash.BucketVista;;

/**
 * Panel de visualización de la Tabla Hash.
 * Muestra los buckets como columnas verticales de celdas.
 * Cada celda representa una Entrada en la cadena del bucket.
 * Las animaciones destacan:
 *   - Inserción  → verde  (C_INSERT)
 *   - Eliminación → rojo   (C_DELETE)
 *   - Búsqueda   → azul   (C_FOUND)
 *   - Bucket visitado durante hash → amarillo (C_CAMINO)
 */

public class HashPanel extends JPanel{
    // Paleta
    static final Color BG       = new Color(233, 236, 241);
    static final Color TEXT     = new Color( 40,  48,  57);
    static final Color C_NORMAL = new Color(193, 199, 210);
    static final Color C_CAMINO = new Color(255, 210,  80); // amarillo
    static final Color C_INSERT = new Color( 80, 200, 120); // verde
    static final Color C_DELETE = new Color(220,  80,  80); // rojo
    static final Color C_FOUND  = new Color( 80, 160, 220); // azul

    // Métricas de layout
    private static final int CELL_W   = 72;  // ancho de cada nodo de la cadena
    private static final int CELL_H   = 36;  // alto de cada nodo
    private static final int ROW_H    = 52;  // alto total de cada fila de bucket
    private static final int IDX_W    = 46;  // ancho de la columna de índice [n]
    private static final int ARROW_W  = 18;  // ancho de la flecha → entre nodos
    private static final int PAD_X    = 10;
    private static final int PAD_Y    = 8;

    // Estado de animación
    private int     bucketAnimado = -1;
    private long    idAnimado     = -1;
    private Color   colorFinal    = C_NORMAL;
    private boolean faseBucket    = false;
    private Timer   animTimer;

    // Componentes
    private final TablaHash   tabla;
    private final DibujoPanel dibujo;   // panel interno que se pinta y hace scroll
    private final JScrollPane scroll;

    public HashPanel(TablaHash tabla) {
        super(new BorderLayout());
        this.tabla = tabla;
        // Panel interno que sobreescribe paintComponent y reporta altura dinámica
        dibujo = new DibujoPanel();
        dibujo.setBackground(BG);
        scroll = new JScrollPane(dibujo,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getViewport().setBackground(BG);
        scroll.setBorder(BorderFactory.createTitledBorder("Tabla Hash"));
        scroll.getVerticalScrollBar().setUnitIncrement(ROW_H);
        setBackground(BG);
        add(scroll, BorderLayout.CENTER);
        setPreferredSize(new Dimension(420, 400));
    }


    // Visualización pública
    public void animar(int bucketIdx, long id, Color color, Runnable alTerminar) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();
        bucketAnimado = bucketIdx;
        idAnimado     = -1;
        colorFinal    = color;
        faseBucket    = true;
        // Fase 1 – bucket amarillo (500 ms)
        animTimer = new Timer(500, e -> {
            ((Timer) e.getSource()).stop();
            faseBucket = false;
            idAnimado  = id;
            dibujo.repaint();
            // Fase 2 – entrada con color final (1200 ms) → limpiar
            Timer limpia = new Timer(1200, ev -> {
                if (alTerminar != null) alTerminar.run();
                bucketAnimado = -1;
                idAnimado     = -1;
                dibujo.repaint();
            });
            limpia.setRepeats(false);
            limpia.start();
        });
        animTimer.start();
        // Hacer scroll automático hacia el bucket animado
        SwingUtilities.invokeLater(() -> scrollToBucket(bucketIdx));
        dibujo.repaint();
    }


    /** Inserción: bucket amarillo → entrada verde. */
    public void animarInsercion(int bucketIdx, long id) {
        animar(bucketIdx, id, C_INSERT, null);
    }

    /** Eliminación: bucket amarillo → entrada roja → callback. */
    public void animarEliminacion(int bucketIdx, long id, Runnable alTerminar) {
        animar(bucketIdx, id, C_DELETE, alTerminar);
    }

    /** Búsqueda: bucket amarillo → entrada azul. */
    public void animarBusqueda(int bucketIdx, long id) {
        animar(bucketIdx, id, C_FOUND, null);
    }

    /** Repinta sin animación (p.ej. después de "Actualizar"). */
    public void refrescar() {
        dibujo.revalidate();
        dibujo.repaint();
    }

    /** Color público para que GUI.java lo use en búsqueda (paridad con AVLpanel). */
    public static Color colorBusqueda() { return C_FOUND; }

    // Scroll automático al bucket afectado
    private void scrollToBucket(int idx) {
        if (idx < 0) return;
        int y = PAD_Y + idx * ROW_H;
        Rectangle r = new Rectangle(0, y, 1, ROW_H);
        dibujo.scrollRectToVisible(r);
    }

    // Dibujo
    private class DibujoPanel extends JPanel {
        DibujoPanel() {
            setOpaque(true);
        }
        /** Altura preferida: una fila por bucket + padding. */
        @Override
        public Dimension getPreferredSize() {
            int cap = tabla.capacidad();
            int h   = PAD_Y * 2 + cap * ROW_H + 20; // +20 para la leyenda
            int w   = scroll.getViewport().getWidth();
            return new Dimension(Math.max(w, 300), h);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            BucketVista[] vistas = tabla.obtenerVistas();
            int cap = vistas.length;
            for (int i = 0; i < cap; i++) {
                int y = PAD_Y + i * ROW_H;
                dibujarFila(g2, vistas[i], i, y);
            }
            // Leyenda inferior
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(TEXT);
            String info = "n=" + tabla.tam()
                    + "   m=" + cap
                    + "   alfa=" + String.format("%.2f", tabla.factorCarga());
            g2.drawString(info, PAD_X, PAD_Y + cap * ROW_H + 14);
        }

        // Dibuja una fila: [idx] → celda → celda → null
        private void dibujarFila(Graphics2D g2, BucketVista vista, int idx, int y) {
            boolean esAnimado = (idx == bucketAnimado);
            int cy = y + (ROW_H - CELL_H) / 2; // centrar verticalmente en la fila
            
            // Separador horizontal ligero entre filas
            g2.setColor(new Color(210, 213, 220));
            g2.setStroke(new BasicStroke(0.5f));
            g2.drawLine(0, y, getWidth(), y);

            // Columna de índice
            Color idxColor = (esAnimado && faseBucket) ? C_CAMINO : new Color(100, 110, 125);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            g2.setColor(idxColor);
            String label = "[" + idx + "]";
            FontMetrics fm = g2.getFontMetrics();
            int lx = PAD_X + IDX_W - fm.stringWidth(label) - 4;
            g2.drawString(label, lx, cy + CELL_H / 2 + 4);

            // Flecha inicial del índice hacia la cadena
            int startX = PAD_X + IDX_W;
            dibujarFlecha(g2, startX, cy + CELL_H / 2, startX + ARROW_W, cy + CELL_H / 2, idxColor);

            // Cadena horizontal
            int x = PAD_X + IDX_W + ARROW_W;

            if (vista.ids.length == 0) {
                // Bucket vacío → solo "null"
                dibujarNull(g2, x, cy, esAnimado && faseBucket);
                return;
            }

            for (int k = 0; k < vista.ids.length; k++) {
                long eid   = vista.ids[k];
                Color fondo = resolverColor(idx, eid);

                dibujarCelda(g2, x, cy, eid, vista.nombres[k], fondo);
                x += CELL_W;

                if (k < vista.ids.length - 1) {
                    // Flecha → al siguiente nodo
                    dibujarFlecha(g2, x, cy + CELL_H / 2,
                                    x + ARROW_W, cy + CELL_H / 2,
                                    new Color(130, 135, 145));
                    x += ARROW_W;
                }
            }

            // Flecha final → null
            dibujarFlecha(g2, x, cy + CELL_H / 2,
                            x + ARROW_W, cy + CELL_H / 2,
                            new Color(130, 135, 145));
            x += ARROW_W;
            dibujarNull(g2, x, cy, false);
        }

        private void dibujarCelda(Graphics2D g2, int x, int y,
                                    long id, String nombre, Color fondo) {
            // Sombra sutil
            g2.setColor(new Color(0, 0, 0, 18));
            g2.fillRoundRect(x + 1, y + 2, CELL_W, CELL_H, 8, 8);

            // Fondo
            g2.setColor(fondo);
            g2.fillRoundRect(x, y, CELL_W, CELL_H, 8, 8);
            g2.setColor(fondo.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, CELL_W, CELL_H, 8, 8);

            // ID (negrita, arriba)
            g2.setColor(TEXT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String idStr = String.valueOf(id);
            FontMetrics fmB = g2.getFontMetrics();
            g2.drawString(idStr,
                    x + (CELL_W - fmB.stringWidth(idStr)) / 2,
                    y + 13);

            // Nombre (pequeño, abajo)
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 9));
            FontMetrics fmS = g2.getFontMetrics();
            String nom = nombre.length() > 7 ? nombre.substring(0, 6) + "." : nombre;
            g2.drawString(nom,
                    x + (CELL_W - fmS.stringWidth(nom)) / 2,
                    y + CELL_H - 5);
        }

        // Dibuja el texto "null" al final de la cadena
        private void dibujarNull(Graphics2D g2, int x, int y, boolean destacado) {
            g2.setFont(new Font("Segoe UI", Font.ITALIC, 10));
            g2.setColor(destacado ? C_CAMINO.darker() : new Color(160, 165, 175));
            g2.drawString("null", x + 2, y + CELL_H / 2 + 4);
        }

        // Dibuja una flecha horizontal
        private void dibujarFlecha(Graphics2D g2,
                                    int x1, int y1, int x2, int y2, Color color) {
            g2.setColor(color);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawLine(x1, y1, x2, y2);
            // Punta de flecha
            int tip = 5;
            g2.drawLine(x2, y2, x2 - tip, y2 - tip + 2);
            g2.drawLine(x2, y2, x2 - tip, y2 + tip - 2);
        }
    
        /** Devuelve el color que corresponde a una entrada según el estado de animación. */
        private Color resolverColor(int bucketIdx, long id) {
            if (!faseBucket && id == idAnimado && bucketIdx == bucketAnimado)
                return colorFinal;
            if (faseBucket && bucketIdx == bucketAnimado)
                return C_CAMINO;
            return C_NORMAL;
        }
}
}
