package grupo1.GUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

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
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import grupo1.Clases.Paciente;
import grupo1.Estructuras.ColaTriage;
import grupo1.GUI.AVLpanel;

public class GUI {
	private static final Color BG = new Color(233, 236, 241);
	private static final Color CARD = new Color(233, 236, 241);
	private static final Color INSET = new Color(225, 229, 235);
	private static final Color TEXT = new Color(40, 48, 57);

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

	private AVLpanel avlPanel; // panel con el arbol AVL

	public GUI(ColaTriage colaTriage) {
		this.colaTriage = colaTriage;

		frame = new JFrame("Triage - Control");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(860, 560);
		frame.setMinimumSize(new Dimension(700, 460));
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

		JPanel card = new JPanel(new BorderLayout(12, 12));
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
		JLabel edadLabel    = crearLabel("Edad");
		JLabel epsLabel     = crearLabel("EPS");
		JLabel sintomasLabel = crearLabel("Síntomas");
		JLabel sexoLabel    = crearLabel("Sexo");

		idField = crearInput();
		nombreField = crearInput();
		edadField = crearInput();
		EPSField = crearInput();
		sintomasField = crearInput();
		sexoCombo    = new JComboBox<>(new String[]{"M", "F"});
		sexoCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		sexoCombo.setBackground(INSET);
		sexoCombo.setForeground(TEXT);
		sexoCombo.setBorder(crearRelieveInterno())
		triageCombo = new JComboBox<>(new Integer[] {1, 2, 3, 4, 5});
		triageCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		triageCombo.setBackground(INSET);
		triageCombo.setForeground(TEXT);
		triageCombo.setBorder(crearRelieveInterno());

		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 0;
		form.add(idLabel, gbc); //ID

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(idField, gbc); //ID

		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		form.add(nombreLabel, gbc); //NOMBRE

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(nombreField, gbc); //NOMRBE

		gbc.gridx = 0; 
		gbc.gridy = 2; 
		gbc.weightx = 0;
		form.add(edadLabel, gbc); //EDAD
		
		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(edadField, gbc); //EDAD

		gbc.gridx = 0; 
		gbc.gridy = 3; 
		gbc.weightx = 0;
		form.add(sexoLabel, gbc); //SEXO
		
		gbc.gridx = 1; 
		gbc.weightx = 1;
		form.add(sexoCombo, gbc); //SeXO

		gbc.gridx = 0; 
		gbc.gridy = 4; 
		gbc.weightx = 0;
		form.add(EPSLabel, gbc); //EPS
		
		gbc.gridx = 1; 
		gbc.weightx = 1;
		form.add(EPSField, gbc);//EPS

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
		form.add(triageLabel, gbc); //IVEL TRI

		gbc.gridx = 1;
		gbc.weightx = 1;
		form.add(triageCombo, gbc); //NIVEL TRI

		JButton registrar = crearBoton("Registrar");
		gbc.gridx = 7;
		gbc.weightx = 0;
		gbc.fill = GridBagConstraints.NONE;
		gbc.anchor = GridBagConstraints.WEST;
		form.add(registrar, gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.WEST;

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

		salida = new JTextArea();
		salida.setEditable(false);
		salida.setLineWrap(true);
		salida.setWrapStyleWord(true);
		salida.setFont(new Font("Consolas", Font.PLAIN, 14));
		salida.setBackground(INSET);
		salida.setForeground(TEXT);
		salida.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

		JScrollPane scroll = new JScrollPane(salida);
		scroll.getViewport().setBackground(INSET);
		scroll.setBorder(crearRelieveInterno());
		scroll.setPreferredSize(new Dimension(520, 260));

		JPanel content = new JPanel(new BorderLayout(10, 10));
		content.setOpaque(false);
		content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		content.add(form, BorderLayout.NORTH);
		content.add(actions, BorderLayout.CENTER);
		content.add(scroll, BorderLayout.SOUTH);

		avlPanel = new AVLpanel(colaTriage.getArbolAVL());
		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, avlPanel, content);
		split.setDividerLocation(340);
		split.setResizeWeight(0.35);
		split.setDividerSize(4);
		card.add(split, BorderLayout.CENTER);
		root.add(header, BorderLayout.NORTH);
		root.add(card, BorderLayout.CENTER);
		frame.add(root);

		registrar.addActionListener(e -> registrarPaciente());
		siguiente.addActionListener(e -> mostrarSiguiente());
		atender.addActionListener(e -> atenderPaciente());
		estado.addActionListener(e -> actualizarEstado());

		actualizarEstado();
	}

