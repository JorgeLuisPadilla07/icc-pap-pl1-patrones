package app1.modelo;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.Set;

/**
 * Ingredientes disponibles, con su precio y los productos con los que son compatibles.
 * Es el único lugar donde se define el precio de un ingrediente.
 */
public enum TipoIngrediente {
    AZUCAR("Azúcar", "0.50", EnumSet.of(TipoProducto.CAFE)),
    CREMA("Crema", "1.20", EnumSet.of(TipoProducto.CAFE)),
    TOMATE("Tomate", "1.10", EnumSet.of(TipoProducto.ENSALADA)),
    POLLO("Pollo", "1.50", EnumSet.of(TipoProducto.ENSALADA)),
    QUESO("Queso", "1.00", EnumSet.of(TipoProducto.PIZZA)),
    PEPPERONI("Pepperoni", "1.10", EnumSet.of(TipoProducto.PIZZA));

    private final String nombre;
    private final BigDecimal precio;
    private final Set<TipoProducto> compatibles;

    TipoIngrediente(String nombre, String precio, Set<TipoProducto> compatibles) {
        this.nombre = nombre;
        this.precio = new BigDecimal(precio);
        this.compatibles = compatibles;
    }

    public String nombre() {
        return nombre;
    }

    public BigDecimal precio() {
        return precio;
    }

    public boolean esCompatibleCon(TipoProducto tipo) {
        return compatibles.contains(tipo);
    }
}
