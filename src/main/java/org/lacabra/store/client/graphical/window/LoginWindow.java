/**
 * @file LoginWindow.java
 * @brief Define la ventana de inicio de sesión para la aplicación.
 */
package org.lacabra.store.client.graphical.window;

import org.lacabra.store.client.graphical.dispatcher.DispatchedWindow;
import org.lacabra.store.client.graphical.dispatcher.Signal;
import org.lacabra.store.client.graphical.dispatcher.WindowDispatcher;
import org.lacabra.store.internals.logging.Logger;
import org.lacabra.store.server.api.type.user.Credentials;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Serial;
import java.util.concurrent.CompletableFuture;

/**
 * @class LoginWindow
 * @brief Implementa la interfaz gráfica para la ventana de inicio de sesión.
 */

public final class LoginWindow extends DispatchedWindow {
    @Serial
    private final static long serialVersionUID = 1L;

    /**
     * @brief Serial version UID para la serialización.
     */
    @Serial
    private final static long serialVersionUID1 = 1L;

    /**
     * @brief Título de la ventana.
     */
    public static final String TITLE = "Iniciar sesión";

    /**
     * @brief Tamaño de la ventana.
     */
    public static final Dimension SIZE = new Dimension(600, 400);

    /**
     * @brief Tamaño de los campos de entrada.
     */
    public static final Dimension FIELD_SIZE = new Dimension(200, 30);

    /**
     * @brief Tamaño del botón "Volver al inicio".
     */
    public static final Dimension BACK_BUTTON_SIZE = new Dimension(160, 25);

    /**
     * @brief Tamaño de las etiquetas del formulario.
     */
    public static final Dimension FORM_LABEL_SIZE = new Dimension(140, 40);

    /**
     * @brief Ancho del borde del formulario.
     */
    public static final int BORDER = 20;

    /**
     * @param wd Dispatcher de ventanas.
     * @brief Constructor de la clase LoginWindow.
     */

    public LoginWindow(final WindowDispatcher wd) {
        super(wd);
    }

    /**
     * @param dispatcher El dispatcher de ventanas a configurar.
     * @brief Configura el dispatcher de la ventana.
     */

    @Override
    public void setDispatcher(final WindowDispatcher dispatcher) {
        super.setDispatcher(dispatcher);

        final var d = this.getDispatcher();
        if (d == null) return;

        final var controller = d.controller();
        if (controller == null) return;

        final var user = controller.getUser();
        final var uid = user == null ? null : user.id();

        controller.unauth();

        this.auth(() -> this.replace(HomeWindow.class), () -> {
            {
                final var w = windows();

                if (w != null) {
                    final var lw = w.values().stream().filter(x -> x.getClass().equals(LoginWindow.class)).findFirst();
                    if (lw.isPresent()) {
                        this.close();
                        lw.get().requestFocus();
                    }
                }
            }

            final var id = new JTextField();
            if (uid != null)
                id.setText(uid.toString());

            final var closed = new WindowAdapter() {
                @Override
                public void windowClosed(final WindowEvent e) {
                    replace(AuthWindow.class);
                }
            };
            this.addWindowListener(closed);

            this.addWindowFocusListener(new WindowAdapter() {
                @Override
                public void windowGainedFocus(final WindowEvent e) {
                    id.requestFocusInWindow();
                }
            });

            this.setTitle(TITLE);
            this.setSize(SIZE);
            this.setLocationRelativeTo(null);

            try {
                final var res = this.getClass().getClassLoader().getResource("icon.png");
                if (res != null) {
                    this.setIconImage(new ImageIcon(res.getFile()).getImage());
                }
            } catch (Exception e) {
                Logger.getLogger().warning(e);
            }

            {
                final var p = new JPanel();
                p.setLayout(new FlowLayout(FlowLayout.LEFT));

                {
                    final var b = new JButton("Volver al inicio");

                    b.setPreferredSize(BACK_BUTTON_SIZE);
                    b.addActionListener(e -> this.close());

                    p.add(b);
                }

                this.add(p, BorderLayout.NORTH);
            }

            {
                final var p = new JPanel();
                p.setLayout(new GridLayout(6, 1));
                p.setBorder(BorderFactory.createEmptyBorder(BORDER, BORDER, BORDER, BORDER));

                {
                    final var b = new JButton("Iniciar sesión");

                    final var creds = new Signal<>(new Credentials(uid));
                    creds.effect(c -> b.setEnabled(!(c.id() == null || c.passwd() == null || c.passwd().isEmpty())));

                    this.connect(creds);

                    {
                        final var p2 = new JPanel();
                        p2.setLayout(new FlowLayout(FlowLayout.LEFT));

                        {
                            final var l = new JLabel("Nombre de usuario", SwingConstants.LEFT);

                            l.setPreferredSize(FORM_LABEL_SIZE);
                            p2.add(l);
                        }

                        {
                            id.setPreferredSize(FIELD_SIZE);
                            id.addActionListener(e -> b.doClick());
                            id.getDocument().addDocumentListener(new DocumentListener() {
                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                    this.changedUpdate(e);
                                }

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                    this.changedUpdate(e);
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                    creds.set(creds.get().id(id.getText()));
                                }
                            });

                            p2.add(id);
                        }

                        p.add(p2);
                    }

                    {
                        final var p2 = new JPanel();
                        p2.setLayout(new FlowLayout(FlowLayout.LEFT));

                        {
                            final var l = new JLabel("Contraseña", SwingConstants.LEADING);

                            l.setPreferredSize(FORM_LABEL_SIZE);
                            p2.add(l);
                        }

                        {
                            final var t = new JPasswordField();

                            t.setPreferredSize(FIELD_SIZE);
                            t.addActionListener(e -> b.doClick());
                            t.getDocument().addDocumentListener(new DocumentListener() {
                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                    this.changedUpdate(e);
                                }

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                    this.changedUpdate(e);
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                    creds.set(creds.get().passwd(new String(t.getPassword())));
                                }
                            });

                            p2.add(t);
                        }

                        p.add(p2);
                    }

                    {
                        b.setPreferredSize(FIELD_SIZE);

                        b.setEnabled(false);
                        b.addActionListener(e -> {
                            final var c = creds.get();

                            this.load(() -> {
                                try {
                                    return controller.auth(c);
                                } catch (Exception exception) {
                                    return CompletableFuture.completedFuture(false);
                                }
                            }, (final Boolean auth) -> {
                                if (!auth) {
                                    this.message("No se pudo iniciar sesión.");

                                    return;
                                }

                                this.removeWindowListener(closed);
                                this.replace(HomeWindow.class);
                            }, DispatchedWindow.AUTH_MESSAGE, DispatchedWindow.AUTH_TIMEOUT, false);
                        });

                        p.add(b);
                    }
                }

                this.add(p, BorderLayout.CENTER);
            }

            this.setVisible(true);
        });
    }
}