	public void mostrar() {
		frame.setVisible(true);
	}

	private JLabel crearLabel(String texto) {
		JLabel label = new JLabel(texto);
		label.setFont(new Font("Segoe UI", Font.BOLD, 15));
		label.setForeground(TEXT);
		return label;
	}

	private JTextField crearInput() {
		JTextField input = new JTextField();
		input.setFont(new Font("Segoe UI", Font.PLAIN, 15));
		input.setBackground(INSET);
		input.setForeground(TEXT);
		input.setBorder(crearRelieveInterno());
		return input;
	}

	private JButton crearBoton(String texto) {
		JButton boton = new JButton(texto);
		boton.setFocusPainted(false);
		boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
		boton.setBackground(CARD);
		boton.setForeground(TEXT);
		boton.setBorder(crearRelieveExterno());
		return boton;
	}

	private void registrarPaciente() {
		try {
			long id = Long.parseLong(idField.getText().trim());
			String nombre = nombreField.getText().trim();
			
			if (nombre.isEmpty()) {
				throw new IllegalArgumentException("El nombre no puede estar vacio.");
			}
			
			int edad = Integer.parseInt(edadField.getText().trim());
			
			char sexo   = ((String) sexoCombo.getSelectedItem()).charAt(0);
			
	        String EPS  = epsField.getText().trim();
			
			if (EPS.isEmpty()) {
				throw new IllegalArgumentException("Tienes que ingresar una EPS");
			}
			
	        String sintoma = sintomasField.getText().trim();
			
			if (sintoma.isEpmty()) {
				throw new IllegalArgumentException ("Debes ingresar los sintomas del paciente");
			}
			
			byte triage = ((Integer) triageCombo.getSelectedItem()).byteValue();
			
			Paciente paciente = new Paciente(id, nombre, edad, sexo, EPS, sintoma, triage);
			colaTriage.insertarPaciente(paciente);
			long[] camino = colaTriage.getArbolAVL().obtenerCaminoBusqueda(id);
			avlPanel.animarInsercion(camino, id);

			salida.setText("Paciente registrado\n" + paciente + "\n\n" + estadoTexto());
			
			idField.setText("");
			nombreField.setText("");
			edadField.setText("");
			EPSField.setText("");
			sintomaField.setText("");
			sexoCombo.setSelectedIndex(0);
			triageCombo.setSelectedIndex(0);
			idField.requestFocus();
			
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(frame, "El ID debe ser numerico.", "Dato invalido", JOptionPane.WARNING_MESSAGE);
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
		salida.setText("Siguiente paciente\n" + siguiente + "\n\n" + estadoTexto());
	}

	private void atenderPaciente() {
		Paciente sig = colaTriage.verSiguientePaciente();
		// Capturar camino ANTES de eliminar
		long[] camino = colaTriage.getArbolAVL().obtenerCaminoBusqueda(sig.getId());

		// Animar primero; la eliminación real ocurre al terminar
		avlPanel.animarEliminacion(camino, sig.getId(), () -> {
			Paciente atendido = colaTriage.atenderPaciente();
			salida.setText("Paciente atendido\n" + atendido + "\n\n" + estadoTexto());
			avlPanel.refrescar();
		});
	}

	private void actualizarEstado() {
		salida.setText(estadoTexto());
		avlPanel.refrescar();
	}

	private String estadoTexto() {
		StringBuilder sb = new StringBuilder();
		sb.append("Estado actual\n");
		sb.append("Total en espera: ").append(colaTriage.totalPacientes()).append("\n");
		for (int i = ColaTriage.TRIAGE_MIN; i <= ColaTriage.TRIAGE_MAX; i++) {
			sb.append("Triage ").append(i).append(": ").append(colaTriage.pacientesPorNivel(i)).append("\n");
		}
		return sb.toString();
	}

	private Border crearRelieveExterno() {
		return BorderFactory.createBevelBorder(
			BevelBorder.RAISED,
			new Color(255, 255, 255),
			new Color(247, 250, 255),
			new Color(193, 199, 210),
			new Color(205, 211, 222)
		);
	}

	private Border crearRelieveInterno() {
		return BorderFactory.createBevelBorder(
			BevelBorder.LOWERED,
			new Color(255, 255, 255),
			new Color(246, 249, 254),
			new Color(193, 199, 210),
			new Color(205, 211, 222)
		);
	}
}
