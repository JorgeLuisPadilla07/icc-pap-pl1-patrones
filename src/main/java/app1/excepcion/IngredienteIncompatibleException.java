package app1.excepcion;

import app1.modelo.TipoIngrediente;
import app1.modelo.TipoProducto;

public class IngredienteIncompatibleException extends RuntimeException {

    public IngredienteIncompatibleException(TipoIngrediente ingrediente, TipoProducto producto) {
        super(ingrediente.nombre() + " no es compatible con " + producto.nombre()
                + ". El pedido no cambió.");
    }
}
