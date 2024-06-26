/**
 * @file ItemDetailsWindow.java
 * @brief Define la ventana para mostrar los detalles de un artículo.
 */

package org.lacabra.store.client.graphical.window;

import org.lacabra.store.client.dto.ItemDTO;
import org.lacabra.store.client.graphical.dispatcher.DispatchedWindow;
import org.lacabra.store.client.graphical.dispatcher.Signal;
import org.lacabra.store.client.graphical.dispatcher.WindowDispatcher;
import org.lacabra.store.internals.type.id.UserId;
import org.lacabra.store.server.api.type.item.ItemType;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serial;
import java.util.HashMap;

/**
 * @class ItemDetailsWindow
 * @brief Implementa la interfaz gráfica para mostrar los detalles de un artículo.
 */
public class ItemDetailsWindow extends DispatchedWindow {
    /** @brief Título de la ventana. */
    public static final String TITLE = "Detalles del artículo";
    
    /** @brief Tamaño de la ventana. */
    public static final Dimension SIZE = new Dimension(800, 600);
    
    /** @brief Tamaño de la imagen del artículo. */
    public static final Dimension IMAGE_SIZE = new Dimension(300, 300);
    
    /** @brief Borde de la ventana. */
    public static final int BORDER = 10;
    
    /** @brief Intervalo de actualización del artículo en milisegundos. */
    public static final int UPDATE_ITEM = 1000;
    
    @Serial
    private final static long serialVersionUID = 1L;

    /**
     * @brief Constructor de la ventana de detalles del artículo.
     * @param item Artículo a mostrar los detalles.
     */
    public ItemDetailsWindow(final ItemDTO item) {
        this(null, item);
    }

    /**
     * @brief Constructor de la ventana de detalles del artículo.
     * @param wd Dispatcher de ventanas.
     * @param item Artículo a mostrar los detalles.
     */
    public ItemDetailsWindow(final WindowDispatcher wd, final ItemDTO item) {
        super(wd, item);
    }

    /**
     * @brief Configura el dispatcher de la ventana.
     * @param wd Dispatcher de ventanas.
     */
    @Override
    public void setDispatcher(final WindowDispatcher wd) {
        this.setDispatcher(wd, (Signal<ItemDTO>) null);
    }

    /**
     * @brief Configura el dispatcher de la ventana.
     * @param wd Dispatcher de ventanas.
     * @param signal Señal para actualizar el artículo.
     */

