package de.lexoland.image_to_mc.core;

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

public class CharField extends JTextField {
	private static final long serialVersionUID = -147103989753950056L;
	
	private CharField charField;
	
	public CharField(char c) {
		super(String.valueOf(c));
		this.charField = this;
		((PlainDocument) getDocument()).setDocumentFilter(new Filter());
	}
	
	public void setChar(char c) {
		setText(String.valueOf(c));
	}
	
	private class Filter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
			throws BadLocationException {
			if(charField.getText().length() < 1)
				super.insertString(fb, offset, string, attr);
			else
				Toolkit.getDefaultToolkit().beep();
		}
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
			if(length == 1 || charField.getText().length() == 0)
				super.replace(fb, offset, length, text, attrs);
			else
				Toolkit.getDefaultToolkit().beep();
		}
		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
			super.remove(fb, offset, length);
		}
	}

}
