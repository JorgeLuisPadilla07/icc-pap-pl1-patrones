import app1.vista.VentanaPedidos;
import javax.swing.SwingUtilities;

public static void main(String[] args) {
    if (args.length > 0 && args[0].equals("--consola")) {
        demoConsola();
        return;
    }
    SwingUtilities.invokeLater(() -> {
        ServicioPedidos servicio = new ServicioPedidos(RegistroPrototipos.conCatalogoBase());
        new VentanaPedidos(servicio).setVisible(true);
    });
}

private static void demoConsola() {   // antes era: public static void main(String[] args)
    // ... el cuerpo que ya tenías, sin cambios
}


