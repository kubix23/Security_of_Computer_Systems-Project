package org.example;

import javax.swing.*;

public class FolderChooser extends JFileChooser {
    public FolderChooser() {
        super();
        this.setDialogTitle("Wybierz miejsce zapisu");
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.setAcceptAllFileFilterUsed(false);
    }
}
