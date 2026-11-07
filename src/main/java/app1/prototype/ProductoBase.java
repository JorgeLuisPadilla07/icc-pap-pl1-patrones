package app1.prototype;

import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;
import app1.modelo.TipoProducto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Producto base del catálogo. Es a la vez:
 * <ul>
 *   <li>un {@link Pedido} (componente concreto del Decorator), y</li>
 *   <li>un {@link Prototipo} (lo que se registra y se clona).</li>
 * </ul>
 * Conserva ID, tipo, descripción y precio base, más una lista mutable de notas
 * que sirve para comprobar que la copia es realmente independiente.
 */
public abstract class ProductoBase implements Pedido, Prototipo<ProductoBase> {

    private final String id;
    private final TipoProducto tipo;
    private final String descripcionBase;
    private final BigDecimal precioBase;
    private final List<String> notas;

    /** Constructor para crear el prototipo original. */
    protected ProductoBase(String id, TipoProducto tipo, String descripcionBase, BigDecimal precioBase) {
        this.id = Objects.requireNonNull(id);
        this.tipo = Objects.requireNonNull(tipo);
        this.descripcionBase = Objects.requireNonNull(descripcionBase);
        this.precioBase = Objects.requireNonNull(precioBase);
        this.notas = new ArrayList<>();
    }

    /** Constructor de copia: base de la clonación en las subclases. */
    protected ProductoBase(ProductoBase origen, String nuevoId) {
        this.id = Objects.requireNonNull(nuevoId, "El nuevo ID es obligatorio");
        this.tipo = origen.tipo;
        this.descripcionBase = origen.descripcionBase;
        this.precioBase = origen.precioBase;           // BigDecimal es inmutable
        this.notas = new ArrayList<>(origen.notas);    // copia profunda de la lista mutable
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public TipoProducto tipo() {
        return tipo;
    }

    @Override
    public String descripcion() {
        return descripcionBase;
    }

    @Override
    public BigDecimal precio() {
        return precioBase;
    }

    @Override
    public List<TipoIngrediente> ingredientes() {
        return List.of();
    }

    public void agregarNota(String nota) {
        notas.add(Objects.requireNonNull(nota));
    }

    public List<String> notas() {
        return Collections.unmodifiableList(notas);
    }
}
