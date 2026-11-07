package app1.prototype;

import app1.modelo.TipoProducto;

/** Producto concreto del catálogo: Café. */
public final class Cafe extends ProductoBase {

    /** Crea el prototipo original (el que se guarda en el registro). */
    public Cafe() {
        super("BASE-CAFE", TipoProducto.CAFE, "Café", TipoProducto.CAFE.precioBase());
    }

    private Cafe(Cafe origen, String nuevoId) {
        super(origen, nuevoId);
    }

    @Override
    public ProductoBase copiar(String nuevoId) {
        return new Cafe(this, nuevoId);
    }
}
