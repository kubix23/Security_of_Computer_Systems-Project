package org.example.app.mainPanel.impl;

import org.example.app.buttons.impl.*;
import org.example.app.mainPanel.api.MainPanel;
import org.example.app.textFields.api.LogsField;
import org.example.app.textFields.impl.LogsFieldComponent;
import org.example.app.textFields.impl.PasswordInputComponent;

import javax.swing.*;
import java.awt.*;

/**
 * Klasa głównego panelu aplikacji.
 */
public class Panel extends JFrame implements MainPanel {

    /**
     * Konstruktor głównej aplikacji
     */
    public Panel() throws HeadlessException {
        super("Hello World");
        config();
        addComponents();
        setVisible(true);
    }

    @Override
    public void config() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 700);
        setLocation(50, 50);
        setLayout(new GridLayout(0, 1));
    }

    @Override
    public void addComponents() {
        LogsField logsField = new LogsFieldComponent();
        PasswordInputComponent passwordInput = new PasswordInputComponent();
        add(new JScrollPane((Component) logsField));
        add(passwordInput);
        add(new RSAGenerateButton(passwordInput, logsField));
        add(new ShowRSAKeysButton(passwordInput, logsField));
        add(new ValidateRSAKeysButton(passwordInput, logsField));
        add(new SignFileButton(passwordInput, logsField));
        add(new ValidateSignFileButton(logsField));
        add(new EncryptFileButton(passwordInput, logsField));
        add(new DecryptFileButton(logsField));
    }
}
