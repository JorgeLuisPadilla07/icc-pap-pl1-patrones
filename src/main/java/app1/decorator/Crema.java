package app1.decorator;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;

import java.math.BigDecimal;

/** Decorador concreto: agrega Crema al pedido envuelto. */
public class Crema extends DecoradorIngrediente {

    public Crema(Pedido envuelto) {
        super(envuelto, TipoIngrediente.CREMA);
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
