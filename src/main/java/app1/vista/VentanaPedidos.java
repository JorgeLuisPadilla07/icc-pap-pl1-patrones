package app1.vista;

import app1.excepcion.IngredienteIncompatibleException;
import app1.excepcion.PedidoNoSeleccionadoException;
import app1.excepcion.PrototipoNoEncontradoException;
import app1.modelo.Dinero;
import app1.modelo.Pedido;
import app1.modelo.TipoIngrediente;
import app1.modelo.TipoProducto;
import app1.servicio.ServicioPedidos;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

/**
 * Única ventana de la aplicación. Captura eventos y actualiza componentes;
 * toda la lógica (clonar, decorar, validar, sumar) vive en {@link ServicioPedidos}.
 */
public class VentanaPedidos extends JFrame {

    // ---- Paleta: un acento azul para acciones; rojo/verde/ámbar solo para mensajes ----
    private static final Color FONDO = new Color(0xF5F4F4);
    private static final Color TARJETA = Color.WHITE;
    private static final Color BORDE = new Color(0xD9D9DE);
    private static final Color TEXTO = new Color(0x1F2328);
    private static final Color TEXTO_SUAVE = new Color(0x5F6368);
    private static final Color ENCABEZADO = new Color(0xE8E8E8);
    private static final Color SELECCION = new Color(0xE6EDFB);
    private static final Color AZUL = new Color(0x1D4ED8);
    private static final Color AZUL_OSCURO = new Color(0x1E3A8A);
    private static final Color AZUL_CLARO = new Color(0xEEF3FF);
    private static final Color GRIS_BOTON = new Color(0xEEEEEF);
    private static final Color GRIS_BOTON_HOVER = new Color(0xE0E0E3);

    private static final Font F_NORMAL = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    private static final Font F_NEGRITA = new Font(Font.SANS_SERIF, Font.BOLD, 14);
    private static final Font F_TITULO = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    private static final Font F_PEDIDO = new Font(Font.SANS_SERIF, Font.BOLD, 20);
    private static final Font F_TOTAL = new Font(Font.SANS_SERIF, Font.BOLD, 28);

    /** Estados de la barra de mensajes. */
    private enum Mensaje {
        INFO(new Color(0xF4F4F4), TEXTO_SUAVE, new Color(0xD9D9DE)),
        EXITO(new Color(0xE8F5E9), new Color(0x1B5E20), new Color(0x2E7D32)),
        ERROR(new Color(0xFDECEA), new Color(0xB3261E), new Color(0xB3261E)),
        AVISO(new Color(0xFFF4D6), new Color(0x7A5200), new Color(0x8A6100));

        final Color fondo;
        final Color texto;
        final Color borde;

        Mensaje(Color fondo, Color texto, Color borde) {
            this.fondo = fondo;
            this.texto = texto;
            this.borde = borde;
        }
    }

    private static final String CARTA_VACIO = "vacio";
    private static final String CARTA_DETALLE = "detalle";

    private final ServicioPedidos servicio;
    private final ModeloTablaPedidos modeloTabla = new ModeloTablaPedidos();

    private JTable tabla;
    private JLabel lblResumen;
    private JPanel panelDetalleCartas;
    private JLabel lblNombrePedido;
    private JLabel lblIdPedido;
    private JPanel panelLineas;
    private JLabel lblTotalPedido;
    private JComboBox<TipoIngrediente> comboIngredientes;
    private JPanel barraMensajes;
    private JLabel lblMensaje;

    public VentanaPedidos(ServicioPedidos servicio) {
        super("PedidoLab");
        this.servicio = servicio;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearContenido(), BorderLayout.CENTER);
        add(crearBarraMensajes(), BorderLayout.SOUTH);

        setMinimumSize(new Dimension(900, 560));
        setSize(1100, 680);
        setLocationRelativeTo(null);

