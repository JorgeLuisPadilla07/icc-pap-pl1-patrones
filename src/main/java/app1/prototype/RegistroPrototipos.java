package app1.prototype;

import app1.excepcion.PrototipoNoEncontradoException;
import app1.modelo.Pedido;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Registro de prototipos: guarda un producto base por clave y entrega siempre
 * una copia nueva, nunca la instancia registrada.
 */
public class RegistroPrototipos {

    private final Map<String, ProductoBase> prototipos = new LinkedHashMap<>();

    /** Registro con los tres productos del catálogo obligatorio. */
    public static RegistroPrototipos conCatalogoBase() {
        RegistroPrototipos registro = new RegistroPrototipos();
        registro.registrar(new Cafe());
        registro.registrar(new Ensalada());
        registro.registrar(new Pizza());
        return registro;
    }

    public void registrar(ProductoBase prototipo) {
        Objects.requireNonNull(prototipo);
        prototipos.put(prototipo.tipo().clave(), prototipo);
    }

    /**
     * @throws PrototipoNoEncontradoException si la clave no está registrada
     */
    public ProductoBase clonar(String clave, String nuevoId) {
        ProductoBase prototipo = prototipos.get(clave);
        if (prototipo == null) {
            throw new PrototipoNoEncontradoException(clave);
        }
        return prototipo.copiar(nuevoId);
    }

    public boolean contiene(String clave) {
        return prototipos.containsKey(clave);
    }

    public Set<String> claves() {
        return Collections.unmodifiableSet(prototipos.keySet());
    }

    /** Indica si el pedido recibido es literalmente uno de los prototipos guardados. */
    public boolean esPrototipoRegistrado(Pedido candidato) {
        return prototipos.values().stream().anyMatch(p -> p == candidato);
    }

    /** Solo para pruebas del mismo paquete. */
    ProductoBase instanciaRegistrada(String clave) {
        return prototipos.get(clave);
    }
}
