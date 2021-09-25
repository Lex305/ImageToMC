package de.lexoland.image_to_mc.core;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.NumberFormatter;

import de.lexoland.image_to_mc.Main;

public class Rescale {
	
	private boolean keepAspectRatio = true;
	
	private int ratioWidth;
	private int ratioHeight;
	private int width;
	private int height;
	
	public Rescale(int width, int height) {
		this.ratioWidth = width;
		this.ratioHeight = height;
		this.width = width;
		this.height = height;
	}
	
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getRatioHeight() {
		return ratioHeight;
	}
	public int getRatioWidth() {
		return ratioWidth;
	}
	public void setRatioHeight(int ratioHeight) {
		this.ratioHeight = ratioHeight;
	}
	public void setRatioWidth(int ratioWidth) {
		this.ratioWidth = ratioWidth;
	}
	public void setKeepAspectRatio(boolean keepAspectRatio) {
		this.keepAspectRatio = keepAspectRatio;
	}
	public boolean isKeepAspectRatio() {
		return keepAspectRatio;
	}
	
	public Rescale divide(double val) {
		width /= val;
		height /= val;
		return this;
	}
	
	private JSpinner widthSpinner;
	private JSpinner heightSpinner;

	public void openDialog(JFrame parent) {
		RescaleDialog dialog = new RescaleDialog(parent, this);
		dialog.pack();
		dialog.setMinimumSize(dialog.getSize());
		dialog.setLocationRelativeTo(parent);
		dialog.setVisible(true);
	}

//	public void openFrame(JFrame parent) {
//		JDialog frame = new JDialog(parent, "Rescale Options...", true);
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		frame.setLayout(null);
//		frame.setIconImage(Main.iconRescale);
//		frame.setSize(300, 160);
//		frame.setLocationRelativeTo(parent);
//		frame.setResizable(false);
//
//		JCheckBox checkbox = new JCheckBox("Keep Aspect Ratio");
//		checkbox.setSelected(keepAspectRatio);
//		checkbox.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				if(checkbox.isSelected()) {
//					float val = (float) (int) widthSpinner.getValue() / (float) ratioWidth;
//					heightSpinner.setValue(Math.round(val * ratioHeight));
//				}
//			}
//		});
//		checkbox.setBounds(10, 10, 200, 20);
//		frame.add(checkbox);
//
//		JLabel widthLabel = new JLabel("Width:");
//		widthLabel.setHorizontalAlignment(JLabel.RIGHT);
//		widthLabel.setBounds(10, 40, 50, 20);
//		frame.add(widthLabel);
//
//		JLabel heightLabel = new JLabel("Height:");
//		heightLabel.setHorizontalAlignment(JLabel.RIGHT);
//		heightLabel.setBounds(10, 70, 50, 20);
//		frame.add(heightLabel);
//
//		widthSpinner = new JSpinner(new SpinnerNumberModel(width, 1, Integer.MAX_VALUE, 10));
//
//		JFormattedTextField txtWidth = ((JSpinner.NumberEditor) widthSpinner.getEditor()).getTextField();
//		txtWidth.addCaretListener(new CaretListener() {
//			@Override
//			public void caretUpdate(CaretEvent e) {
//				SwingUtilities.invokeLater(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							widthSpinner.commitEdit();
//							txtWidth.setCaretPosition(txtWidth.getText().length());
//						} catch (ParseException e1) {
//						}
//					}
//				});
//			}
//		});
//
//		widthSpinner.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				if(checkbox.isSelected()) {
//					float val = (float) (int) widthSpinner.getValue() / (float) ratioWidth;
//					heightSpinner.setValue(Math.round(val * ratioHeight));
//				}
//			}
//		});
//
//		widthSpinner.setBounds(70, 40, 80, 40);
//		frame.add(widthSpinner);
//
//		heightSpinner = new JSpinner(new SpinnerNumberModel(height, 1, Integer.MAX_VALUE, 10));
//
//		JFormattedTextField txtHeight = ((JSpinner.NumberEditor) heightSpinner.getEditor()).getTextField();
//		txtHeight.addCaretListener(new CaretListener() {
//			@Override
//			public void caretUpdate(CaretEvent e) {
//				SwingUtilities.invokeLater(new Runnable() {
//					@Override
//					public void run() {
//						try {
//							heightSpinner.commitEdit();
//							txtHeight.setCaretPosition(txtHeight.getText().length());
//						} catch (ParseException e1) {
//						}
//					}
//				});
//			}
//		});
//		((NumberFormatter) txtHeight.getFormatter()).setAllowsInvalid(false);
//		heightSpinner.addChangeListener(new ChangeListener() {
//			@Override
//			public void stateChanged(ChangeEvent e) {
//				if(checkbox.isSelected()) {
//					float val = (float) (int) heightSpinner.getValue() / (float) ratioHeight;
//					widthSpinner.setValue(Math.round(val * ratioWidth));
//				}
//			}
//		});
//		heightSpinner.setBounds(70, 70, 80, 20);
//		frame.add(heightSpinner);
//
//		JButton saveButton = new JButton("Save");
//		saveButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				frame.dispose();
//				width = (int) widthSpinner.getValue();
//				height = (int) heightSpinner.getValue();
//				keepAspectRatio = checkbox.isSelected();
//			}
//		});
//		saveButton.setBounds(10, 100, 100, 20);
//		frame.add(saveButton);
//
//		JButton cancelButton = new JButton("Cancel");
//		cancelButton.addActionListener(new ActionListener() {
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				frame.dispose();
//			}
//		});
//		cancelButton.setBounds(180, 100, 100, 20);
//		frame.add(cancelButton);
//
//
//		frame.setVisible(true);
//	}
}
