package app1.decorator;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;

import java.math.BigDecimal;

/** Decorador concreto: agrega Azúcar al pedido envuelto. */
public class Azucar extends DecoradorIngrediente {

    public Azucar(Pedido envuelto) {
        super(envuelto, TipoIngrediente.AZUCAR);
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