    public void setDispatcher(final WindowDispatcher wd, final Signal<ItemDTO> signal) {
        super.setDispatcher(wd, signal);

        final var controller = this.controller();
        if (controller == null) return;

        controller.auth().thenAccept((auth) -> {
            if (!auth) {
                controller.unauth();
                this.replace(AuthWindow.class);

                return;
            }

            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            this.setTitle(TITLE);
            this.setSize(SIZE);
            this.setLocationRelativeTo(null);
            this.setLayout(new BorderLayout());

            Long update = null;
            if (signal != null) {
                signal.interval(UPDATE_ITEM);

                update = signal.effect(curr -> {
                    if (signal.isRunning())
                        return;

                    if (curr == null)
                        return;

                    controller.GET.Item.id(curr.id()).thenAccept((item) -> {
                        if (item == null)
                            return;

                        signal.set(item);
                    });
                });
            }

            {
                final var p = new JPanel();
                p.setLayout(new BorderLayout());
                p.setBorder(new EmptyBorder(BORDER, BORDER, BORDER, BORDER));

                {
                    final var p2 = new JPanel();
                    p2.setLayout(new GridLayout(1, 4));
                    p2.setBackground(Color.LIGHT_GRAY);
                    p2.setBorder(new EmptyBorder(BORDER / 2, BORDER / 2, BORDER / 2, BORDER / 2));

                    for (Object[] init : new Object[][]{{"Inicio", null, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            close();
                            dispatch(HomeWindow.class);
                        }

                    }}, {"Categoría", null, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            close();
                            dispatch(ShoppingWindow.class);
                        }

                    }}, {"Carrito", "src/main/resources/img/carro.png", new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            close();
                            dispatch(ShoppingCartWindow.class);
                        }

                    }}, {"Buscar",
                            "src/main/resources/img/lupa.png", new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            close();
                            dispatch(ShoppingWindow.class);
                        }

                    }}}) {
                        final var b = new JButton((String) init[0], new ImageIcon((String) init[1]));

                        b.setHorizontalTextPosition(SwingConstants.CENTER);
                        b.setVerticalTextPosition(SwingConstants.BOTTOM);
                        b.addActionListener((ActionListener) init[2]);

                        p2.add(b);
                    }

                    p.add(p2, BorderLayout.NORTH);
                }

                {
                    final var l = new JLabel();
                    l.setPreferredSize(IMAGE_SIZE);

                    {
                        var i = new ImageIcon("src/main/resources/img/utilidad.jpg");
                        ItemDTO item=signal.get();
                        if(item.type().equals(ItemType.Utilities)) {
                            i = new ImageIcon("src/main/resources/img/utilidad.jpg");
                        }else if(item.type().equals(ItemType.Decoration)) {
                            i = new ImageIcon("src/main/resources/img/decoracion.jpg");
                        }else if(item.type().equals(ItemType.Accessories)) {
                            i = new ImageIcon("src/main/resources/img/accesorio.jpg");
                        }else if(item.type().equals(ItemType.Clothing)) {
                            i = new ImageIcon("src/main/resources/img/ropa.jpg");
                        }
                        l.setIcon(i);
                    }

                    p.add(l, BorderLayout.WEST);
                }

                {
                    final var p2 = new JTabbedPane();

                    {
                        final var p3 = new JPanel();
                        p3.setLayout(new BorderLayout());
                        p3.setBorder(new CompoundBorder(new TitledBorder("Reseñas del producto"),
                                new EmptyBorder(BORDER, BORDER, BORDER, BORDER)));

                        if (signal != null)
                            signal.effect(item -> {
                                if (item == null)
                                    return;

                                p3.removeAll();

                                {
                                    final var p4 = new JPanel();

                                    new HashMap<UserId, String>().forEach((user, review) -> {
                                        final var p5 = new JPanel();

                                        {
                                            final var t = new JTextArea(review);
                                            t.setEditable(false);

                                            p5.add(t);
                                        }

                                        p4.add(p5);
                                    });

                                    p3.add(new JScrollPane(p4), BorderLayout.CENTER);
                                }

                                {
                                    final var b = new JButton("Agregar reseña");
                                    b.addActionListener(e -> {
                                        Object[] options = {"1", "2", "3", "4", "5"};

                                        int choice = JOptionPane.showOptionDialog(
                                                null,
                                                "Por favor, seleccione su puntuación del producto:",
                                                "Valorar",
                                                JOptionPane.DEFAULT_OPTION,
                                                JOptionPane.PLAIN_MESSAGE,
                                                null,
                                                options,
                                                options[0]);

                                        int score = choice + 1;
                                        this.input("Escribe tu reseña:");
                                    });

                                    p3.add(b, BorderLayout.SOUTH);
                                }
                            }, update);

                        p2.add(p3);
                    }

                    p.add(p2, BorderLayout.CENTER);
                }

                {
                    p.add(this.footer(true), BorderLayout.SOUTH);
                }

                this.add(p);
            }

            this.setVisible(true);
            itemCheck(signal.get());
        });

    }
    public void itemCheck(ItemDTO item){
        String adv="Advertencia";
        String msg="Mantener fuera del alcance de los niños.";
        String msg1="Precaución, producto inflamable.";
        String msg2="Manipular con cuidado. El producto puede contener sustancias nocivas.";
        if(item.type().equals(ItemType.Accessories)){
            JOptionPane.showMessageDialog(this, msg,
                    adv,
                    JOptionPane.INFORMATION_MESSAGE);
        }

        if(item.type().equals(ItemType.Clothing)){
            JOptionPane.showMessageDialog(this, msg1,
                    adv,
                    JOptionPane.INFORMATION_MESSAGE);
        }

        if(item.type().equals(ItemType.Utilities)){
            JOptionPane.showMessageDialog(this, msg2,
                    adv,
                    JOptionPane.INFORMATION_MESSAGE);
        }

    }

}

