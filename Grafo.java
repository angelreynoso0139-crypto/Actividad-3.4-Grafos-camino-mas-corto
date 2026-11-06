package modelo;

import java.util.*;

public class Grafo {
    private Map<String, Vertice> vertices = new LinkedHashMap<>();
    private List<Arista> aristas = new ArrayList<>();

    public void agregarVertice(Vertice v) {
        vertices.put(v.getId(), v);
    }

    public void agregarArista(String idOrigen, String idDestino, double peso) {
        Vertice origen = vertices.get(idOrigen);
        Vertice destino = vertices.get(idDestino);
        if (origen != null && destino != null) {
            aristas.add(new Arista(origen, destino, peso));
        }
    }

    public Map<String, Vertice> getVertices() { return vertices; }
    public List<Arista> getAristas() { return aristas; }

    public List<Arista> getAristasSalientes(Vertice u) {
        List<Arista> salientes = new ArrayList<>();
        for (Arista a : aristas) {
            if (a.getOrigen().getId().equals(u.getId())) {
                salientes.add(a);
            }
        }
        return salientes;
    }
}