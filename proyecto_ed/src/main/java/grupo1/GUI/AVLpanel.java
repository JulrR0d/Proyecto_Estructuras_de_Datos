package grupo1.GUI;

import java.awt.*;
import javax.swing.*;

import grupo1.Estructuras.ArbolAVL;
import grupo1.Estructuras.ArbolAVL.NodoVista;

public class AVLpanel extends JPanel {
    // ── Colores del tema (mismos que GUI.java) ──────────────────────────────
    private static final Color BG        = new Color(233, 236, 241);
    private static final Color TEXT      = new Color( 40,  48,  57);
    private static final Color C_NORMAL  = new Color(193, 199, 210); // nodo base
    private static final Color C_CAMINO  = new Color(255, 210,  80); // amarillo
    private static final Color C_INSERT  = new Color( 80, 200, 120); // verde
    private static final Color C_DELETE  = new Color(220,  80,  80); // rojo
    private static final Color C_FOUND   = new Color( 80, 160, 220); // azul busqueda

    private static final int R  = 22;   // radio del circulo del nodo
    private static final int DY = 64;   // separacion vertical entre niveles

    private ArbolAVL arbol;

    // ── Estado de animacion ─────────────────────────────────────────────────
    private long[] camino     = new long[0]; // IDs del camino actual
    private int    pasoActual = -1;          // qué nodo del camino está resaltado
    private long   idFinal    = -1;          // nodo destino (insert/delete/found)
    private Color  colorFinal = C_NORMAL;
    private Timer  animTimer;

    // Capacidad máxima de nodos que se dibuja (ajustar si el proyecto crece)
    private static final int MAX_NODOS = 256;

    public AVLpanel(ArbolAVL arbol) {
        this.arbol = arbol;
        setBackground(BG);
        setPreferredSize(new Dimension(340, 400));
        setBorder(BorderFactory.createTitledBorder("Árbol AVL"));
    }

    // ── API pública que llama GUI.java ──────────────────────────────────────
    /**
     * Lanza la animacion del camino recorrido en una operacion.
     *
     * @param caminoIds  arreglo de IDs visitados (de obtenerCaminoBusqueda)
     * @param idDestino  ID del nodo insertado / eliminado / encontrado
     * @param colorDest  C_INSERT, C_DELETE o C_FOUND segun la operacion
     */
    public void animar(long[] caminoIds, long idDestino, Color colorDest, Runnable alTerminar) {
        if (animTimer != null && animTimer.isRunning()) animTimer.stop();

        camino     = caminoIds;
        pasoActual = 0;
        idFinal    = idDestino;
        colorFinal = colorDest;

        animTimer = new Timer(400, e -> {
            pasoActual++;
            if (pasoActual >= camino.length) {
                ((Timer) e.getSource()).stop();
                repaint();
                Timer limpia = new Timer(1500, ev -> {
                    if (alTerminar != null) alTerminar.run(); // ← ejecuta aquí
                    camino     = new long[0];
                    pasoActual = -1;
                    idFinal    = -1;
                    repaint();
                });
                limpia.setRepeats(false);
                limpia.start();
            }
            repaint();
        });
        animTimer.start();
        repaint();
    }

    // Atajos para que GUI.java no maneje colores directamente
    public void animarInsercion(long[] camino, long id)  { animar(camino, id, C_INSERT, null); }
    public void animarEliminacion(long[] camino, long id, Runnable alTerminar) {
        animar(camino, id, C_DELETE, alTerminar);
    }
    public void animarBusqueda(long[] camino, long id)   { animar(camino, id, C_FOUND,  null); }

    /** Repinta sin animacion (para Actualizar) */
    public void refrescar() { repaint(); }

    // ── Dibujo ──────────────────────────────────────────────────────────────
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (arbol.vacio()) {
            g2.setColor(TEXT);
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            g2.drawString("Árbol vacío", getWidth() / 2 - 35, getHeight() / 2);
            return;
        }

        // Obtener snapshot plano del árbol
        NodoVista[] vistas = new NodoVista[MAX_NODOS];
        int total = arbol.obtenerVistas(vistas);

        // Calcular coordenadas de pantalla para cada nodo
        // x se distribuye proporcionalmente por posicion inorden
        // y depende del nivel
        int[] px = new int[total];
        int[] py = new int[total];
        int anchoUtil = getWidth() - 2 * R;

        for (int i = 0; i < total; i++) {
            // pos va de 0..total-1, mapeamos a ancho disponible
            px[i] = R + (int) ((vistas[i].pos + 0.5) * anchoUtil / total);
            py[i] = R + 20 + vistas[i].nivel * DY;
        }

        // 1) Dibujar aristas primero (quedan debajo de los nodos)
        g2.setColor(C_NORMAL);
        g2.setStroke(new BasicStroke(1.5f));
        for (int i = 0; i < total; i++) {
            if (vistas[i].idPadre == -1) continue;
            int pi = buscarIndice(vistas, total, vistas[i].idPadre);
            if (pi >= 0) {
                g2.drawLine(px[pi], py[pi], px[i], py[i]);
            }
        }

        // 2) Dibujar nodos
        for (int i = 0; i < total; i++) {
            Color fondo = resolverColor(vistas[i].id);

            // Circulo
            g2.setColor(fondo);
            g2.fillOval(px[i] - R, py[i] - R, 2 * R, 2 * R);
            g2.setColor(fondo.darker());
            g2.drawOval(px[i] - R, py[i] - R, 2 * R, 2 * R);

            // ID dentro del circulo
            g2.setColor(TEXT);
            g2.setFont(new Font("Segoe UI", Font.BOLD, 11));
            String label = String.valueOf(vistas[i].id);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, px[i] - fm.stringWidth(label) / 2, py[i] + fm.getAscent() / 2 - 1);

            // Nombre pequeño debajo del circulo
            g2.setFont(new Font("Segoe UI", Font.PLAIN, 10));
            fm = g2.getFontMetrics();
            String nom = vistas[i].nombre.length() > 6
                ? vistas[i].nombre.substring(0, 5) + "."
                : vistas[i].nombre;
            g2.drawString(nom, px[i] - fm.stringWidth(nom) / 2, py[i] + R + 12);
        }
    }

    // ── Helpers privados ────────────────────────────────────────────────────

    /** Determina el color de un nodo segun el estado de animacion actual */
    private Color resolverColor(long id) {
        // Nodo destino final
        if (id == idFinal && pasoActual >= camino.length) return colorFinal;
        // Nodo ya visitado en el camino (hasta pasoActual)
        for (int k = 0; k < pasoActual && k < camino.length; k++) {
            if (camino[k] == id) return C_CAMINO;
        }
        return C_NORMAL;
    }

    /** Busca el indice en el arreglo de vistas que corresponde a un ID dado */
    private int buscarIndice(NodoVista[] vistas, int total, long id) {
        for (int i = 0; i < total; i++) {
            if (vistas[i].id == id) return i;
        }
        return -1;
    }

    // Color publico para que GUI.java lo use en busqueda
    public static Color colorBusqueda() { return C_FOUND; }
}
