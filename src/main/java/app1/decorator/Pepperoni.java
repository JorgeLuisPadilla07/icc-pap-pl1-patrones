package app1.decorator;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;

import java.math.BigDecimal;

/** Decorador concreto: agrega Pepperoni al pedido envuelto. */
public class Pepperoni extends DecoradorIngrediente {

    public Pepperoni(Pedido envuelto) {
        super(envuelto, TipoIngrediente.PEPPERONI);
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
