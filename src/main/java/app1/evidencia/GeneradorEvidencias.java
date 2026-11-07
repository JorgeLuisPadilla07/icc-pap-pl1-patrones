package app1.evidencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Convierte los resultados de CA01–CA11 en una tabla Markdown lista para pegar en evidencias/APP1.md.
 * Si existe evidencias/capturas/caXX.png, la tabla enlaza la captura automáticamente.
 */
public final class GeneradorEvidencias {

    private GeneradorEvidencias() {
    }

    public static void escribir(Path destino, List<ResultadoCaso> resultados) throws IOException {
        Path carpeta = destino.toAbsolutePath().getParent();
        Files.createDirectories(carpeta);
        Files.writeString(destino, generar(carpeta, resultados), StandardCharsets.UTF_8);
    }

    static String generar(Path carpeta, List<ResultadoCaso> resultados) {
        long cumplidos = resultados.stream().filter(ResultadoCaso::cumple).count();
        String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        StringBuilder md = new StringBuilder();
        md.append("# Resultados de ejecución · CA01–CA11\n\n");
        md.append("Generado: ").append(fecha).append("  \n");
        md.append("Casos cumplidos: **").append(cumplidos).append('/').append(resultados.size()).append("**\n\n");
        md.append("| Caso | Entrada | Esperado | Real | Evidencia | Estado |\n");
        md.append("|---|---|---|---|---|---|\n");
        for (ResultadoCaso r : resultados) {
            md.append("| ").append(r.id())
                    .append(" | ").append(celda(r.entrada()))
                    .append(" | ").append(celda(r.esperado()))
                    .append(" | ").append(celda(r.real()))
                    .append(" | ").append(evidencia(carpeta, r))
                    .append(" | ").append(r.cumple() ? "✅ " : "❌ ").append(r.estado())
                    .append(" |\n");
        }
        md.append("\n> Los importes proceden de la ejecución de `ServicioPedidos`, no de valores escritos en la GUI.\n");
        return md.toString();
    }

    private static String evidencia(Path carpeta, ResultadoCaso r) {
        String archivo = "capturas/" + r.id().toLowerCase() + ".png";
        String texto = "Prueba: `" + r.prueba() + "`";
        if (Files.exists(carpeta.resolve(archivo))) {
            texto += " · [captura](" + archivo + ")";
        }
        return celda(texto);
    }

    private static String celda(String texto) {
        return texto.replace("|", "\\|").replace("\n", " ");
    }
}