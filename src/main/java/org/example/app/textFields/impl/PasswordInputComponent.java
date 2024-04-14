package org.example.app.textFields.impl;

import org.example.app.textFields.api.PasswordInput;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa opisująca działanie panelu, który zawiera pole tekstowe do wprowadzania hasła i etykietę do tego pola.
 */
public class PasswordInputComponent extends JPanel implements PasswordInput {
    private final JLabel jl1;

    /**
     * Konstruktor pola tekstowego do wprowadzania hasła
     */
    public PasswordInputComponent() {
        JTextField jtf = new JTextField("Hasło wpisane tutaj będzie wykorzystywane przez wszystke funkcje programu");
        jl1 = new JLabel("Tutaj wpisz hasło:");
        jl1.setVerticalAlignment(SwingConstants.CENTER);
        jl1.setLabelFor(jtf);
        setLayout(new BorderLayout());
        add(jl1, BorderLayout.WEST);
        add(jtf, BorderLayout.CENTER);
    }

    @Override
    public String getPassword() {
        return jl1.getText();
    }
}
