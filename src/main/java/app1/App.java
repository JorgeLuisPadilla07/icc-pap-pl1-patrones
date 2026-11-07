package app1;

import app1.evidencia.CasosAceptacion;
import app1.evidencia.GeneradorEvidencias;
import app1.evidencia.ResultadoCaso;
import app1.prototype.RegistroPrototipos;
import app1.servicio.ServicioPedidos;
import app1.vista.VentanaPedidos;

import javax.swing.SwingUtilities;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Punto de entrada.
 * <ul>
 *   <li>sin argumentos: abre la ventana Swing</li>
 *   <li>{@code --consola}: ejecuta CA01–CA11 e imprime el resultado</li>
 *   <li>{@code --evidencias}: ejecuta CA01–CA11 y escribe evidencias/resultados-ca.md</li>
 * </ul>
 */
public class App {

    public static void main(String[] args) {
        String modo = args.length > 0 ? args[0] : "";
        switch (modo) {
            case "--consola" -> imprimirCasos();
            case "--evidencias" -> generarEvidencias();
            default -> abrirVentana();
        }
    }

    private static void abrirVentana() {
        SwingUtilities.invokeLater(() -> {
            ServicioPedidos servicio = new ServicioPedidos(RegistroPrototipos.conCatalogoBase());
            new VentanaPedidos(servicio).setVisible(true);
        });
    }

    private static void imprimirCasos() {
        System.out.println("=== PedidoLab · casos de aceptación ===");
        for (ResultadoCaso r : new CasosAceptacion().ejecutar()) {
            System.out.printf("%-5s %-6s %s%n", r.id(), r.cumple() ? "OK" : "FALLO", r.real());
        }
    }

    private static void generarEvidencias() {
        List<ResultadoCaso> resultados = new CasosAceptacion().ejecutar();
        Path destino = Path.of("evidencias", "resultados-ca.md");
        try {
            GeneradorEvidencias.escribir(destino, resultados);
            System.out.println("Evidencias escritas en " + destino.toAbsolutePath());
        } catch (IOException ex) {
            System.err.println("No se pudo escribir " + destino + ": " + ex.getMessage());
            System.exit(1);
        }
    }
}