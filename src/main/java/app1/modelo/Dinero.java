package app1.modelo;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Formato de importes: dólares con dos decimales.
 */
public final class Dinero {

    private Dinero() {
    }

    public static String formatear(BigDecimal monto) {
        return String.format(Locale.US, "$%.2f", monto);
    }
}
