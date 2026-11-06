package app1.decorator;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;
import app1.modelo.TipoProducto;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Decorador abstracto: es un {@link Pedido} y además contiene otro {@link Pedido}.
 * Delega lo que no cambia (id, tipo, lista de ingredientes) y deja a cada decorador
 * concreto la descripción y el precio.
 */
public abstract class DecoradorIngrediente implements Pedido {

    protected final Pedido envuelto;
    private final TipoIngrediente ingrediente;

    protected DecoradorIngrediente(Pedido envuelto, TipoIngrediente ingrediente) {
        this.envuelto = Objects.requireNonNull(envuelto, "El pedido a decorar es obligatorio");
        this.ingrediente = Objects.requireNonNull(ingrediente);
    }

    public TipoIngrediente ingrediente() {
        return ingrediente;
    }

    @Override
    public String id() {
        return envuelto.id();
    }

    @Override
    public TipoProducto tipo() {
        return envuelto.tipo();
    }

    @Override
    public List<TipoIngrediente> ingredientes() {
        List<TipoIngrediente> todos = new ArrayList<>(envuelto.ingredientes());
        todos.add(ingrediente);
        return List.copyOf(todos);
    }
}
