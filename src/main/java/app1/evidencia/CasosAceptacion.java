package app1.evidencia;

import app1.excepcion.IngredienteIncompatibleException;
import app1.excepcion.PedidoNoSeleccionadoException;
import app1.excepcion.PrototipoNoEncontradoException;
import app1.modelo.Dinero;
import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;
import app1.prototype.ProductoBase;
import app1.prototype.RegistroPrototipos;
import app1.servicio.ServicioPedidos;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Ejecuta CA01–CA11 contra el servicio real y devuelve lo que ocurrió.
 * Los importes salen de la ejecución, no están escritos a mano.
 */
public class CasosAceptacion {

    private final List<ResultadoCaso> resultados = new ArrayList<>();

    public List<ResultadoCaso> ejecutar() {
        resultados.clear();
        RegistroPrototipos registro = RegistroPrototipos.conCatalogoBase();
        ServicioPedidos servicio = new ServicioPedidos(registro);

        // CA01 · clonar Café dos veces
        Pedido cafe1 = servicio.crearPedido("cafe");
        Pedido cafe2 = servicio.crearPedido("cafe");
        anotar("CA01", "Clonar Café dos veces",
                "IDs diferentes; ambos empiezan en $2.20",
                cafe1.id() + " y " + cafe2.id() + "; base " + Dinero.formatear(cafe1.precio())
                        + " y " + Dinero.formatear(cafe2.precio()),
                !cafe1.id().equals(cafe2.id()) && igual(cafe1.precio(), "2.20") && igual(cafe2.precio(), "2.20"),
                "RegistroPrototiposTest.ca01_clonarCafeDosVecesDaIdsDistintosYPrecioBase");

        // CA02 · clon distinto del prototipo y prototipo intacto
        boolean esElRegistrado = registro.esPrototipoRegistrado(cafe1) || registro.esPrototipoRegistrado(cafe2);
        ProductoBase otraCopia = registro.clonar("cafe", "TEMPORAL");
        anotar("CA02", "Comparar clon y objeto del registro",
                "Referencias distintas; el prototipo conserva su configuración",
                "Clon es el registrado: " + esElRegistrado + "; nueva copia parte de "
                        + Dinero.formatear(otraCopia.precio()) + " y sin ingredientes",
                !esElRegistrado && igual(otraCopia.precio(), "2.20") && otraCopia.ingredientes().isEmpty(),
                "RegistroPrototiposTest.ca02_elClonNoEsLaInstanciaRegistradaYElPrototipoConservaSuConfiguracion");

        // CA03 · Azúcar y Crema al primer Café
        servicio.agregarIngrediente(cafe1.id(), TipoIngrediente.AZUCAR);
        servicio.agregarIngrediente(cafe1.id(), TipoIngrediente.CREMA);
        Pedido c1 = servicio.buscar(cafe1.id()).orElseThrow();
        anotar("CA03", "Al primer Café agregar Azúcar y Crema",
                "Descripción incluye los dos ingredientes; total $3.90",
                c1.descripcion() + " = " + Dinero.formatear(c1.precio()),
                igual(c1.precio(), "3.90") && c1.descripcion().contains("Azúcar") && c1.descripcion().contains("Crema"),
                "ServicioPedidosTest.ca03yCa04_personalizarUnCafeNoAfectaAlOtro");

        // CA04 · el segundo Café no cambió
        Pedido c2 = servicio.buscar(cafe2.id()).orElseThrow();
        anotar("CA04", "Consultar el segundo Café sin ingredientes",
                "Mantiene $2.20, sin ingredientes",
                c2.descripcion() + " = " + Dinero.formatear(c2.precio()) + "; ingredientes: " + c2.ingredientes().size(),
                igual(c2.precio(), "2.20") && c2.ingredientes().isEmpty(),
                "ServicioPedidosTest.ca03yCa04_personalizarUnCafeNoAfectaAlOtro");

        // CA05 · Ensalada con Tomate y Pollo
        Pedido ensalada = servicio.crearPedido("ensalada");
        servicio.agregarIngrediente(ensalada.id(), TipoIngrediente.TOMATE);
        servicio.agregarIngrediente(ensalada.id(), TipoIngrediente.POLLO);
        Pedido e = servicio.buscar(ensalada.id()).orElseThrow();
        anotar("CA05", "Clonar Ensalada y agregar Tomate y Pollo",
                "Total $5.60",
                e.descripcion() + " = " + Dinero.formatear(e.precio()),
                igual(e.precio(), "5.60"),
                "DecoradorTest.ca05_ensaladaConTomateYPolloSuma560");

        // CA06 · Pizza con Queso y Pepperoni
        Pedido pizza = servicio.crearPedido("pizza");
        servicio.agregarIngrediente(pizza.id(), TipoIngrediente.QUESO);
        servicio.agregarIngrediente(pizza.id(), TipoIngrediente.PEPPERONI);
        Pedido p = servicio.buscar(pizza.id()).orElseThrow();
        anotar("CA06", "Clonar Pizza y agregar Queso y Pepperoni",
                "Total $7.10",
                p.descripcion() + " = " + Dinero.formatear(p.precio()),
                igual(p.precio(), "7.10"),
                "DecoradorTest.ca06_pizzaConQuesoYPepperoniSuma710");

        // CA07 · Queso a Café → rechazado sin cambios
        String descripcionAntes = c2.descripcion();
        BigDecimal precioAntes = c2.precio();
        String mensaje07;
        try {
            servicio.agregarIngrediente(cafe2.id(), TipoIngrediente.QUESO);
            mensaje07 = "NO fue rechazado";
        } catch (IngredienteIncompatibleException ex) {
            mensaje07 = ex.getMessage();
        }
        Pedido c2Despues = servicio.buscar(cafe2.id()).orElseThrow();
        boolean sinCambios07 = c2Despues.descripcion().equals(descripcionAntes)
                && c2Despues.precio().compareTo(precioAntes) == 0;
        anotar("CA07", "Intentar agregar Queso a Café",
                "Operación rechazada; total y descripción no cambian",
                mensaje07 + " Pedido: " + c2Despues.descripcion() + " = " + Dinero.formatear(c2Despues.precio()),
                sinCambios07 && mensaje07.contains("no es compatible"),
                "ServicioPedidosTest.ca07_ingredienteIncompatibleSeRechazaSinCambiarElPedido");

        // CA08 · ingrediente sin pedido seleccionado
        int cantidadAntes = servicio.cantidad();
        BigDecimal totalAntes = servicio.totalGeneral();
        String mensaje08;
        try {
            servicio.agregarIngrediente(null, TipoIngrediente.AZUCAR);
            mensaje08 = "NO fue rechazado";
        } catch (PedidoNoSeleccionadoException ex) {
            mensaje08 = ex.getMessage();
        }
        anotar("CA08", "Agregar ingrediente sin pedido seleccionado",
                "Mensaje claro y ningún cambio de estado",
                mensaje08 + " Pedidos: " + servicio.cantidad() + ", total " + Dinero.formatear(servicio.totalGeneral()),
                servicio.cantidad() == cantidadAntes && servicio.totalGeneral().compareTo(totalAntes) == 0
                        && mensaje08.startsWith("Selecciona"),
                "ServicioPedidosTest.ca08_sinPedidoSeleccionadoSeRechazaSinCambiarElEstado");

        // CA09 · clave inexistente
        String mensaje09;
        try {
            servicio.crearPedido("te");
            mensaje09 = "NO fue rechazado";
        } catch (PrototipoNoEncontradoException ex) {
            mensaje09 = ex.getMessage();
        }
        anotar("CA09", "Solicitar clon de clave inexistente («te»)",
                "Fallo controlado; no se crea pedido",
                mensaje09 + " Pedidos: " + servicio.cantidad(),
                servicio.cantidad() == cantidadAntes && mensaje09.contains("«te»"),
                "ServicioPedidosTest.ca09_claveInexistenteNoCreaPedidoNiConsumeId");

        // CA10 · Azúcar dos veces
        Pedido cafe3 = servicio.crearPedido("cafe");
        servicio.agregarIngrediente(cafe3.id(), TipoIngrediente.AZUCAR);
        servicio.agregarIngrediente(cafe3.id(), TipoIngrediente.AZUCAR);
        Pedido c3 = servicio.buscar(cafe3.id()).orElseThrow();
        anotar("CA10", "Agregar dos veces Azúcar a un Café",
                "Total $3.20 y dos aplicaciones del decorador",
                c3.descripcion() + " = " + Dinero.formatear(c3.precio()) + "; aplicaciones: " + c3.ingredientes().size(),
                igual(c3.precio(), "3.20") && c3.ingredientes().size() == 2,
                "DecoradorTest.ca10_azucarDosVecesSuma320YAplicaElDecoradorDosVeces");

        // CA11 · reiniciar y volver a clonar
        servicio.reiniciar();
        Pedido nuevo = servicio.crearPedido("cafe");
        anotar("CA11", "Reiniciar y volver a clonar Café",
                "Nuevos pedidos parten nuevamente del valor base $2.20",
                "Tras reiniciar: " + nuevo.id() + " = " + Dinero.formatear(nuevo.precio())
                        + "; pedidos: " + servicio.cantidad(),
                igual(nuevo.precio(), "2.20") && servicio.cantidad() == 1,
                "ServicioPedidosTest.ca11_reiniciarLimpiaPedidosYNuevosClonesParten2_20");

        return List.copyOf(resultados);
    }

    private void anotar(String id, String entrada, String esperado, String real, boolean cumple, String prueba) {
        resultados.add(new ResultadoCaso(id, entrada, esperado, real, cumple, prueba));
    }

    private static boolean igual(BigDecimal valor, String esperado) {
        return valor.compareTo(new BigDecimal(esperado)) == 0;
    }
}