package optimizacion;

import modelo.Grafo;
import modelo.Vertice;

import javax.swing.*;
import java.awt.*;

public class VentanaOptimizacion extends JFrame {

    private Grafo grafo;

    private JComboBox<String> cbOrigen;
    private JComboBox<String> cbDestino;
    private JComboBox<String> cbAlgoritmo;
    private JButton btnCalcular;
    private JButton btnComparar;
    private JButton btnLimpiar;
    private JTextArea txtAreaTraza;
    private JLabel lblResultadoRuta;
    private JLabel lblResultadoCosto;

    public VentanaOptimizacion(Grafo grafo) {
        this.grafo = grafo;
        initComponents();
        cargarNodosEnCombos();
    }

    private void initComponents() {
        setTitle("Actividad 3.4 - Módulo de Optimización de Rutas");
        setSize(950, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // PANEL SUPERIOR ORGANIZADO EN 2 FILAS PARA QUE NADA SE CORTE
        JPanel panelControles = new JPanel(new GridLayout(2, 1, 5, 5));
        panelControles.setBorder(BorderFactory.createTitledBorder("Configuración de Ruta y Algoritmo"));

        // Fila 1: Seleccionadores de Nodos y Algoritmo
        JPanel fila1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        cbOrigen = new JComboBox<>();
        cbDestino = new JComboBox<>();
        cbAlgoritmo = new JComboBox<>(new String[]{"Algoritmo de Dijkstra", "Algoritmo de Bellman-Ford"});

        fila1.add(new JLabel("Origen:"));
        fila1.add(cbOrigen);
        fila1.add(new JLabel("Destino:"));
        fila1.add(cbDestino);
        fila1.add(new JLabel("Algoritmo:"));
        fila1.add(cbAlgoritmo);

        // Fila 2: Los 3 Botones de Acción
        JPanel fila2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        btnCalcular = new JButton("Calcular Ruta");
        btnComparar = new JButton("Comparar Ambos");
        btnLimpiar = new JButton("Limpiar Traza");

        // Colores distintivos
        btnCalcular.setBackground(new Color(46, 139, 87)); // Verde
        btnCalcular.setForeground(Color.WHITE);
        
        btnComparar.setBackground(new Color(70, 130, 180)); // Azul
        btnComparar.setForeground(Color.WHITE);
        
        btnLimpiar.setBackground(new Color(178, 34, 34));   // Rojo
        btnLimpiar.setForeground(Color.WHITE);

        fila2.add(btnCalcular);
        fila2.add(btnComparar);
        fila2.add(btnLimpiar);

        panelControles.add(fila1);
        panelControles.add(fila2);

        add(panelControles, BorderLayout.NORTH);

        // PANEL CENTRO (Área de Texto JTextArea)
        txtAreaTraza = new JTextArea();
        txtAreaTraza.setEditable(false);
        txtAreaTraza.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtAreaTraza.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane scrollTraza = new JScrollPane(txtAreaTraza);
        scrollTraza.setBorder(BorderFactory.createTitledBorder("Registro Paso a Paso (Trazabilidad)"));
        add(scrollTraza, BorderLayout.CENTER);

        // PANEL INFERIOR (Resultados)
        JPanel panelInferior = new JPanel(new GridLayout(2, 1, 5, 5));
        panelInferior.setBorder(BorderFactory.createTitledBorder("Resultado Final"));

        lblResultadoRuta = new JLabel("Ruta Óptima: -", SwingConstants.LEFT);
        lblResultadoRuta.setFont(new Font("SansSerif", Font.BOLD, 14));

        lblResultadoCosto = new JLabel("Costo Total: -", SwingConstants.LEFT);
        lblResultadoCosto.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblResultadoCosto.setForeground(new Color(178, 34, 34));

        panelInferior.add(lblResultadoRuta);
        panelInferior.add(lblResultadoCosto);
        add(panelInferior, BorderLayout.SOUTH);

        // EVENTOS DE LOS BOTONES
        btnCalcular.addActionListener(e -> ejecutarAlgoritmoSeleccionado());
        btnComparar.addActionListener(e -> ejecutarComparativa());
        btnLimpiar.addActionListener(e -> limpiarTraza());
    }

    public void cargarNodosEnCombos() {
        cbOrigen.removeAllItems();
        cbDestino.removeAllItems();

        if (grafo != null && !grafo.getVertices().isEmpty()) {
            for (String idNodo : grafo.getVertices().keySet()) {
                cbOrigen.addItem(idNodo);
                cbDestino.addItem(idNodo);
            }
        }
    }

    private void ejecutarAlgoritmoSeleccionado() {
        if (!validarSeleccion()) return;

        txtAreaTraza.setText("");
        String origen = (String) cbOrigen.getSelectedItem();
        String destino = (String) cbDestino.getSelectedItem();
        String algoritmo = (String) cbAlgoritmo.getSelectedItem();

        ResultadoCamino res = null;
        if ("Algoritmo de Dijkstra".equals(algoritmo)) {
            res = AlgoritmosCaminosCortos.dijkstra(grafo, origen, destino, txtAreaTraza);
        } else {
            res = AlgoritmosCaminosCortos.bellmanFord(grafo, origen, destino, txtAreaTraza);
        }

        mostrarResultadoGUI(res);
    }

    private void ejecutarComparativa() {
        if (!validarSeleccion()) return;

        txtAreaTraza.setText("");
        String origen = (String) cbOrigen.getSelectedItem();
        String destino = (String) cbDestino.getSelectedItem();

        ResultadoCamino r1 = AlgoritmosCaminosCortos.dijkstra(grafo, origen, destino, txtAreaTraza);
        ResultadoCamino r2 = AlgoritmosCaminosCortos.bellmanFord(grafo, origen, destino, txtAreaTraza);

        if (r1 != null && r2 != null) {
            txtAreaTraza.append("\n==========================================================\n");
            txtAreaTraza.append(" TABLA COMPARATIVA DE DESEMPEÑO\n");
            txtAreaTraza.append("==========================================================\n");
            txtAreaTraza.append(String.format("%-22s | %-12s | %-12s\n", "Métrica", "Dijkstra", "Bellman-Ford"));
            txtAreaTraza.append("----------------------------------------------------------\n");
            txtAreaTraza.append(String.format("%-22s | %-12.2f | %-12.2f\n", "Costo Óptimo", r1.getCostoTotal(), r2.getCostoTotal()));
            txtAreaTraza.append(String.format("%-22s | %-12d | %-12d\n", "Evaluaciones", r1.getTotalEvaluaciones(), r2.getTotalEvaluaciones()));
            txtAreaTraza.append(String.format("%-22s | %-12d | %-12d\n", "Aceptaciones (Relaj)", r1.getTotalAceptaciones(), r2.getTotalAceptaciones()));
            txtAreaTraza.append(String.format("%-22s | %-12d | %-12d\n", "Descartes", r1.getTotalDescartes(), r2.getTotalDescartes()));
            txtAreaTraza.append(String.format("%-22s | %-12.4f | %-12.4f\n", "Tiempo Ejecución (ms)", r1.getTiempoEjecucionNanos() / 1e6, r2.getTiempoEjecucionNanos() / 1e6));
            txtAreaTraza.append("==========================================================\n");
        }
    }

    private void limpiarTraza() {
        txtAreaTraza.setText("");
        lblResultadoRuta.setText("Ruta Óptima: -");
        lblResultadoCosto.setText("Costo Total: -");
    }

    private boolean validarSeleccion() {
        if (cbOrigen.getSelectedItem() == null || cbDestino.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione nodos de Origen y Destino válidos.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void mostrarResultadoGUI(ResultadoCamino res) {
        if (res == null) return;

        if (res.isAlcanzable()) {
            lblResultadoRuta.setText("Ruta Óptima: " + String.join(" ➔ ", res.getRuta()));
            lblResultadoCosto.setText(String.format("Costo Total: %.2f", res.getCostoTotal()));
        } else {
            lblResultadoRuta.setText("Ruta Óptima: INACCESIBLE (No hay conexión)");
            lblResultadoCosto.setText("Costo Total: ∞");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Grafo g = crearGrafoPrueba();
            VentanaOptimizacion v = new VentanaOptimizacion(g);
            v.setVisible(true);
        });
    }

    private static Grafo crearGrafoPrueba() {
        Grafo g = new Grafo();
        g.agregarVertice(new Vertice("A", "Nodo A"));
        g.agregarVertice(new Vertice("B", "Nodo B"));
        g.agregarVertice(new Vertice("C", "Nodo C"));
        g.agregarVertice(new Vertice("D", "Nodo D"));
        g.agregarVertice(new Vertice("Z", "Nodo Aislado"));

        g.agregarArista("A", "B", 4.0);
        g.agregarArista("A", "C", 2.0);
        g.agregarArista("C", "B", 1.0);
        g.agregarArista("B", "D", 2.0);
        g.agregarArista("C", "D", 5.0);
        return g;
    }
}