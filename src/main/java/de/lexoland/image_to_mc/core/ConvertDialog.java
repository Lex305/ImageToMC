package de.lexoland.image_to_mc.core;

import javax.swing.*;
import java.awt.event.*;

public class ConvertDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonCancel;
    private JProgressBar progressBar;

    private final Runnable onCancel;

    public ConvertDialog(JFrame parent, Runnable onCancel) {
        super(parent);
        this.onCancel = onCancel;
        setContentPane(contentPane);
//        setModal(true);
        setTitle("Converting...");
        setIconImages(null);

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public void setIndeterminate(boolean newVal) {
        progressBar.setIndeterminate(newVal);
    }

    public void setMaximum(int max) {
        progressBar.setMaximum(max);
    }

    public void setValue(int value) {
        progressBar.setValue(value);
    }

    private void onCancel() {
        onCancel.run();
        dispose();
    }

    public static ConvertDialog openDialog(JFrame parent, Runnable onCancel) {
        ConvertDialog dialog = new ConvertDialog(parent, onCancel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
        return dialog;
    }
}
