package app1.excepcion;

public class PrototipoNoEncontradoException extends RuntimeException {

    public PrototipoNoEncontradoException(String clave) {
        super("No existe un prototipo registrado con la clave «" + clave + "». No se creó ningún pedido.");
    }
}