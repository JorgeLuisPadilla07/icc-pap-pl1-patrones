package app1.decorator;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;

import java.math.BigDecimal;

/** Decorador concreto: agrega Tomate al pedido envuelto. */
public class Tomate extends DecoradorIngrediente {

    public Tomate(Pedido envuelto) {
        super(envuelto, TipoIngrediente.TOMATE);
    }

    @Override
    public String descripcion() {
        return envuelto.descripcion() + " + " + ingrediente().nombre();
    }

    @Override
    public BigDecimal precio() {
        return envuelto.precio().add(ingrediente().precio());
    }
}
