package grupo1.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.Border;

import grupo1.Clases.Paciente;
import grupo1.Estructuras.TablaHash;
import grupo1.Estructuras.ColaTriage;
import grupo1.Estructuras.Pila;
import grupo1.Features.RegistroCSV;
import grupo1.Features.ResumenTXT;

public class GUI {
	private static final Color BG = new Color(243, 244, 246); // Light slate/gray
	private static final Color CARD = new Color(255, 255, 255); // White
	private static final Color INSET = new Color(249, 250, 251); // Very light gray for inputs
	private static final Color TEXT = new Color(31, 41, 55); // Dark gray for text
	private static final Color BORDER_COLOR = new Color(229, 231, 235); // Light border
	private static final Color PRIMARY = new Color(59, 130, 246); // Primary blue for buttons
	private static final Color PRIMARY_TEXT = new Color(255, 255, 255); // White text for primary btn

	private final ColaTriage colaTriage;
	private final JFrame frame;
	private final JTextField idField;
	private final JTextField nombreField;
	private final JComboBox<Integer> triageCombo;
	private final JTextArea salida;
	private final JTextField edadField;
	private final JTextField EPSField;
	private final JTextField sintomasField;
	private final JComboBox<String> sexoCombo;
	private final JTextField buscarField;

	private final TablaHash tablaHash = new TablaHash(); // Genera la tabla hash

	private AVLpanel avlPanel; // panel con el arbol AVL

	private final RegistroCSV registro = new RegistroCSV();
	/**
	 * creacion del objeto para poder escribir el archivo csv
	 */

	// Pila de historial: registra cada paciente atendido en orden LIFO.
	private final Pila historialAtenciones = new Pila();

