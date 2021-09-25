package de.lexoland.image_to_mc.core;

import com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme;
import de.lexoland.image_to_mc.Main;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;
import java.awt.event.*;
import java.text.ParseException;

public class RescaleDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JCheckBox keepAspectRatioCheckBox;
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;

    private final Rescale rescale;

    public RescaleDialog(JFrame parent, Rescale rescale) {
        super(parent);
        this.rescale = rescale;
        setTitle("Rescale Options...");
        setIconImage(Main.iconRescale);
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());
        buttonCancel.addActionListener(e -> onCancel());

        keepAspectRatioCheckBox.setSelected(rescale.isKeepAspectRatio());
        keepAspectRatioCheckBox.addActionListener(e -> {
            if(keepAspectRatioCheckBox.isSelected()) {
                float val = (float) (int) widthSpinner.getValue() / (float) rescale.getRatioWidth();
                heightSpinner.setValue(Math.round(val * rescale.getRatioHeight()));
            }
        });
        initSpinners();

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private boolean changing = false;

    private void initSpinners() {
        widthSpinner.setModel(new SpinnerNumberModel(rescale.getWidth(), 1, Integer.MAX_VALUE, 10));
        heightSpinner.setModel(new SpinnerNumberModel(rescale.getHeight(), 1, Integer.MAX_VALUE, 10));

        JFormattedTextField txtWidth = ((JSpinner.NumberEditor) widthSpinner.getEditor()).getTextField();
        txtWidth.addCaretListener(e -> SwingUtilities.invokeLater(() -> {
            try {
                widthSpinner.commitEdit();
                txtWidth.setCaretPosition(txtWidth.getText().length());
            } catch (ParseException ignore) {}
        }));
        widthSpinner.addChangeListener(e -> {
            if(keepAspectRatioCheckBox.isSelected() && !changing) {
                changing = true;
                float val = (float) (int) widthSpinner.getValue() / (float) rescale.getRatioHeight();
                heightSpinner.setValue(Math.round(val * rescale.getRatioHeight()));
                changing = false;
            }
        });

        JFormattedTextField txtHeight = ((JSpinner.NumberEditor) heightSpinner.getEditor()).getTextField();
        txtHeight.addCaretListener(e -> SwingUtilities.invokeLater(() -> {
            try {
                heightSpinner.commitEdit();
                txtHeight.setCaretPosition(txtHeight.getText().length());
            } catch (ParseException ignore) {}
        }));
        ((NumberFormatter) txtHeight.getFormatter()).setAllowsInvalid(false);
        heightSpinner.addChangeListener(e -> {
            if(keepAspectRatioCheckBox.isSelected() && !changing) {
                changing = true;
                float val = (float) (int) heightSpinner.getValue() / (float) rescale.getRatioHeight();
                widthSpinner.setValue(Math.round(val * rescale.getRatioWidth()));
                changing = false;
            }
        });
    }

    private void onOK() {
        rescale.setWidth((Integer) widthSpinner.getValue());
        rescale.setHeight((Integer) heightSpinner.getValue());
        rescale.setKeepAspectRatio(keepAspectRatioCheckBox.isSelected());
        dispose();
    }

    private void onCancel() {
        dispose();
    }
}
