package org.example;

import javax.swing.*;

public class FolderChooser extends JFileChooser {
    public FolderChooser(String name) {
        super();
        this.setDialogTitle(name);
        this.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        this.setAcceptAllFileFilterUsed(false);
    }
}
