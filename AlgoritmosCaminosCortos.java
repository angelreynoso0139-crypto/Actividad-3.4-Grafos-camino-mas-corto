package optimizacion;

import modelo.Arista;
import modelo.Grafo;
import modelo.Vertice;

import javax.swing.JTextArea;
import java.util.*;

public class AlgoritmosCaminosCortos {

    public static ResultadoCamino dijkstra(Grafo grafo, String idOrigen, String idDestino, JTextArea areaLog) {
        long inicioTiempo = System.nanoTime();
        areaLog.append("==========================================================\n");
        areaLog.append(" INICIO: ALGORITMO DE DIJKSTRA\n");
        areaLog.append(" Origen: " + idOrigen + " | Destino: " + idDestino + "\n");
        areaLog.append("==========================================================\n\n");

        if (!validarEntrada(grafo, idOrigen, idDestino, areaLog)) return null;

        for (Arista arista : grafo.getAristas()) {
            if (arista.getPeso() < 0) {
                areaLog.append("ERROR CRÍTICO: Dijkstra no admite aristas negativas (" 
                        + arista.getOrigen().getNombre() + " -> " + arista.getDestino().getNombre() 
                        + " = " + arista.getPeso() + "). Operación cancelada.\n");
                return null;
            }
        }

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();
        Set<String> visitados = new HashSet<>();
        PriorityQueue<NodoDistancia> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.distancia));

        int evaluaciones = 0, aceptaciones = 0, descartes = 0;

        areaLog.append("--- FASE 1: INICIALIZACIÓN DE DISTANCIAS ---\n");
        for (Vertice v : grafo.getVertices().values()) {
            distancias.put(v.getId(), Double.POSITIVE_INFINITY);
            predecesores.put(v.getId(), null);
            areaLog.append(String.format(" Nodo [%s]: dist = ∞, predecesor = null\n", v.getNombre()));
        }

        distancias.put(idOrigen, 0.0);
        pq.add(new NodoDistancia(idOrigen, 0.0));
        areaLog.append(String.format(" Nodo Origen [%s]: dist = 0.0 asignada.\n\n", idOrigen));

        areaLog.append("--- FASE 2: BÚSQUEDA Y RELAJACIÓN DE ARISTAS ---\n");

        while (!pq.isEmpty()) {
            NodoDistancia actual = pq.poll();
            String u = actual.idNode;

            if (visitados.contains(u)) continue;
            visitados.add(u);
            evaluaciones++;

            areaLog.append(String.format("\n>> Extraído de Cola de Prioridad: [%s] (Costo acum: %.2f)\n", u, actual.distancia));

            if (u.equals(idDestino)) {
                areaLog.append("   ¡Nodo Destino alcanzado! Finalizando expansión.\n");
                break;
            }

            Vertice verticeU = grafo.getVertices().get(u);
            for (Arista arista : grafo.getAristasSalientes(verticeU)) {
                String v = arista.getDestino().getId();
                double peso = arista.getPeso();

                areaLog.append(String.format("   Analizando %s -> %s (Peso: %.2f): ", u, v, peso));

                if (distancias.get(u) + peso < distancias.get(v)) {
                    double distAnterior = distancias.get(v);
                    double nuevaDist = distancias.get(u) + peso;
                    distancias.put(v, nuevaDist);
                    predecesores.put(v, u);
                    pq.add(new NodoDistancia(v, nuevaDist));
                    aceptaciones++;

                    areaLog.append("[ACEPTADO - RELAJACIÓN]\n");
                    areaLog.append(String.format("      Dist. previa [%s]: %s -> Nueva dist: %.2f (Vía %s)\n",
                            v, (distAnterior == Double.POSITIVE_INFINITY ? "∞" : String.format("%.2f", distAnterior)), nuevaDist, u));
                } else {
                    descartes++;
                    areaLog.append("[DESCARTADO]\n");
                    areaLog.append(String.format("      Costo propuesto (%.2f + %.2f = %.2f) >= Costo actual [%s] (%.2f)\n",
                            distancias.get(u), peso, (distancias.get(u) + peso), v, distancias.get(v)));
                }
            }
        }

        long finTiempo = System.nanoTime();
        boolean alcanzable = distancias.get(idDestino) != Double.POSITIVE_INFINITY;
        List<String> rutaFinal = reconstruirRuta(predecesores, idOrigen, idDestino);

        imprimirResultadoFinal(areaLog, "Dijkstra", idOrigen, idDestino, alcanzable, rutaFinal, distancias.get(idDestino));

        return new ResultadoCamino("Dijkstra", rutaFinal, distancias.get(idDestino),
                evaluaciones, aceptaciones, descartes, (finTiempo - inicioTiempo), alcanzable);
    }

    public static ResultadoCamino bellmanFord(Grafo grafo, String idOrigen, String idDestino, JTextArea areaLog) {
        long inicioTiempo = System.nanoTime();
        areaLog.append("==========================================================\n");
        areaLog.append(" INICIO: ALGORITMO DE BELLMAN-FORD\n");
        areaLog.append(" Origen: " + idOrigen + " | Destino: " + idDestino + "\n");
        areaLog.append("==========================================================\n\n");

        if (!validarEntrada(grafo, idOrigen, idDestino, areaLog)) return null;

        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> predecesores = new HashMap<>();
        int numVertices = grafo.getVertices().size();
        int evaluaciones = 0, aceptaciones = 0, descartes = 0;

        areaLog.append("--- FASE 1: INICIALIZACIÓN DE DISTANCIAS ---\n");
        for (Vertice v : grafo.getVertices().values()) {
            distancias.put(v.getId(), Double.POSITIVE_INFINITY);
            predecesores.put(v.getId(), null);
            areaLog.append(String.format(" Nodo [%s]: dist = ∞, predecesor = null\n", v.getNombre()));
        }
        distancias.put(idOrigen, 0.0);
        areaLog.append(String.format(" Nodo Origen [%s]: dist = 0.0 asignada.\n\n", idOrigen));

        areaLog.append("--- FASE 2: RELAJACIÓN ITERATIVA (|V|-1 = " + (numVertices - 1) + " pasadas) ---\n");

        List<Arista> aristas = grafo.getAristas();
        for (int i = 1; i <= numVertices - 1; i++) {
            areaLog.append(String.format("\n--- Iteración #%d --- \n", i));
            boolean huboCambios = false;

            for (Arista arista : aristas) {
                String u = arista.getOrigen().getId();
                String v = arista.getDestino().getId();
                double peso = arista.getPeso();
                evaluaciones++;

                if (distancias.get(u) != Double.POSITIVE_INFINITY) {
                    areaLog.append(String.format("   Evaluando %s -> %s (Peso: %.2f): ", u, v, peso));
                    if (distancias.get(u) + peso < distancias.get(v)) {
                        double distAnterior = distancias.get(v);
                        double nuevaDist = distancias.get(u) + peso;
                        distancias.put(v, nuevaDist);
                        predecesores.put(v, u);
                        aceptaciones++;
                        huboCambios = true;

                        areaLog.append("[ACEPTADO]\n");
                        areaLog.append(String.format("      Relajación exitosa: %s pasa de %s a %.2f (vía %s)\n",
                                v, (distAnterior == Double.POSITIVE_INFINITY ? "∞" : String.format("%.2f", distAnterior)), nuevaDist, u));
                    } else {
                        descartes++;
                        areaLog.append("[DESCARTADO]\n");
                        areaLog.append(String.format("      Costo vía %s (%.2f) >= actual (%.2f)\n",
                                u, (distancias.get(u) + peso), distancias.get(v)));
                    }
                }
            }

            if (!huboCambios) {
                areaLog.append(String.format("\n  Convergencia alcanzada en la iteración %d.\n", i));
                break;
            }
        }

        areaLog.append("\n--- FASE 3: VERIFICACIÓN DE CICLOS NEGATIVOS ---\n");
        for (Arista arista : aristas) {
            String u = arista.getOrigen().getId();
            String v = arista.getDestino().getId();
            double peso = arista.getPeso();
            if (distancias.get(u) != Double.POSITIVE_INFINITY && distancias.get(u) + peso < distancias.get(v)) {
                areaLog.append("ERROR CRÍTICO: Ciclo de peso negativo detectado. No existe camino mínimo bien definido.\n");
                return null;
            }
        }
        areaLog.append(" Verificación completada sin ciclos negativos.\n");

        long finTiempo = System.nanoTime();
        boolean alcanzable = distancias.get(idDestino) != Double.POSITIVE_INFINITY;
        List<String> rutaFinal = reconstruirRuta(predecesores, idOrigen, idDestino);

        imprimirResultadoFinal(areaLog, "Bellman-Ford", idOrigen, idDestino, alcanzable, rutaFinal, distancias.get(idDestino));

        return new ResultadoCamino("Bellman-Ford", rutaFinal, distancias.get(idDestino),
                evaluaciones, aceptaciones, descartes, (finTiempo - inicioTiempo), alcanzable);
    }

    private static boolean validarEntrada(Grafo grafo, String origen, String destino, JTextArea areaLog) {
        if (grafo == null || grafo.getVertices().isEmpty()) {
            areaLog.append("ERROR: El grafo está vacío.\n");
            return false;
        }
        if (!grafo.getVertices().containsKey(origen)) {
            areaLog.append("ERROR: Nodo origen '" + origen + "' no existe.\n");
            return false;
        }
        if (!grafo.getVertices().containsKey(destino)) {
            areaLog.append("ERROR: Nodo destino '" + destino + "' no existe.\n");
            return false;
        }
        return true;
    }

    private static List<String> reconstruirRuta(Map<String, String> predecesores, String origen, String destino) {
        LinkedList<String> ruta = new LinkedList<>();
        if (predecesores.get(destino) == null && !origen.equals(destino)) return ruta;
        String actual = destino;
        while (actual != null) {
            ruta.addFirst(actual);
            if (actual.equals(origen)) break;
            actual = predecesores.get(actual);
        }
        return ruta;
    }

    private static void imprimirResultadoFinal(JTextArea areaLog, String alg, String origen, String destino,
                                                boolean alcanzable, List<String> ruta, double costo) {
        areaLog.append("\n==========================================================\n");
        areaLog.append(" RESULTADO FINAL (" + alg.toUpperCase() + ")\n");
        areaLog.append("==========================================================\n");
        if (!alcanzable) {
            areaLog.append(" ESTADO: INACCESIBLE - No existe ningún camino entre " + origen + " y " + destino + ".\n");
        } else {
            areaLog.append(" ESTADO: RUTA ÓPTIMA ENCONTRADA\n");
            areaLog.append(" SECUENCIA DE NODOS: " + String.join(" ➔ ", ruta) + "\n");
            areaLog.append(String.format(" COSTO TOTAL ACUMULADO: %.2f\n", costo));
        }
        areaLog.append("==========================================================\n\n");
    }

    private static class NodoDistancia {
        String idNode;
        double distancia;
        NodoDistancia(String idNode, double distancia) {
            this.idNode = idNode;
            this.distancia = distancia;
        }
    }
}