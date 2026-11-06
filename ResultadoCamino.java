package optimizacion;

import java.util.List;

public class ResultadoCamino {
    private final String algoritmo;
    private final List<String> ruta;
    private final double costoTotal;
    private final int totalEvaluaciones;
    private final int totalAceptaciones;
    private final int totalDescartes;
    private final long tiempoEjecucionNanos;
    private final boolean alcanzable;

    public ResultadoCamino(String algoritmo, List<String> ruta, double costoTotal,
                           int totalEvaluaciones, int totalAceptaciones, int totalDescartes,
                           long tiempoEjecucionNanos, boolean alcanzable) {
        this.algoritmo = algoritmo;
        this.ruta = ruta;
        this.costoTotal = costoTotal;
        this.totalEvaluaciones = totalEvaluaciones;
        this.totalAceptaciones = totalAceptaciones;
        this.totalDescartes = totalDescartes;
        this.tiempoEjecucionNanos = tiempoEjecucionNanos;
        this.alcanzable = alcanzable;
    }

    public String getAlgoritmo() { return algoritmo; }
    public List<String> getRuta() { return ruta; }
    public double getCostoTotal() { return costoTotal; }
    public int getTotalEvaluaciones() { return totalEvaluaciones; }
    public int getTotalAceptaciones() { return totalAceptaciones; }
    public int getTotalDescartes() { return totalDescartes; }
    public long getTiempoEjecucionNanos() { return tiempoEjecucionNanos; }
    public boolean isAlcanzable() { return alcanzable; }
}