	public GUI(ColaTriage colaTriage) {

		this.colaTriage = colaTriage;

		frame = new JFrame("Triage - Control");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(1100, 700);
		frame.setMinimumSize(new Dimension(900, 600));
		frame.setLocation(90, 90);

		JPanel root = new JPanel(new BorderLayout(14, 14));
		root.setBackground(BG);
		root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));

		JLabel title = new JLabel("Panel de Control Triage");
		title.setForeground(TEXT);
		title.setFont(new Font("Segoe UI", Font.BOLD, 30));

		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		header.setOpaque(false);
		header.add(title);

		JPanel card = new RoundedPanel(CARD, 12);
		card.setBackground(CARD);
		card.setBorder(crearRelieveExterno());

		JPanel form = new JPanel(new GridBagLayout());
		form.setOpaque(false);

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 10, 8, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		JLabel idLabel = crearLabel("ID");
		JLabel nombreLabel = crearLabel("Nombre");
		JLabel triageLabel = crearLabel("Nivel triage");
		JLabel edadLabel = crearLabel("Edad");
		JLabel epsLabel = crearLabel("EPS");
		JLabel sintomasLabel = crearLabel("Síntomas");
		JLabel sexoLabel = crearLabel("Sexo");
		JLabel divisorRegistroBusqueda = crearLabel(
				"===============");

		idField = crearInput();
		nombreField = crearInput();
		edadField = crearInput();
		EPSField = crearInput();
		sintomasField = crearInput();
		// aplicar estilo redondeado y foco a campos editables
		aplicarEstiloRedondeado(idField, true);
		aplicarEstiloRedondeado(nombreField, true);
		aplicarEstiloRedondeado(edadField, true);
		aplicarEstiloRedondeado(EPSField, true);
		aplicarEstiloRedondeado(sintomasField, true);
		sexoCombo = new JComboBox<>(new String[] { "M", "F" });
		sexoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		sexoCombo.setBackground(INSET);
		sexoCombo.setForeground(TEXT);
		sexoCombo.setBorder(crearRelieveInterno());
		sexoCombo.setOpaque(true);
		aplicarEstiloRedondeado(sexoCombo, true);
		triageCombo = new JComboBox<>(new Integer[] { 1, 2, 3, 4, 5 });
		triageCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		triageCombo.setBackground(INSET);
		triageCombo.setForeground(TEXT);
		triageCombo.setBorder(crearRelieveInterno());
		triageCombo.setOpaque(true);
		aplicarEstiloRedondeado(triageCombo, true);

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		form.add(idLabel, gbc); // ID

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(idField, gbc); // ID

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		form.add(nombreLabel, gbc); // NOMBRE

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(nombreField, gbc); // NOMRBE

		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.weightx = 0;
		form.add(edadLabel, gbc); // EDAD

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(edadField, gbc); // EDAD

		gbc.gridx = 0;
		gbc.gridy = 3;
		gbc.weightx = 0;
		form.add(sexoLabel, gbc); // SEXO

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(sexoCombo, gbc); // SeXO

		gbc.gridx = 0;
		gbc.gridy = 4;
		gbc.weightx = 0;
		form.add(epsLabel, gbc); // EPS

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(EPSField, gbc);// EPS

		gbc.gridx = 0;
		gbc.gridy = 5;
		gbc.weightx = 0;
		form.add(sintomasLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(sintomasField, gbc);

		gbc.gridx = 0;
		gbc.gridy = 6;
		gbc.weightx = 0;
		form.add(triageLabel, gbc); // NIVEL TRI

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(triageCombo, gbc); // NIVEL TRI

		JButton registrar = crearBoton("Registrar");
		// usar una columna cercana para evitar crear muchas columnas vacías
		// que pueden provocar solapamientos al redimensionar
		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		form.add(registrar, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

		// Creacion del boton para la busqeuda de paciente mediante el ID

		gbc.gridx = 0;
		gbc.gridy = 8;
		gbc.weightx = 0;
		form.add(divisorRegistroBusqueda, gbc); // ID

		JLabel buscarLabel = crearLabel("Buscar paciente por ID");
		buscarField = crearInput();
		JButton buscarBtn = crearBoton("Buscar");
		aplicarEstiloRedondeado(buscarField, true);

		gbc.gridx = 0;
		gbc.gridy = 10;
		gbc.weightx = 0;
		form.add(buscarLabel, gbc);

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(buscarField, gbc);

		// colocar el botón de búsqueda en la misma zona razonable
		gbc.gridx = 2;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		form.add(buscarBtn, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		// Fin creacion boton para busqueda por ID

		JPanel actions = new JPanel(new GridBagLayout());
		actions.setOpaque(false);
		GridBagConstraints actionGbc = new GridBagConstraints();
		actionGbc.insets = new Insets(4, 6, 4, 6);
		actionGbc.anchor = GridBagConstraints.WEST;

		JButton siguiente = crearBoton("Ver siguiente");
		JButton atender = crearBoton("Atender");
		JButton estado = crearBoton("Actualizar");

		actionGbc.gridx = 0;
		actionGbc.gridy = 0;
		actions.add(siguiente, actionGbc);

		actionGbc.gridx = 0;
		actionGbc.gridy = 1;
		actions.add(atender, actionGbc);

		actionGbc.gridx = 1;
		actionGbc.gridy = 0;
		actions.add(estado, actionGbc);

		// creacion boton apra generar reporte
		JButton reporte = crearBoton("Generar reporte");

		actionGbc.gridx = 1;
		actionGbc.gridy = 1;
		actions.add(reporte, actionGbc);
		// fin creacion boton generacion de resporte

		salida = new JTextArea();
		salida.setEditable(false);
		salida.setLineWrap(true);
		salida.setWrapStyleWord(true);
		salida.setFont(new Font("Consolas", Font.PLAIN, 14));
		salida.setBackground(INSET);
		salida.setForeground(TEXT);
		// mantener padding interior en la consola, con bordes redondeados
		salida.setBorder(BorderFactory.createCompoundBorder(crearRelieveInterno(),
				BorderFactory.createEmptyBorder(12, 12, 12, 12)));
		aplicarEstiloRedondeado(salida, false);

		JScrollPane scroll = new JScrollPane(salida);
		scroll.getViewport().setBackground(INSET);
		// scroll con borde redondeado
		scroll.setBorder(crearRelieveInterno());
		aplicarEstiloRedondeado(scroll, false);
		// permitir que el scroll adapte su tamaño dinámicamente
		scroll.setMinimumSize(new Dimension(200, 120));

		JPanel content = new JPanel(new BorderLayout(10, 10));
		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		// Agrupar el formulario y colocar los botones justo debajo, encima de la
		// consola
		JPanel topBlock = new JPanel(new BorderLayout());
		topBlock.setOpaque(false);
		// formulario en la parte superior del bloque
		topBlock.add(form, BorderLayout.NORTH);
		// acciones directamente debajo del formulario
		topBlock.add(actions, BorderLayout.SOUTH);
		content.add(topBlock, BorderLayout.NORTH);
		// Poner el área de salida en el centro para que ocupe el espacio
		// restante y no se sobreponga al formulario cuando se redimensiona
		content.add(scroll, BorderLayout.CENTER);

		// avlPanel = new AVLpanel(tablaHash);
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, avlPanel, content);
		split.setDividerLocation(420);
		split.setResizeWeight(0.35);
		split.setDividerSize(4);
		card.add(split, BorderLayout.CENTER);
		root.add(header, BorderLayout.NORTH);
		root.add(card, BorderLayout.CENTER);
		frame.add(root);

		registrar.addActionListener(e -> registrarPaciente());
		siguiente.addActionListener(e -> mostrarSiguiente());
		atender.addActionListener(e -> atenderPaciente());
		// estado.addActionListener(e -> actualizarEstado());
		buscarBtn.addActionListener(e -> buscarPaciente());
		reporte.addActionListener(e -> generarReporte());

		Paciente[] existentes = colaTriage.obtenerSiguientesPacientes(colaTriage.totalPacientes());
		for (Paciente p : existentes) {
			tablaHash.insertar(p.getId(), p);
		}

		// actualizarEstado();
	}

	public void mostrar() {
		frame.setVisible(true);
	}

	private JLabel crearLabel(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		label.setForeground(new Color(63, 63, 70));
		return label;
	}

	private JTextField crearInput() {
		JTextField input = new JTextField();
		input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		input.setBackground(INSET);
		input.setForeground(TEXT);
		input.setOpaque(true);
		// keep border simple to preserve external layout paddings
		input.setBorder(crearRelieveInterno());
		return input;
	}

	private JButton crearBoton(String texto) {
		JButton boton = new JButton(texto);
		boton.setFocusPainted(false);
		boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
		boton.setBackground(PRIMARY);
		boton.setForeground(PRIMARY_TEXT);
		boton.setOpaque(true);
		boton.setContentAreaFilled(true);
		boton.setBorderPainted(false);
		boton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		// subtle rounded border to match overall roundness
		boton.setBorder(new RoundedBorder(8, BORDER_COLOR));

		// Hover / press feedback without affecting existing animations
		final Color normal = PRIMARY;
		final Color hover = new Color(37, 99, 235);
		final Color pressed = new Color(30, 64, 175);
		boton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent e) {
				boton.setBackground(hover);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent e) {
				boton.setBackground(normal);
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent e) {
				boton.setBackground(pressed);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent e) {
				boton.setBackground(normal);
			}
		});

		return boton;
	}

	// Aplica estilo redondeado y efecto de foco a componentes editables
	private void aplicarEstiloRedondeado(javax.swing.JComponent comp, boolean focusable) {
		comp.setOpaque(true);
		comp.setBackground(INSET);
		comp.setForeground(TEXT);
		comp.setBorder(crearRelieveInterno());
		if (focusable) {
			comp.addFocusListener(new java.awt.event.FocusAdapter() {
				@Override
				public void focusGained(java.awt.event.FocusEvent e) {
					comp.setBorder(new RoundedBorder(8, PRIMARY));
				}

				@Override
				public void focusLost(java.awt.event.FocusEvent e) {
					comp.setBorder(crearRelieveInterno());
				}
			});
		}
	}

	// Metodo para busqueda de los pacientes
	private void buscarPaciente() {
		String textoID = buscarField.getText();
		if (textoID.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "Ingresa un id", "Campo vacio", JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			Long idBuscar = Long.parseLong(textoID.trim());
			Paciente busq_id = tablaHash.buscar(idBuscar);
			if (busq_id == null) {
				JOptionPane.showMessageDialog(frame, "El paciente no existe", "No encontrado",
						JOptionPane.WARNING_MESSAGE);
				return;
			} else {
				salida.setText(
						"Paciente encontrado con ID dado\n\n" + busq_id + "\n\n" + estadoTexto());
			}
			buscarField.setText("");
		} catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(frame, "Ingresa un numero entero", "Dato inválido",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

	}

	// metodo para generar reporte en txt

	private void generarReporte() {
		new ResumenTXT().generarResumenDia();
		JOptionPane.showMessageDialog(
				frame,
				"Reporte del dia generado",
				"Reporte",
				JOptionPane.INFORMATION_MESSAGE);
	}

	private void registrarPaciente() {
		try {
			long id = Long.parseLong(idField.getText().trim());
			String nombre = nombreField.getText().trim();

			if (tablaHash.buscar(id) != null) {
				throw new IllegalArgumentException("Ya existe un paciente con el ID " + id);
			}

			if (nombre.isEmpty()) {
				throw new IllegalArgumentException("El nombre no puede estar vacio.");
			}

			int edad = Integer.parseInt(edadField.getText().trim());

			char sexo = ((String) sexoCombo.getSelectedItem()).charAt(0);

			String EPS = EPSField.getText().trim();

			if (EPS.isEmpty()) {
				throw new IllegalArgumentException("Tienes que ingresar una EPS");
			}

			String sintoma = sintomasField.getText().trim();

			if (sintoma.isEmpty()) {
				throw new IllegalArgumentException("Debes ingresar los sintomas del paciente");
			}

			byte triage = ((Integer) triageCombo.getSelectedItem()).byteValue();

			Paciente paciente = new Paciente(id, nombre, edad, sexo, EPS, sintoma, triage);
			colaTriage.insertarPaciente(paciente);
			tablaHash.insertar(paciente.getId(), paciente);
			// long[] camino = tablaHash.obtenerCaminoBusqueda(id);
			// avlPanel.animarInsercion(camino, id);

			salida.setText("Paciente registrado\n\n" + paciente + "\n\n" + estadoTexto());

			idField.setText("");
			nombreField.setText("");
			edadField.setText("");
			EPSField.setText("");
			sintomasField.setText("");
			sexoCombo.setSelectedIndex(0);
			triageCombo.setSelectedIndex(0);
			idField.requestFocus();

		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(frame, "ID/Edad deben ser numericos.", "Dato invalido",
					JOptionPane.WARNING_MESSAGE);
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(frame, ex.getMessage(), "Dato invalido", JOptionPane.WARNING_MESSAGE);
		}
	}

	private void mostrarSiguiente() {
		Paciente siguiente = colaTriage.verSiguientePaciente();
		if (siguiente == null) {
			salida.setText("No hay pacientes en espera.\n\n" + estadoTexto());
			return;
		}
		salida.setText("Siguiente paciente\n\n" + siguiente + "\n\n" + estadoTexto());
	}

	private void atenderPaciente() {
		Paciente sig = colaTriage.verSiguientePaciente();

		if (sig == null) {
			salida.setText("No hay pacientes para atender.\n\n" + estadoTexto());
			return;
		}
		// long[] camino = tablaHash.obtenerCaminoBusqueda(sig.getId());
		// Animar primero; la eliminación real ocurre al terminar
		/*
		 * avlPanel.animarEliminacion(camino, sig.getId(), () -> {
		 * Paciente atendido = colaTriage.atenderPaciente();
		 * registro.registrarAtencion(atendido); // anota el paciente en le csv
		 * historialAtenciones.push(atendido); // registro LIFO
		 * tablaHash.eliminar(atendido.getId());
		 * salida.setText("Paciente atendido\n\n" + atendido + "\n\n" + estadoTexto());
		 * avlPanel.refrescar();
		 * });
		 */
	}

	/*
	 * private void actualizarEstado() {
	 * salida.setText(estadoTexto());
	 * avlPanel.refrescar();
	 * }
	 */

	private String estadoTexto() {
		StringBuilder sb = new StringBuilder();
		sb.append("Estado actual\n");
		sb.append("Total en espera: ").append(colaTriage.totalPacientes()).append("\n");
		for (int i = ColaTriage.TRIAGE_MIN; i <= ColaTriage.TRIAGE_MAX; i++) {
			sb.append("Triage ").append(i).append(": ").append(colaTriage.pacientesPorNivel(i)).append("\n");
		}
		return sb.toString();
	}

	// Deshace ultima atencion
	public boolean deshacerUltimaAtencion() {
		Paciente p = historialAtenciones.pop();
		if (p == null)
			return false;
		colaTriage.insertarPaciente(p);
		return true;
	}

	private Border crearRelieveExterno() {
		// Rounded subtle border for outer card
		return new RoundedBorder(12, BORDER_COLOR);
	}

	private Border crearRelieveInterno() {
		// Slightly rounded border for inputs
		return new RoundedBorder(8, BORDER_COLOR);
	}

	// Rounded border implementation
	private static class RoundedBorder implements Border {
		private final int radius;
		private final Color color;

		RoundedBorder(int radius, Color color) {
			this.radius = radius;
			this.color = color;
		}

		@Override
		public Insets getBorderInsets(java.awt.Component c) {
			return new Insets(4, 8, 4, 8);
		}

		@Override
		public boolean isBorderOpaque() {
			return false;
		}

		@Override
		public void paintBorder(java.awt.Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setColor(color);
			g2.draw(new RoundRectangle2D.Double(x + 0.5, y + 0.5, width - 1, height - 1, radius, radius));
			g2.dispose();
		}
	}

	// Rounded panel with subtle shadow and rounded background
	private static class RoundedPanel extends JPanel {
		private final int radius;
		private final Color bg;

		RoundedPanel(Color bg, int radius) {
			super(new BorderLayout());
			this.radius = radius;
			this.bg = bg;
			setOpaque(false);
		}

		@Override
		protected void paintComponent(Graphics g) {
			int shadow = 6;
			int x = shadow;
			int y = shadow;
			int w = getWidth() - shadow * 2;
			int h = getHeight() - shadow * 2;

			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// subtle shadow
			g2.setColor(new Color(0, 0, 0, 18));
			g2.fill(new RoundRectangle2D.Double(x + 1, y + 1, w, h, radius, radius));

			// main background
			g2.setColor(bg);
			g2.fill(new RoundRectangle2D.Double(x, y, w, h, radius, radius));
			g2.dispose();
			super.paintComponent(g);
		}
	}
}
