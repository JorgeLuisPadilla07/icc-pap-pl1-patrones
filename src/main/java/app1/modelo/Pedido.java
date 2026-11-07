package app1.modelo;

import java.math.BigDecimal;
import java.util.List;

/**
 * Componente común del patrón Decorator: lo implementan tanto los productos base
 * como los decoradores de ingredientes.
 */
public interface Pedido {

    String id();

    TipoProducto tipo();

    String descripcion();

    BigDecimal precio();

    /** Ingredientes agregados, en orden de aplicación (puede haber repetidos). */
    List<TipoIngrediente> ingredientes();
}
