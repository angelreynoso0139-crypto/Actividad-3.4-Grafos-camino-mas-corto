package modelo;

public class Arista {
    private Vertice origen;
    private Vertice destino;
    private double peso;

    public Arista(Vertice origen, Vertice destino, double peso) {
        this.origen = origen;
        this.destino = destino;
        this.peso = peso;
    }

    public Vertice getOrigen() { return origen; }
    public Vertice getDestino() { return destino; }
    public double getPeso() { return peso; }
}