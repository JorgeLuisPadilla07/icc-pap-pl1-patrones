package app1.servicio;

import app1.decorator.Azucar;
import app1.decorator.Crema;
import app1.decorator.Pepperoni;
import app1.decorator.Pollo;
import app1.decorator.Queso;
import app1.decorator.Tomate;
import app1.excepcion.IngredienteIncompatibleException;
import app1.excepcion.PedidoNoSeleccionadoException;
import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;
import app1.prototype.RegistroPrototipos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.UnaryOperator;

/**
 * Reglas de negocio: administra la lista de pedidos, genera los ID, valida la
 * compatibilidad de ingredientes y reemplaza un pedido por su versión decorada.
 * No conoce nada de Swing.
 */
public class ServicioPedidos {

    private final RegistroPrototipos registro;
    private final List<Pedido> pedidos = new ArrayList<>();
    private final Map<TipoIngrediente, UnaryOperator<Pedido>> decoradores = new EnumMap<>(TipoIngrediente.class);
    private int contadorId = 0;

    public ServicioPedidos(RegistroPrototipos registro) {
        this.registro = Objects.requireNonNull(registro);
        decoradores.put(TipoIngrediente.AZUCAR, Azucar::new);
        decoradores.put(TipoIngrediente.CREMA, Crema::new);
        decoradores.put(TipoIngrediente.TOMATE, Tomate::new);
        decoradores.put(TipoIngrediente.POLLO, Pollo::new);
        decoradores.put(TipoIngrediente.QUESO, Queso::new);
        decoradores.put(TipoIngrediente.PEPPERONI, Pepperoni::new);
    }

    /**
     * Crea un pedido clonando el prototipo registrado con esa clave.
     * Si la clave no existe, no se crea nada y el contador de ID no avanza.
     */
    public Pedido crearPedido(String clave) {
        String id = formatearId(contadorId + 1);
        Pedido nuevo = registro.clonar(clave, id);   // puede lanzar PrototipoNoEncontradoException
        contadorId++;
        pedidos.add(nuevo);
        return nuevo;
    }

    /**
     * Envuelve el pedido indicado con el decorador del ingrediente.
     * Las validaciones ocurren antes de crear el decorador, así un rechazo no altera nada.
     *
     * @param idPedido ID del pedido seleccionado (null o vacío si no hay selección)
     */
    public Pedido agregarIngrediente(String idPedido, TipoIngrediente ingrediente) {
        if (idPedido == null || idPedido.isBlank()) {
            throw new PedidoNoSeleccionadoException();
        }
        Objects.requireNonNull(ingrediente, "El ingrediente es obligatorio");

        int posicion = posicionDe(idPedido);
        Pedido actual = pedidos.get(posicion);

        if (!ingrediente.esCompatibleCon(actual.tipo())) {
            throw new IngredienteIncompatibleException(ingrediente, actual.tipo());
        }

        Pedido decorado = decoradores.get(ingrediente).apply(actual);
        pedidos.set(posicion, decorado);
        return decorado;
    }

    public List<Pedido> listar() {
        return List.copyOf(pedidos);
    }

    public Optional<Pedido> buscar(String idPedido) {
        return pedidos.stream().filter(p -> p.id().equals(idPedido)).findFirst();
    }

    public int cantidad() {
        return pedidos.size();
    }

    public BigDecimal totalGeneral() {
        return pedidos.stream().map(Pedido::precio).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /** Limpia los pedidos creados. No toca los prototipos del registro. */
    public void reiniciar() {
        pedidos.clear();
        contadorId = 0;
    }

    private int posicionDe(String idPedido) {
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).id().equals(idPedido)) {
                return i;
            }
        }
        throw new NoSuchElementException("No existe el pedido " + idPedido);
    }

    private static String formatearId(int numero) {
        return String.format("P-%03d", numero);
    }
}
