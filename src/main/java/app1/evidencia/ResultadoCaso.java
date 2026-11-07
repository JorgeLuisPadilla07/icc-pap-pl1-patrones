package app1.evidencia;

/**
 * Resultado de ejecutar un caso de aceptación.
 *
 * @param id       CA01, CA02...
 * @param entrada  operación realizada
 * @param esperado resultado que pide la guía
 * @param real     lo que ocurrió en la ejecución
 * @param cumple   true si lo real coincide con lo esperado
 * @param prueba   prueba unitaria que cubre el mismo caso
 */
public record ResultadoCaso(String id, String entrada, String esperado, String real,
                            boolean cumple, String prueba) {

    public String estado() {
        return cumple ? "Cumple" : "Falla";
    }
}