package modelo;

public class Vertice {
    private String id;
    private String nombre;

    public Vertice(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
}