        refrescar(null);
        mostrarMensaje("Elige un producto para crear un pedido.", Mensaje.INFO);
    }

    // =====================================================================
    // Construcción de la interfaz
    // =====================================================================

    private JPanel crearEncabezado() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TARJETA);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, BORDE),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));
        JLabel titulo = new JLabel("PedidoLab");
        titulo.setFont(F_TITULO);
        titulo.setForeground(TEXTO);
        panel.add(titulo, BorderLayout.WEST);
        return panel;
    }

    private JPanel crearContenido() {
        JPanel contenido = new JPanel(new GridBagLayout());
        contenido.setBackground(FONDO);
        contenido.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1;

        // Tamaño preferido 0: todo el ancho se reparte por peso y la proporción 62/38 no cambia con el contenido
        JPanel izquierda = crearPanelPedidos();
        izquierda.setPreferredSize(new Dimension(0, 0));
        c.gridx = 0;
        c.weightx = 0.62;
        c.insets = new Insets(0, 0, 0, 16);
        contenido.add(izquierda, c);

        JPanel derecha = crearPanelDetalle();
        derecha.setPreferredSize(new Dimension(0, 0));
        c.gridx = 1;
        c.weightx = 0.38;
        c.insets = new Insets(0, 0, 0, 0);
        contenido.add(derecha, c);
        return contenido;
    }

    private JPanel crearPanelPedidos() {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(TARJETA);
        tarjeta.setBorder(BorderFactory.createLineBorder(BORDE));

        // Botones de producto: cada clic crea un pedido nuevo (clonación)
        JPanel productos = new JPanel(new GridLayout(1, TipoProducto.values().length, 12, 0));
        productos.setOpaque(false);
        productos.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        for (TipoProducto tipo : TipoProducto.values()) {
            String nombreCorto = tipo.nombre().split(" ")[0];   // "Ensalada sencilla" -> "Ensalada"
            BotonPlano boton = new BotonPlano(
                    "+ " + nombreCorto + " " + Dinero.formatear(tipo.precioBase()),
                    TARJETA, AZUL_CLARO, AZUL, AZUL);
            boton.setPreferredSize(new Dimension(0, 44));
            boton.addActionListener(e -> alCrearPedido(tipo));
            productos.add(boton);
        }
        tarjeta.add(productos, BorderLayout.NORTH);

        tarjeta.add(crearTabla(), BorderLayout.CENTER);

        // Pie: reiniciar (discreto) y resumen
        JPanel pie = new JPanel(new BorderLayout());
        pie.setOpaque(false);
        pie.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        BotonPlano reiniciar = new BotonPlano("Reiniciar pedidos", GRIS_BOTON, GRIS_BOTON_HOVER, TEXTO, BORDE);
        reiniciar.setPreferredSize(new Dimension(160, 38));
        reiniciar.addActionListener(e -> alReiniciar());
        lblResumen = new JLabel();
        lblResumen.setFont(F_NORMAL);
        lblResumen.setForeground(TEXTO_SUAVE);
        pie.add(reiniciar, BorderLayout.WEST);
        pie.add(lblResumen, BorderLayout.EAST);
        tarjeta.add(pie, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JScrollPane crearTabla() {
        tabla = new JTable(modeloTabla) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {   // estado vacío
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                            RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                    g2.setColor(TEXTO_SUAVE);
                    g2.setFont(F_NORMAL);
                    g2.drawString("Sin pedidos creados.", 16, 30);
                    g2.dispose();
                }
            }
        };
        tabla.setFont(F_NORMAL);
        tabla.setForeground(TEXTO);
        tabla.setRowHeight(38);
        tabla.setShowGrid(false);
        tabla.setShowHorizontalLines(true);
        tabla.setGridColor(BORDE);
        tabla.setIntercellSpacing(new Dimension(0, 1));
        tabla.setFillsViewportHeight(true);
        tabla.setBackground(TARJETA);
        tabla.setSelectionBackground(SELECCION);
        tabla.setSelectionForeground(TEXTO);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        tabla.getColumnModel().getColumn(0).setPreferredWidth(70);
        tabla.getColumnModel().getColumn(1).setPreferredWidth(140);
        tabla.getColumnModel().getColumn(2).setPreferredWidth(240);
        tabla.getColumnModel().getColumn(3).setPreferredWidth(90);

        tabla.getColumnModel().getColumn(0).setCellRenderer(crearRenderizadorCelda(SwingConstants.LEFT));
        tabla.getColumnModel().getColumn(1).setCellRenderer(crearRenderizadorCelda(SwingConstants.LEFT));
        tabla.getColumnModel().getColumn(2).setCellRenderer(crearRenderizadorCelda(SwingConstants.LEFT));
        tabla.getColumnModel().getColumn(3).setCellRenderer(crearRenderizadorCelda(SwingConstants.RIGHT));

        JTableHeader encabezado = tabla.getTableHeader();
        encabezado.setReorderingAllowed(false);
        encabezado.setResizingAllowed(false);
        encabezado.setBackground(ENCABEZADO);
        encabezado.setPreferredSize(new Dimension(0, 40));
        encabezado.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                                                           boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, false, false, fila, columna);
                setBackground(ENCABEZADO);
                setForeground(TEXTO_SUAVE);
                setFont(F_NEGRITA);
                setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
                setHorizontalAlignment(columna == 3 ? SwingConstants.RIGHT : SwingConstants.LEFT);
                return this;
            }
        });

        tabla.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                mostrarDetalle();
            }
        });

        JScrollPane scroll = new JScrollPane(tabla);
        scroll.setColumnHeaderView(tabla.getTableHeader());
        scroll.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, BORDE));
        scroll.getViewport().setBackground(TARJETA);
        return scroll;
    }

    private DefaultTableCellRenderer crearRenderizadorCelda(int alineacion) {
        DefaultTableCellRenderer renderizador = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object valor, boolean sel,
                                                           boolean foco, int fila, int columna) {
                super.getTableCellRendererComponent(t, valor, sel, false, fila, columna);
                setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));   // el padre lo reinicia en cada celda
                return this;
            }
        };
        renderizador.setHorizontalAlignment(alineacion);
        return renderizador;
    }

    private JPanel crearPanelDetalle() {
        JPanel tarjeta = new JPanel(new BorderLayout(0, 16));
        tarjeta.setBackground(TARJETA);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDE),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        // Centro: mensaje vacío o detalle del pedido seleccionado
        panelDetalleCartas = new JPanel(new CardLayout());
        panelDetalleCartas.setOpaque(false);

        JPanel vacio = new JPanel(new BorderLayout());
        vacio.setOpaque(false);
        JLabel textoVacio = new JLabel("Crea o selecciona un pedido");
        textoVacio.setFont(F_NORMAL);
        textoVacio.setForeground(TEXTO_SUAVE);
        vacio.add(textoVacio, BorderLayout.NORTH);
        panelDetalleCartas.add(vacio, CARTA_VACIO);

        JPanel detalle = new JPanel(new BorderLayout());
        detalle.setOpaque(false);
        detalle.add(crearContenidoDetalle(), BorderLayout.NORTH);
        panelDetalleCartas.add(detalle, CARTA_DETALLE);

        tarjeta.add(panelDetalleCartas, BorderLayout.CENTER);
        tarjeta.add(crearSelectorIngredientes(), BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearContenidoDetalle() {
        JPanel contenido = new JPanel();
        contenido.setOpaque(false);
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));

        JPanel titulo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        titulo.setOpaque(false);
        titulo.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNombrePedido = new JLabel();
        lblNombrePedido.setFont(F_PEDIDO);
        lblNombrePedido.setForeground(TEXTO);
        lblIdPedido = new JLabel();
        lblIdPedido.setFont(F_NEGRITA);
        lblIdPedido.setForeground(AZUL);
        lblIdPedido.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        titulo.add(lblNombrePedido);
        titulo.add(lblIdPedido);
        contenido.add(titulo);
        contenido.add(Box.createVerticalStrut(14));

        JLabel caption = new JLabel("Desglose");
        caption.setFont(F_NORMAL);
        caption.setForeground(TEXTO_SUAVE);
        caption.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(caption);
        contenido.add(Box.createVerticalStrut(6));

        panelLineas = new JPanel();
        panelLineas.setOpaque(false);
        panelLineas.setLayout(new BoxLayout(panelLineas, BoxLayout.Y_AXIS));
        panelLineas.setAlignmentX(Component.LEFT_ALIGNMENT);
        contenido.add(panelLineas);

        contenido.add(Box.createVerticalStrut(8));
        JSeparator separador = new JSeparator();
        separador.setForeground(BORDE);
        separador.setAlignmentX(Component.LEFT_ALIGNMENT);
        separador.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        contenido.add(separador);
        contenido.add(Box.createVerticalStrut(8));

        JPanel total = new JPanel(new BorderLayout());
        total.setOpaque(false);
        total.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel etiqueta = new JLabel("Total del pedido");
        etiqueta.setFont(F_NEGRITA);
        etiqueta.setForeground(TEXTO);
        lblTotalPedido = new JLabel();
        lblTotalPedido.setFont(F_TOTAL);
        lblTotalPedido.setForeground(TEXTO);
        total.add(etiqueta, BorderLayout.WEST);
        total.add(lblTotalPedido, BorderLayout.EAST);
        total.setMaximumSize(new Dimension(Integer.MAX_VALUE, total.getPreferredSize().height));
        contenido.add(total);
        return contenido;
    }

    private JPanel crearSelectorIngredientes() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.weightx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.WEST;

        JLabel etiqueta = new JLabel("Ingredientes");
        etiqueta.setFont(F_NORMAL);
        etiqueta.setForeground(TEXTO_SUAVE);
        c.gridy = 0;
        c.insets = new Insets(0, 0, 6, 0);
        panel.add(etiqueta, c);

        comboIngredientes = new JComboBox<>(TipoIngrediente.values());
        comboIngredientes.setFont(F_NORMAL);
        comboIngredientes.setBackground(TARJETA);
        comboIngredientes.setPreferredSize(new Dimension(0, 40));
        comboIngredientes.setSelectedIndex(-1);
        comboIngredientes.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor, int indice,
                                                          boolean seleccionado, boolean foco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, foco);
                if (valor instanceof TipoIngrediente ingrediente) {
                    setText(ingrediente.nombre() + "   " + Dinero.formatear(ingrediente.precio()));
                } else {
                    setText("Selecciona un ingrediente");
                    setForeground(TEXTO_SUAVE);
                }
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
        c.gridy = 1;
        c.insets = new Insets(0, 0, 10, 0);
        panel.add(comboIngredientes, c);

        BotonPlano agregar = new BotonPlano("Agregar ingrediente", AZUL, AZUL_OSCURO, Color.WHITE, AZUL);
        agregar.setPreferredSize(new Dimension(0, 44));
        agregar.addActionListener(e -> alAgregarIngrediente());
        c.gridy = 2;
        c.insets = new Insets(0, 0, 0, 0);
        panel.add(agregar, c);
        return panel;
    }

    private JPanel crearBarraMensajes() {
        barraMensajes = new JPanel(new BorderLayout());
        lblMensaje = new JLabel(" ");
        lblMensaje.setFont(F_NORMAL);
        barraMensajes.add(lblMensaje, BorderLayout.CENTER);
        return barraMensajes;
    }

    // =====================================================================
    // Eventos: solo llaman al servicio y actualizan la vista
    // =====================================================================

    private void alCrearPedido(TipoProducto tipo) {
        try {
            Pedido nuevo = servicio.crearPedido(tipo.clave());
            refrescar(nuevo.id());
            mostrarMensaje(nuevo.id() + " creado: " + tipo.nombre() + " a "
                    + Dinero.formatear(nuevo.precio()) + ".", Mensaje.EXITO);
        } catch (PrototipoNoEncontradoException ex) {
            mostrarMensaje(ex.getMessage(), Mensaje.ERROR);
        }
    }

    private void alAgregarIngrediente() {
        String idSeleccionado = idSeleccionado();
        TipoIngrediente ingrediente = (TipoIngrediente) comboIngredientes.getSelectedItem();
        try {
            if (idSeleccionado != null && ingrediente == null) {
                mostrarMensaje("Selecciona un ingrediente.", Mensaje.AVISO);
                return;
            }
            Pedido decorado = servicio.agregarIngrediente(idSeleccionado, ingrediente);
            refrescar(decorado.id());
            mostrarMensaje(ingrediente.nombre() + " agregado a " + decorado.id() + ".", Mensaje.EXITO);
        } catch (PedidoNoSeleccionadoException ex) {
            mostrarMensaje(ex.getMessage(), Mensaje.AVISO);
        } catch (IngredienteIncompatibleException ex) {
            mostrarMensaje(ex.getMessage(), Mensaje.ERROR);
        }
    }

    private void alReiniciar() {
        servicio.reiniciar();
        refrescar(null);
        mostrarMensaje("Pedidos reiniciados. Los prototipos no se modificaron.", Mensaje.INFO);
    }

    // =====================================================================
    // Actualización de la vista
    // =====================================================================

    /** Recarga la tabla y deja seleccionado el pedido indicado (o ninguno si es null). */
    private void refrescar(String idASeleccionar) {
        modeloTabla.setPedidos(servicio.listar());

        int cantidad = servicio.cantidad();
        lblResumen.setText(cantidad + (cantidad == 1 ? " pedido" : " pedidos")
                + " · Total general " + Dinero.formatear(servicio.totalGeneral()));

        int fila = idASeleccionar == null ? -1 : modeloTabla.filaDe(idASeleccionar);
        if (fila >= 0) {
            tabla.setRowSelectionInterval(fila, fila);
        } else {
            tabla.clearSelection();
        }
        mostrarDetalle();
    }

    private String idSeleccionado() {
        int fila = tabla.getSelectedRow();
        return fila < 0 ? null : modeloTabla.pedidoEn(fila).id();
    }

    private void mostrarDetalle() {
        CardLayout cartas = (CardLayout) panelDetalleCartas.getLayout();
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            cartas.show(panelDetalleCartas, CARTA_VACIO);
            return;
        }

        Pedido pedido = modeloTabla.pedidoEn(fila);
        lblNombrePedido.setText(pedido.tipo().nombre());
        lblIdPedido.setText(pedido.id());

        panelLineas.removeAll();
        panelLineas.add(crearLinea(pedido.tipo().nombre() + " base",
                Dinero.formatear(pedido.tipo().precioBase())));
        List<TipoIngrediente> ingredientes = pedido.ingredientes();
        for (TipoIngrediente ingrediente : ingredientes) {
            panelLineas.add(crearLinea("+ " + ingrediente.nombre(), Dinero.formatear(ingrediente.precio())));
        }
        panelLineas.revalidate();
        panelLineas.repaint();

        BigDecimal total = pedido.precio();   // viene de la composición de decoradores
        lblTotalPedido.setText(Dinero.formatear(total));
        cartas.show(panelDetalleCartas, CARTA_DETALLE);
    }

    private JPanel crearLinea(String izquierda, String derecha) {
        JPanel linea = new JPanel(new BorderLayout());
        linea.setOpaque(false);
        linea.setBorder(BorderFactory.createEmptyBorder(3, 0, 3, 0));
        JLabel lblIzquierda = new JLabel(izquierda);
        lblIzquierda.setFont(F_NORMAL);
        lblIzquierda.setForeground(TEXTO);
        JLabel lblDerecha = new JLabel(derecha);
        lblDerecha.setFont(F_NORMAL);
        lblDerecha.setForeground(TEXTO);
        linea.add(lblIzquierda, BorderLayout.WEST);
        linea.add(lblDerecha, BorderLayout.EAST);
        linea.setMaximumSize(new Dimension(Integer.MAX_VALUE, linea.getPreferredSize().height));
        return linea;
    }

    private void mostrarMensaje(String texto, Mensaje tipo) {
        lblMensaje.setText(texto);
        lblMensaje.setForeground(tipo.texto);
        barraMensajes.setBackground(tipo.fondo);
        barraMensajes.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, tipo.borde),
                BorderFactory.createEmptyBorder(14, 24, 14, 24)));
        barraMensajes.revalidate();
        barraMensajes.repaint();
    }

    // =====================================================================
    // Botón plano con efecto al pasar el mouse (funciona igual en todos los L&F)
    // =====================================================================

    private static class BotonPlano extends JButton {

        BotonPlano(String texto, Color fondo, Color fondoHover, Color colorTexto, Color colorBorde) {
            super(texto);
            setFont(F_NEGRITA);
            setForeground(colorTexto);
            setBackground(fondo);
            setContentAreaFilled(false);   // primero esto: resetea 'opaque'
            setOpaque(true);               // y después esto, para que pinte el fondo
            setFocusPainted(false);
            setBorder(BorderFactory.createLineBorder(colorBorde));
            setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBackground(fondoHover);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBackground(fondo);
                }
            });
        }
    }
}