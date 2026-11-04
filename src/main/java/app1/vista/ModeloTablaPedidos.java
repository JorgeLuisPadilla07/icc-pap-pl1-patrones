package app1.vista;

import app1.modelo.Dinero;
import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;

import javax.swing.table.AbstractTableModel;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Modelo de la tabla de pedidos. Solo presenta datos: no calcula precios ni valida nada.
 */
public class ModeloTablaPedidos extends AbstractTableModel {

    private static final String[] COLUMNAS = {"ID", "Producto", "Ingredientes", "Total"};

    private List<Pedido> pedidos = List.of();

    public void setPedidos(List<Pedido> nuevos) {
        this.pedidos = List.copyOf(nuevos);
        fireTableDataChanged();
    }

    public Pedido pedidoEn(int fila) {
        return pedidos.get(fila);
    }

    /** @return la fila del pedido con ese ID, o -1 si no está */
    public int filaDe(String id) {
        for (int i = 0; i < pedidos.size(); i++) {
            if (pedidos.get(i).id().equals(id)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getRowCount() {
        return pedidos.size();
    }

    @Override
    public int getColumnCount() {
        return COLUMNAS.length;
    }

    @Override
    public String getColumnName(int columna) {
        return COLUMNAS[columna];
    }

    @Override
    public Object getValueAt(int fila, int columna) {
        Pedido pedido = pedidos.get(fila);
        return switch (columna) {
            case 0 -> pedido.id();
            case 1 -> pedido.tipo().nombre();
            case 2 -> resumir(pedido.ingredientes());
            case 3 -> Dinero.formatear(pedido.precio());
            default -> "";
        };
    }

    /** "Azúcar + Crema", "Azúcar ×2" o "Sin ingredientes". */
    private static String resumir(List<TipoIngrediente> ingredientes) {
        if (ingredientes.isEmpty()) {
            return "Sin ingredientes";
        }
        Map<TipoIngrediente, Integer> conteo = new LinkedHashMap<>();
        for (TipoIngrediente ingrediente : ingredientes) {
            conteo.merge(ingrediente, 1, Integer::sum);
        }
        StringBuilder texto = new StringBuilder();
        conteo.forEach((ingrediente, veces) -> {
            if (!texto.isEmpty()) {
                texto.append(" + ");
            }
            texto.append(ingrediente.nombre());
            if (veces > 1) {
                texto.append(" ×").append(veces);
            }
        });
        return texto.toString();
    }
}