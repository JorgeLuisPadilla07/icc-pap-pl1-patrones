package app1.excepcion;

public class PedidoNoSeleccionadoException extends RuntimeException {

    public PedidoNoSeleccionadoException() {
        super("Selecciona un pedido antes de agregar ingredientes.");
    }
}
