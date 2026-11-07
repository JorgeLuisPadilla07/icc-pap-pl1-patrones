package app1.prototype;

import app1.modelo.TipoProducto;

/** Producto concreto del catálogo: Pizza. */
public final class Pizza extends ProductoBase {

    /** Crea el prototipo original (el que se guarda en el registro). */
    public Pizza() {
        super("BASE-PIZZA", TipoProducto.PIZZA, "Pizza", TipoProducto.PIZZA.precioBase());
    }

    private Pizza(Pizza origen, String nuevoId) {
        super(origen, nuevoId);
    }

    @Override
    public ProductoBase copiar(String nuevoId) {
        return new Pizza(this, nuevoId);
    }
}
