package app1.prototype;

/**
 * Contrato del patrón Prototype: un objeto sabe producir una copia independiente de sí mismo.
 *
 * @param <T> tipo del objeto copiado
 */
public interface Prototipo<T> {

    /**
     * @param nuevoId identificador que tendrá la copia
     * @return copia independiente: no comparte referencias mutables con el original
     */
    T copiar(String nuevoId);
}
