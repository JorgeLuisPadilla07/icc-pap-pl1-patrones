package app1.decorator;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;

import java.math.BigDecimal;

/** Decorador concreto: agrega Queso al pedido envuelto. */
public class Queso extends DecoradorIngrediente {

    public Queso(Pedido envuelto) {
        super(envuelto, TipoIngrediente.QUESO);
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
