package app1.decorator;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;

import java.math.BigDecimal;

/** Decorador concreto: agrega Pollo al pedido envuelto. */
public class Pollo extends DecoradorIngrediente {

    public Pollo(Pedido envuelto) {
        super(envuelto, TipoIngrediente.POLLO);
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
