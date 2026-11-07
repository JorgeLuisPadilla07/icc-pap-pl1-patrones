package app1.prototype;

import app1.modelo.TipoProducto;

/** Producto concreto del catálogo: Ensalada. */
public final class Ensalada extends ProductoBase {

    /** Crea el prototipo original (el que se guarda en el registro). */
    public Ensalada() {
        super("BASE-ENSALADA", TipoProducto.ENSALADA, "Ensalada", TipoProducto.ENSALADA.precioBase());
    }

    private Ensalada(Ensalada origen, String nuevoId) {
        super(origen, nuevoId);
    }

    @Override
    public ProductoBase copiar(String nuevoId) {
        return new Ensalada(this, nuevoId);
    }
}
