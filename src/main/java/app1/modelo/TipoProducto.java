package app1.modelo;

import java.math.BigDecimal;

/**
 * Catálogo de productos base. La clave es la que usa el registro de prototipos.
 */
public enum TipoProducto {
    CAFE("cafe", "Café", "2.20"),
    ENSALADA("ensalada", "Ensalada sencilla", "3.00"),
    PIZZA("pizza", "Pizza sencilla", "5.00");

    private final String clave;
    private final String nombre;
    private final BigDecimal precioBase;

    TipoProducto(String clave, String nombre, String precioBase) {
        this.clave = clave;
        this.nombre = nombre;
        this.precioBase = new BigDecimal(precioBase);
    }

    public String clave() {
        return clave;
    }

    public String nombre() {
        return nombre;
    }

    public BigDecimal precioBase() {
        return precioBase;
    }
}
