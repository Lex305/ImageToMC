package de.lexoland.image_to_mc.frames;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;

import de.lexoland.image_to_mc.Main;
import de.lexoland.image_to_mc.core.*;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private final JComboBox<Theme> themeComboBox;
	private boolean themeChanged = false;

	private JPanel contentPane;
	private JTextField playerField;
	private JTextField fileField;
	
	private String jsonText = "";
	private JTextArea output;
	private JScrollPane scrollPane;

	public Thread convertThread;
	public ConvertDialog convertDialog;
	private JCheckBox rescaleCheckbox;
	
	private Component[] generateGroup;
	
	private MainFrame frame;
	private JButton imageGenerateButton;
	private JButton playerGenerateButton;
	private JTextArea output_1;
	private JTextArea output_2;
	private JButton copyButton_3;
	private JPanel generatedPanel;
	private JTabbedPane tabbedPane;
	private JPanel image_panel;
	private JPanel player_panel;
	private CharField characterField;
	private JCheckBox optimizeCheckbox;
	
	private Rescale imageRescale = new Rescale(100, 100);
	private Rescale gifRescale = new Rescale(100, 100);

	public MainFrame() {
		this.frame = this;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 749, 516);
		setTitle("Image to Minecraft");
		setIconImage(Main.iconMain);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[]{419, 419, 0};
		gbl_contentPane.rowHeights = new int[]{92, 24, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[]{1.0, 1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[]{1.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		JPanel panel_2 = new JPanel();
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.insets = new Insets(0, 0, 5, 0);
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 0;
		contentPane.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[]{754, 0};
		gbl_panel_2.rowHeights = new int[]{0, 0};
		gbl_panel_2.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				int index = tabbedPane.getSelectedIndex();
				if(index == 3)
					loadSettings();
				if(index == 2 || index == 3) {
					setContentsEnabled(generatedPanel, false);
				} else {
					setContentsEnabled(generatedPanel, true);
				}
				
			}
		});
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		panel_2.add(tabbedPane, gbc_tabbedPane);
		
		player_panel = new JPanel();
//		player_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Player Head", null, player_panel, null);
		GridBagLayout gbl_player_panel = new GridBagLayout();
		gbl_player_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_player_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_player_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_player_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		player_panel.setLayout(gbl_player_panel);
		
		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_2 = new GridBagConstraints();
		gbc_horizontalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_2.gridx = 1;
		gbc_horizontalStrut_2.gridy = 0;
		player_panel.add(horizontalStrut_2, gbc_horizontalStrut_2);
		
		JLabel lblNewLabel = new JLabel("Player Name:");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		player_panel.add(lblNewLabel, gbc_lblNewLabel);
		
		playerField = new JTextField();
		GridBagConstraints gbc_playerField = new GridBagConstraints();
		gbc_playerField.gridwidth = 3;
		gbc_playerField.insets = new Insets(0, 0, 5, 5);
		gbc_playerField.fill = GridBagConstraints.HORIZONTAL;
		gbc_playerField.gridx = 2;
		gbc_playerField.gridy = 1;
		player_panel.add(playerField, gbc_playerField);
		playerField.setColumns(10);
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_1 = new GridBagConstraints();
		gbc_verticalStrut_1.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut_1.gridx = 21;
		gbc_verticalStrut_1.gridy = 1;
		player_panel.add(verticalStrut_1, gbc_verticalStrut_1);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut = new GridBagConstraints();
		gbc_verticalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut.gridx = 0;
		gbc_verticalStrut.gridy = 2;
		player_panel.add(verticalStrut, gbc_verticalStrut);
		
		playerGenerateButton = new JButton("Generate");
		playerGenerateButton.addActionListener(e -> {
			convertThread = new Thread(() -> {
				convertDialog = ConvertDialog.openDialog(MainFrame.this, () -> {
					convertThread.stop();
				});
				try {
					setGenerateGroupEnabled(false);
					convertDialog.setIndeterminate(true);
					BufferedImage skin = Main.stealSkin(playerField.getText());
					BufferedImage face = Main.overlap(Main.cutFace(skin), Main.cutFaceOverlay(skin));
					jsonText = Main.imageToJson(face);
					setFilterEnabled(false);
					updateGenerated();
					setFilterEnabled(true);
				} finally {
					convertDialog.dispose();
					Toolkit.getDefaultToolkit().beep();
					setGenerateGroupEnabled(true);
				}
			});
			convertThread.start();
		});
		GridBagConstraints gbc_playerGenerateButton = new GridBagConstraints();
		gbc_playerGenerateButton.anchor = GridBagConstraints.WEST;
		gbc_playerGenerateButton.insets = new Insets(0, 0, 5, 5);
		gbc_playerGenerateButton.gridx = 2;
		gbc_playerGenerateButton.gridy = 2;
		player_panel.add(playerGenerateButton, gbc_playerGenerateButton);
		
		image_panel = new JPanel();
//		image_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Image", null, image_panel, null);
		GridBagLayout gbl_image_panel = new GridBagLayout();
		gbl_image_panel.columnWidths = new int[]{0, 75, 56, 73, 0, 0, 0, 3, 0};
		gbl_image_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_image_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_image_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		image_panel.setLayout(gbl_image_panel);
		
		Component horizontalStrut_3 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_3 = new GridBagConstraints();
		gbc_horizontalStrut_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_horizontalStrut_3.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_3.gridx = 2;
		gbc_horizontalStrut_3.gridy = 0;
		image_panel.add(horizontalStrut_3, gbc_horizontalStrut_3);
		
		Component verticalStrut_2 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_2 = new GridBagConstraints();
		gbc_verticalStrut_2.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_2.gridx = 0;
		gbc_verticalStrut_2.gridy = 1;
		image_panel.add(verticalStrut_2, gbc_verticalStrut_2);
		
		JLabel lblNewLabel_1 = new JLabel("Image File:");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 1;
		image_panel.add(lblNewLabel_1, gbc_lblNewLabel_1);
		
		fileField = new JTextField();
		GridBagConstraints gbc_fileField = new GridBagConstraints();
		gbc_fileField.fill = GridBagConstraints.HORIZONTAL;
		gbc_fileField.gridwidth = 4;
		gbc_fileField.insets = new Insets(0, 0, 5, 5);
		gbc_fileField.gridx = 2;
		gbc_fileField.gridy = 1;
		image_panel.add(fileField, gbc_fileField);
		fileField.setColumns(10);
		
		JButton browse = new JButton("Browse");
		browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return ".png .jpg";
					}
					
					@Override
					public boolean accept(File f) {
						if(f.getName().toLowerCase().endsWith(".png"))
							return true;
						if(f.getName().toLowerCase().endsWith(".jpg"))
							return true;
						if(f.isDirectory())
							return true;
						return false;
					}
				});
				try {
					chooser.setCurrentDirectory(new File(fileField.getText()).getParentFile());
				} catch (Exception e2) {
				}
				chooser.showOpenDialog(contentPane);
				if(chooser.getSelectedFile() != null) {
					fileField.setText(chooser.getSelectedFile().getPath());
					try {
						BufferedImage image = ImageIO.read(chooser.getSelectedFile());
						if(image.getWidth() > 100 || image.getHeight() > 100) {
							int choice = JOptionPane.showConfirmDialog(frame, "Your image is larger than 100 x 100px." + System.lineSeparator() + "Do you want to enable rescaling?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							if(choice == JOptionPane.YES_OPTION) {
								rescaleCheckbox.setSelected(true);
								optionsButton1.setEnabled(true);
								imageRescale.setRatioWidth(image.getWidth());
								imageRescale.setRatioHeight(image.getHeight());
								imageRescale.setWidth(image.getWidth());
								imageRescale.setHeight(image.getHeight());
							}
						}
					} catch (Exception e2) {
					}
				}
			}
		});
		GridBagConstraints gbc_browse = new GridBagConstraints();
		gbc_browse.insets = new Insets(0, 0, 5, 5);
		gbc_browse.gridx = 6;
		gbc_browse.gridy = 1;
		image_panel.add(browse, gbc_browse);
		
		Component verticalStrut_3 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_3 = new GridBagConstraints();
		gbc_verticalStrut_3.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut_3.gridx = 7;
		gbc_verticalStrut_3.gridy = 1;
		image_panel.add(verticalStrut_3, gbc_verticalStrut_3);
		
		rescaleCheckbox = new JCheckBox("Rescale:");
//		rescaleCheckbox.setBackground(Color.WHITE);
		rescaleCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				optionsButton1.setEnabled(rescaleCheckbox.isSelected());
			}
		});
		GridBagConstraints gbc_rescaleCheckbox = new GridBagConstraints();
		gbc_rescaleCheckbox.anchor = GridBagConstraints.EAST;
		gbc_rescaleCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_rescaleCheckbox.gridx = 1;
		gbc_rescaleCheckbox.gridy = 2;
		image_panel.add(rescaleCheckbox, gbc_rescaleCheckbox);
		
		imageGenerateButton = new JButton("Generate");
		imageGenerateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				convertThread = new Thread() {
					public void run() {
						convertDialog = ConvertDialog.openDialog(MainFrame.this, () -> {
							convertThread.stop();
						});
						setGenerateGroupEnabled(false);
						convertDialog.setIndeterminate(true);
						try {
							BufferedImage image;
							try {
								image = ImageIO.read(new File(fileField.getText()));
								if(rescaleCheckbox.isSelected()) {
									BufferedImage rescaled = new BufferedImage(
										imageRescale.getWidth(),
										imageRescale.getHeight(),
										BufferedImage.TYPE_INT_ARGB
									);
									Graphics g = rescaled.getGraphics();
									g.drawImage(image, 0, 0,
										imageRescale.getWidth(),
										imageRescale.getHeight(),
										null
									);
									image = rescaled;
								}
								jsonText = Main.imageToJson(image);
								setFilterEnabled(false);
								updateGenerated();
								setFilterEnabled(true);
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(contentPane, "File not found!", "Error", JOptionPane.ERROR_MESSAGE);
							}
						} finally {
							setGenerateGroupEnabled(true);
							Toolkit.getDefaultToolkit().beep();
							convertDialog.dispose();
						}
					};
				};
				convertThread.start();
			}
		});
		
		optionsButton1 = new JButton("Options...");
		optionsButton1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				imageRescale.openDialog(frame);
				
			}
		});
		optionsButton1.setEnabled(false);
		GridBagConstraints gbc_optionsButton1 = new GridBagConstraints();
		gbc_optionsButton1.insets = new Insets(0, 0, 5, 5);
		gbc_optionsButton1.gridx = 2;
		gbc_optionsButton1.gridy = 2;
		image_panel.add(optionsButton1, gbc_optionsButton1);
		
		
		GridBagConstraints gbc_imageGenerateButton = new GridBagConstraints();
		gbc_imageGenerateButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_imageGenerateButton.insets = new Insets(0, 0, 5, 5);
		gbc_imageGenerateButton.gridx = 2;
		gbc_imageGenerateButton.gridy = 3;
		image_panel.add(imageGenerateButton, gbc_imageGenerateButton);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.gridwidth = 2;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		contentPane.add(panel, gbc_panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{1, 0};
		gbl_panel.rowHeights = new int[]{1, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		generatedPanel = new JPanel();
		generatedPanel.setBorder(new TitledBorder(null, "Generated", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_generatedPanel = new GridBagConstraints();
		gbc_generatedPanel.gridwidth = 2;
		gbc_generatedPanel.insets = new Insets(0, 0, 5, 0);
		gbc_generatedPanel.fill = GridBagConstraints.BOTH;
		gbc_generatedPanel.gridx = 0;
		gbc_generatedPanel.gridy = 2;
		contentPane.add(generatedPanel, gbc_generatedPanel);
		generatedPanel.setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane_1 = new JTabbedPane(JTabbedPane.TOP);
		generatedPanel.add(tabbedPane_1);
		
		JPanel tellrawTab = new JPanel();
//		tellrawTab.setBackground(Color.WHITE);
		tabbedPane_1.addTab("Tellraw", null, tellrawTab, null);
		GridBagLayout gbl_tellrawTab = new GridBagLayout();
		gbl_tellrawTab.columnWidths = new int[]{0, 0, 0, 0};
		gbl_tellrawTab.rowHeights = new int[]{0, 0, 0};
		gbl_tellrawTab.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_tellrawTab.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		tellrawTab.setLayout(gbl_tellrawTab);
		
		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_4 = new GridBagConstraints();
		gbc_horizontalStrut_4.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_4.gridx = 1;
		gbc_horizontalStrut_4.gridy = 0;
		tellrawTab.add(horizontalStrut_4, gbc_horizontalStrut_4);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridheight = 2;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		tellrawTab.add(scrollPane, gbc_scrollPane);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		output = new JTextArea();
		scrollPane.setViewportView(output);
		output.setFont(new Font("Consolas", 0, 12));
		output.setLineWrap(true);
		output.setEditable(true);
		
		JButton copyButton_1 = new JButton("   Copy   ");
		copyButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(out1Text), null);
			}
		});
		GridBagConstraints gbc_copyButton_1 = new GridBagConstraints();
		gbc_copyButton_1.insets = new Insets(0, 0, 0, 5);
		gbc_copyButton_1.anchor = GridBagConstraints.NORTH;
		gbc_copyButton_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_copyButton_1.gridx = 1;
		gbc_copyButton_1.gridy = 1;
		tellrawTab.add(copyButton_1, gbc_copyButton_1);
		
		Component verticalStrut_6 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_6 = new GridBagConstraints();
		gbc_verticalStrut_6.gridx = 2;
		gbc_verticalStrut_6.gridy = 1;
		tellrawTab.add(verticalStrut_6, gbc_verticalStrut_6);
		((PlainDocument) output.getDocument()).setDocumentFilter(new DisallowFilter());
		
		JPanel bookTab = new JPanel();
//		bookTab.setBackground(Color.WHITE);
		tabbedPane_1.addTab("Book", null, bookTab, null);
		GridBagLayout gbl_bookTab = new GridBagLayout();
		gbl_bookTab.columnWidths = new int[]{0, 0, 0, 0};
		gbl_bookTab.rowHeights = new int[]{0, 0, 0};
		gbl_bookTab.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_bookTab.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		bookTab.setLayout(gbl_bookTab);
		
		Component horizontalStrut_5 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_5 = new GridBagConstraints();
		gbc_horizontalStrut_5.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_5.gridx = 1;
		gbc_horizontalStrut_5.gridy = 0;
		bookTab.add(horizontalStrut_5, gbc_horizontalStrut_5);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.gridheight = 2;
		gbc_scrollPane_1.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 0;
		gbc_scrollPane_1.gridy = 0;
		bookTab.add(scrollPane_1, gbc_scrollPane_1);
		
		output_1 = new JTextArea();
		output_1.setLineWrap(true);
		output_1.setFont(new Font("Consolas", Font.PLAIN, 12));
		output_1.setEditable(true);
		scrollPane_1.setViewportView(output_1);
		
		JButton copyButton_2 = new JButton("   Copy   ");
		copyButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(out2Text), null);
			}
		});
		GridBagConstraints gbc_copyButton_2 = new GridBagConstraints();
		gbc_copyButton_2.insets = new Insets(0, 0, 0, 5);
		gbc_copyButton_2.anchor = GridBagConstraints.NORTH;
		gbc_copyButton_2.gridx = 1;
		gbc_copyButton_2.gridy = 1;
		bookTab.add(copyButton_2, gbc_copyButton_2);
		
		Component verticalStrut_7 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_7 = new GridBagConstraints();
		gbc_verticalStrut_7.gridx = 2;
		gbc_verticalStrut_7.gridy = 1;
		bookTab.add(verticalStrut_7, gbc_verticalStrut_7);
		
		JPanel jsonTab = new JPanel();
//		jsonTab.setBackground(Color.WHITE);
		tabbedPane_1.addTab("JSON", null, jsonTab, null);
		GridBagLayout gbl_jsonTab = new GridBagLayout();
		gbl_jsonTab.columnWidths = new int[]{0, 0, 0, 0};
		gbl_jsonTab.rowHeights = new int[]{0, 0, 0};
		gbl_jsonTab.columnWeights = new double[]{1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_jsonTab.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		jsonTab.setLayout(gbl_jsonTab);
		
		Component horizontalStrut_6 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_6 = new GridBagConstraints();
		gbc_horizontalStrut_6.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_6.gridx = 1;
		gbc_horizontalStrut_6.gridy = 0;
		jsonTab.add(horizontalStrut_6, gbc_horizontalStrut_6);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.gridheight = 2;
		gbc_scrollPane_2.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.gridx = 0;
		gbc_scrollPane_2.gridy = 0;
		jsonTab.add(scrollPane_2, gbc_scrollPane_2);
		
		output_2 = new JTextArea();
		output_2.setLineWrap(true);
		output_2.setFont(new Font("Consolas", Font.PLAIN, 12));
		output_2.setEditable(true);
		scrollPane_2.setViewportView(output_2);
		
		copyButton_3 = new JButton("   Copy   ");
		copyButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(out3Text), null);
			}
		});
		GridBagConstraints gbc_copyButton_3 = new GridBagConstraints();
		gbc_copyButton_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_copyButton_3.insets = new Insets(0, 0, 0, 5);
		gbc_copyButton_3.anchor = GridBagConstraints.NORTH;
		gbc_copyButton_3.gridx = 1;
		gbc_copyButton_3.gridy = 1;
		jsonTab.add(copyButton_3, gbc_copyButton_3);
		
		Component verticalStrut_8 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_8 = new GridBagConstraints();
		gbc_verticalStrut_8.gridx = 2;
		gbc_verticalStrut_8.gridy = 1;
		jsonTab.add(verticalStrut_8, gbc_verticalStrut_8);
		
		JPanel hologramTab = new JPanel();
//		hologramTab.setBackground(Color.WHITE);
		tabbedPane_1.addTab("Hologram", null, hologramTab, null);
		GridBagLayout gbl_hologramTab = new GridBagLayout();
		gbl_hologramTab.columnWidths = new int[]{0, 0, 0};
		gbl_hologramTab.rowHeights = new int[]{0, 0, 0, 0};
		gbl_hologramTab.columnWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		gbl_hologramTab.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		hologramTab.setLayout(gbl_hologramTab);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 0);
		gbc_horizontalStrut.gridx = 1;
		gbc_horizontalStrut.gridy = 0;
		hologramTab.add(horizontalStrut, gbc_horizontalStrut);
		
		Component verticalStrut_4 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_4 = new GridBagConstraints();
		gbc_verticalStrut_4.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_4.gridx = 0;
		gbc_verticalStrut_4.gridy = 1;
		hologramTab.add(verticalStrut_4, gbc_verticalStrut_4);
		
		JButton btnNewButton = new JButton("Save as McFunction");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setSelectedFile(new File("unnamed.mcfunction"));
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return "Minecraft Function (.mcfunction)";
					}
					
					@Override
					public boolean accept(File f) {
						if(f.isDirectory() || f.getName().endsWith(".mcfunction"))
							return true;
						return false;
					}
				});
				chooser.showSaveDialog(frame);
				File f = chooser.getSelectedFile();
				if(f != null) {
					if(!f.getName().endsWith(".mcfunction"))
						f = new File(f.getPath() + ".mcfunction");
					if(f.exists()) {
						int choice = JOptionPane.showConfirmDialog(frame, "\"" + f.getName() + "\" already exists!" + System.lineSeparator() + "Do you want to replace?", "Confirm", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						if(choice == JOptionPane.NO_OPTION)
							return;
					}
					Main.writeCommandsToFile(f, Main.jsonToHologram(jsonText.replace(" ", "")));
				}
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton.gridx = 1;
		gbc_btnNewButton.gridy = 1;
		hologramTab.add(btnNewButton, gbc_btnNewButton);
		
		JButton btnNewButton_1 = new JButton("Copy as One Command");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(new StringSelection(Main.createOneCommand(Main.jsonToHologram(jsonText.replace(" ", "")))), null);
				JOptionPane.showMessageDialog(frame, "Copied to clipboard!", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.anchor = GridBagConstraints.WEST;
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 2;
		hologramTab.add(btnNewButton_1, gbc_btnNewButton_1);
		
		generateGroup = new Component[] {playerGenerateButton, imageGenerateButton};
		
		DropTargetAdapter dropImage = new DropTargetAdapter() {
			
			@SuppressWarnings("unchecked")
			@Override
			public void drop(DropTargetDropEvent e) {
				Transferable tr = e.getTransferable();
				DataFlavor[] flavors = tr.getTransferDataFlavors();
				for (int i = 0; i < flavors.length; i++) {
					if (flavors[i].isFlavorJavaFileListType()) {
						e.acceptDrop(e.getDropAction());
						try {
							List<File> files = (List<File>) tr.getTransferData(flavors[i]);
							File f = files.get(0);
							if((f.getName().endsWith(".png") || f.getName().endsWith(".jpg")) && f.isFile()) {
								fileField.setText(f.getPath());
								try {
									BufferedImage image = ImageIO.read(f);
									if(image.getWidth() > 100 || image.getHeight() > 100) {
										int choice = JOptionPane.showConfirmDialog(frame, "Your image is larger than 100 x 100px." + System.lineSeparator() + "Do you want to enable rescaling?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
										if(choice == JOptionPane.YES_OPTION) {
											rescaleCheckbox.setSelected(true);
											optionsButton1.setEnabled(true);
											imageRescale.setRatioWidth(image.getWidth());
											imageRescale.setRatioHeight(image.getHeight());
											imageRescale.setWidth(image.getWidth());
											imageRescale.setHeight(image.getHeight());
										}
									}
								} catch (Exception e2) {
								}
							}
							e.dropComplete(true);
						} catch (UnsupportedFlavorException | IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				return;
			}
			@SuppressWarnings("unchecked")
			@Override
			public void dragEnter(DropTargetDragEvent e) {
				Transferable tr = e.getTransferable();
				DataFlavor[] flavors = tr.getTransferDataFlavors();
				for (int i = 0; i < flavors.length; i++) {
					if (flavors[i].isFlavorJavaFileListType()) {
						try {
							List<File> files = (List<File>) tr.getTransferData(flavors[i]);
							File f = files.get(0);
							if((f.getName().endsWith(".png") || f.getName().endsWith(".jpg")) && f.isFile()) {
								
							} else
								e.rejectDrag();
						} catch (UnsupportedFlavorException | IOException e1) {
							e1.printStackTrace();
						}
					}
				}
				return;
			}
		};
		new DropTarget(contentPane, dropImage);
		new DropTarget(fileField, dropImage);
		
		;
		new DropTarget(fileField, dropImage);
		
		JPanel gif_panel = new JPanel();
//		gif_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("GIF", null, gif_panel, null);
		GridBagLayout gbl_gif_panel = new GridBagLayout();
		gbl_gif_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_gif_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_gif_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_gif_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gif_panel.setLayout(gbl_gif_panel);
		
		Component horizontalStrut_8 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_8 = new GridBagConstraints();
		gbc_horizontalStrut_8.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_8.gridx = 1;
		gbc_horizontalStrut_8.gridy = 0;
		gif_panel.add(horizontalStrut_8, gbc_horizontalStrut_8);
		
		Component verticalStrut_10 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_10 = new GridBagConstraints();
		gbc_verticalStrut_10.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_10.gridx = 0;
		gbc_verticalStrut_10.gridy = 1;
		gif_panel.add(verticalStrut_10, gbc_verticalStrut_10);
		
		JLabel lblNewLabel_9 = new JLabel("GIF File:");
		GridBagConstraints gbc_lblNewLabel_9 = new GridBagConstraints();
		gbc_lblNewLabel_9.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_9.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_9.gridx = 1;
		gbc_lblNewLabel_9.gridy = 1;
		gif_panel.add(lblNewLabel_9, gbc_lblNewLabel_9);
		
		gifFileField = new JTextField();
		GridBagConstraints gbc_gifFileField = new GridBagConstraints();
		gbc_gifFileField.gridwidth = 2;
		gbc_gifFileField.insets = new Insets(0, 0, 5, 5);
		gbc_gifFileField.fill = GridBagConstraints.HORIZONTAL;
		gbc_gifFileField.gridx = 2;
		gbc_gifFileField.gridy = 1;
		gif_panel.add(gifFileField, gbc_gifFileField);
		gifFileField.setColumns(10);
		
		JButton btnNewButton_4 = new JButton("Browse");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setAcceptAllFileFilterUsed(false);
				chooser.addChoosableFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						return ".png .jpg";
					}
					
					@Override
					public boolean accept(File f) {
						if(f.getName().toLowerCase().endsWith(".gif"))
							return true;
						if(f.isDirectory())
							return true;
						return false;
					}
				});
				try {
					chooser.setCurrentDirectory(new File(gifFileField.getText()).getParentFile());
				} catch (Exception e2) {
				}
				chooser.showOpenDialog(contentPane);
				if(chooser.getSelectedFile() != null) {
					gifFileField.setText(chooser.getSelectedFile().getPath());
					try {
						BufferedImage image = ImageIO.read(chooser.getSelectedFile());
						if(image.getWidth() > 100 || image.getHeight() > 100) {
							int choice = JOptionPane.showConfirmDialog(frame, "Your gif is larger than 100 x 100px." + System.lineSeparator() + "Do you want to enable rescaling?", "Warning", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
							if(choice == JOptionPane.YES_OPTION) {
								gifRescaleCheckbox.setSelected(true);
								optionsButton2.setEnabled(true);
								gifRescale.setRatioWidth(image.getWidth());
								gifRescale.setRatioHeight(image.getHeight());
								gifRescale.setWidth(image.getWidth());
								gifRescale.setHeight(image.getHeight());
							}
						}
					} catch (Exception e2) {
					}
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_4 = new GridBagConstraints();
		gbc_btnNewButton_4.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_4.gridx = 4;
		gbc_btnNewButton_4.gridy = 1;
		gif_panel.add(btnNewButton_4, gbc_btnNewButton_4);
		
		Component verticalStrut_11 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_11 = new GridBagConstraints();
		gbc_verticalStrut_11.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut_11.gridx = 5;
		gbc_verticalStrut_11.gridy = 1;
		gif_panel.add(verticalStrut_11, gbc_verticalStrut_11);
		
		gifRescaleCheckbox = new JCheckBox("Rescale:");
//		gifRescaleCheckbox.setBackground(Color.WHITE);
		gifRescaleCheckbox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				optionsButton2.setEnabled(gifRescaleCheckbox.isSelected());
			}
		});
		GridBagConstraints gbc_gifRescaleCheckbox = new GridBagConstraints();
		gbc_gifRescaleCheckbox.anchor = GridBagConstraints.EAST;
		gbc_gifRescaleCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_gifRescaleCheckbox.gridx = 1;
		gbc_gifRescaleCheckbox.gridy = 2;
		gif_panel.add(gifRescaleCheckbox, gbc_gifRescaleCheckbox);
		
		optionsButton2 = new JButton("Options...");
		optionsButton2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gifRescale.openDialog(frame);
			}
		});
		optionsButton2.setEnabled(false);
		GridBagConstraints gbc_optionsButton2 = new GridBagConstraints();
		gbc_optionsButton2.anchor = GridBagConstraints.WEST;
		gbc_optionsButton2.insets = new Insets(0, 0, 5, 5);
		gbc_optionsButton2.gridx = 2;
		gbc_optionsButton2.gridy = 2;
		gif_panel.add(optionsButton2, gbc_optionsButton2);
		
		controlableFramesCheckbox = new JCheckBox("Enable jumping between frames");
		controlableFramesCheckbox.setToolTipText("If disabled, you can't jump between the frames.");
		GridBagConstraints gbc_controlableFramesCheckbox = new GridBagConstraints();
		gbc_controlableFramesCheckbox.gridwidth = 3;
		gbc_controlableFramesCheckbox.anchor = GridBagConstraints.WEST;
		gbc_controlableFramesCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_controlableFramesCheckbox.gridx = 1;
		gbc_controlableFramesCheckbox.gridy = 3;
		gif_panel.add(controlableFramesCheckbox, gbc_controlableFramesCheckbox);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new TitledBorder(null, "Export", TitledBorder.LEADING, TitledBorder.TOP, null, null));
//		panel_1.setBackground(Color.WHITE);
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.gridwidth = 4;
		gbc_panel_1.insets = new Insets(0, 0, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 4;
		gif_panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.rowHeights = new int[]{0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		intoDatapackButton = new JRadioButton("Into Datapack:");
		intoDatapackButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setGroupEnabled(intoDatapackGroup, true);
				setGroupEnabled(onlyFilesGroup, false);
			}
		});
		exportGroup.add(intoDatapackButton);
		intoDatapackButton.setSelected(true);
//		intoDatapackButton.setBackground(Color.WHITE);
		GridBagConstraints gbc_intoDatapackButton = new GridBagConstraints();
		gbc_intoDatapackButton.anchor = GridBagConstraints.WEST;
		gbc_intoDatapackButton.insets = new Insets(0, 0, 5, 5);
		gbc_intoDatapackButton.gridx = 0;
		gbc_intoDatapackButton.gridy = 0;
		panel_1.add(intoDatapackButton, gbc_intoDatapackButton);
		
		JLabel lblNewLabel_11 = new JLabel("Destination:");
		GridBagConstraints gbc_lblNewLabel_11 = new GridBagConstraints();
		gbc_lblNewLabel_11.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_11.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_11.gridx = 2;
		gbc_lblNewLabel_11.gridy = 0;
		panel_1.add(lblNewLabel_11, gbc_lblNewLabel_11);
		
		intoDatapackDestinationField = new JTextField();
		GridBagConstraints gbc_intoDatapackDestinationField = new GridBagConstraints();
		gbc_intoDatapackDestinationField.gridwidth = 3;
		gbc_intoDatapackDestinationField.insets = new Insets(0, 0, 5, 5);
		gbc_intoDatapackDestinationField.fill = GridBagConstraints.HORIZONTAL;
		gbc_intoDatapackDestinationField.gridx = 3;
		gbc_intoDatapackDestinationField.gridy = 0;
		panel_1.add(intoDatapackDestinationField, gbc_intoDatapackDestinationField);
		intoDatapackDestinationField.setColumns(10);
		
		JButton btnNewButton_5 = new JButton("Browse");
		btnNewButton_5.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				intoDatapackDestinationField.setText(chooser.getSelectedFile().getAbsolutePath());
		});
		GridBagConstraints gbc_btnNewButton_5 = new GridBagConstraints();
		gbc_btnNewButton_5.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_5.gridx = 6;
		gbc_btnNewButton_5.gridy = 0;
		panel_1.add(btnNewButton_5, gbc_btnNewButton_5);
		
		Component verticalStrut_13 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_13 = new GridBagConstraints();
		gbc_verticalStrut_13.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut_13.gridx = 7;
		gbc_verticalStrut_13.gridy = 0;
		panel_1.add(verticalStrut_13, gbc_verticalStrut_13);
		
		onlyFilesButton = new JRadioButton("Only Files:");
		onlyFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setGroupEnabled(intoDatapackGroup, false);
				setGroupEnabled(onlyFilesGroup, true);
			}
		});
		exportGroup.add(onlyFilesButton);
//		onlyFilesButton.setBackground(Color.WHITE);
		GridBagConstraints gbc_onlyFilesButton = new GridBagConstraints();
		gbc_onlyFilesButton.insets = new Insets(0, 0, 5, 5);
		gbc_onlyFilesButton.anchor = GridBagConstraints.WEST;
		gbc_onlyFilesButton.gridx = 0;
		gbc_onlyFilesButton.gridy = 1;
		panel_1.add(onlyFilesButton, gbc_onlyFilesButton);
		
		Component verticalStrut_12 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_12 = new GridBagConstraints();
		gbc_verticalStrut_12.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_12.gridx = 1;
		gbc_verticalStrut_12.gridy = 1;
		panel_1.add(verticalStrut_12, gbc_verticalStrut_12);
		
		JLabel lblNewLabel_10 = new JLabel("Workspace Name:");
		lblNewLabel_10.setEnabled(false);
		GridBagConstraints gbc_lblNewLabel_10 = new GridBagConstraints();
		gbc_lblNewLabel_10.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_10.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_10.gridx = 2;
		gbc_lblNewLabel_10.gridy = 1;
		panel_1.add(lblNewLabel_10, gbc_lblNewLabel_10);
		
		workspaceNameField = new JTextField();
		workspaceNameField.setEnabled(false);
		GridBagConstraints gbc_workspaceNameField = new GridBagConstraints();
		gbc_workspaceNameField.insets = new Insets(0, 0, 5, 5);
		gbc_workspaceNameField.fill = GridBagConstraints.HORIZONTAL;
		gbc_workspaceNameField.gridx = 3;
		gbc_workspaceNameField.gridy = 1;
		panel_1.add(workspaceNameField, gbc_workspaceNameField);
		workspaceNameField.setColumns(10);
		
		JLabel lblNewLabel_12 = new JLabel("Destination:");
		lblNewLabel_12.setEnabled(false);
		GridBagConstraints gbc_lblNewLabel_12 = new GridBagConstraints();
		gbc_lblNewLabel_12.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_12.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_12.gridx = 4;
		gbc_lblNewLabel_12.gridy = 1;
		panel_1.add(lblNewLabel_12, gbc_lblNewLabel_12);
		
		onlyFilesDestinationField = new JTextField();
		onlyFilesDestinationField.setEnabled(false);
		GridBagConstraints gbc_onlyFilesDestinationField = new GridBagConstraints();
		gbc_onlyFilesDestinationField.insets = new Insets(0, 0, 5, 5);
		gbc_onlyFilesDestinationField.fill = GridBagConstraints.HORIZONTAL;
		gbc_onlyFilesDestinationField.gridx = 5;
		gbc_onlyFilesDestinationField.gridy = 1;
		panel_1.add(onlyFilesDestinationField, gbc_onlyFilesDestinationField);
		onlyFilesDestinationField.setColumns(10);
		
		JButton btnNewButton_6 = new JButton("Browse");
		btnNewButton_6.addActionListener(e -> {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			chooser.setAcceptAllFileFilterUsed(false);
			if(chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)
				onlyFilesDestinationField.setText(chooser.getSelectedFile().getAbsolutePath());
		});
		btnNewButton_6.setEnabled(false);
		GridBagConstraints gbc_btnNewButton_6 = new GridBagConstraints();
		gbc_btnNewButton_6.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_6.gridx = 6;
		gbc_btnNewButton_6.gridy = 1;
		panel_1.add(btnNewButton_6, gbc_btnNewButton_6);
		
		JButton exportAsHologramButton = new JButton("Export As Hologram");
		exportAsHologramButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				convertThread = new Thread() {
					@Override
					public void run() {
						convertDialog = ConvertDialog.openDialog(Main.frame, () -> {
							convertThread.stop();
						});
						GifConverter.saveGifAsFunctions(
							new File(gifFileField.getText()),
							new File(intoDatapackButton.isSelected() ? intoDatapackDestinationField.getText() : onlyFilesDestinationField.getText()),
							gifRescale,
							controlableFramesCheckbox.isSelected(),
							intoDatapackButton.isSelected() ? null : workspaceNameField.getText()
						);
						Toolkit.getDefaultToolkit().beep();
						convertDialog.dispose();
					}
				};
				convertThread.start();
			}
		});
		GridBagConstraints gbc_exportAsHologramButton = new GridBagConstraints();
		gbc_exportAsHologramButton.anchor = GridBagConstraints.SOUTHWEST;
		gbc_exportAsHologramButton.insets = new Insets(0, 0, 5, 5);
		gbc_exportAsHologramButton.gridx = 0;
		gbc_exportAsHologramButton.gridy = 3;
		panel_1.add(exportAsHologramButton, gbc_exportAsHologramButton);
		
//		JButton btnNewButton_8 = new JButton("Export As Tellraw");
//		btnNewButton_8.setToolTipText("Soon!");
//		btnNewButton_8.setEnabled(false);
//		GridBagConstraints gbc_btnNewButton_8 = new GridBagConstraints();
//		gbc_btnNewButton_8.anchor = GridBagConstraints.SOUTH;
//		gbc_btnNewButton_8.insets = new Insets(0, 0, 5, 5);
//		gbc_btnNewButton_8.gridx = 2;
//		gbc_btnNewButton_8.gridy = 3;
//		panel_1.add(btnNewButton_8, gbc_btnNewButton_8);
		
		Component horizontalStrut_10 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_10 = new GridBagConstraints();
		gbc_horizontalStrut_10.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_10.gridx = 0;
		gbc_horizontalStrut_10.gridy = 4;
		panel_1.add(horizontalStrut_10, gbc_horizontalStrut_10);
		
		Component horizontalStrut_9 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_9 = new GridBagConstraints();
		gbc_horizontalStrut_9.gridwidth = 2;
		gbc_horizontalStrut_9.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_9.gridx = 2;
		gbc_horizontalStrut_9.gridy = 5;
		gif_panel.add(horizontalStrut_9, gbc_horizontalStrut_9);
		
		JPanel settings_panel = new JPanel();
//		settings_panel.setBackground(Color.WHITE);
		tabbedPane.addTab("Settings", null, settings_panel, null);
		GridBagLayout gbl_settings_panel = new GridBagLayout();
		gbl_settings_panel.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_settings_panel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_settings_panel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		gbl_settings_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		settings_panel.setLayout(gbl_settings_panel);
		
		Component horizontalStrut_7 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_7 = new GridBagConstraints();
		gbc_horizontalStrut_7.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_7.gridx = 2;
		gbc_horizontalStrut_7.gridy = 0;
		settings_panel.add(horizontalStrut_7, gbc_horizontalStrut_7);
		
		JLabel lblNewLabel_4 = new JLabel("Character:");
		GridBagConstraints gbc_lblNewLabel_4 = new GridBagConstraints();
		gbc_lblNewLabel_4.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_4.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_4.gridx = 1;
		gbc_lblNewLabel_4.gridy = 1;
		settings_panel.add(lblNewLabel_4, gbc_lblNewLabel_4);
		
		characterField = new CharField('█');
		GridBagConstraints gbc_characterField = new GridBagConstraints();
		gbc_characterField.insets = new Insets(0, 0, 5, 5);
		gbc_characterField.fill = GridBagConstraints.HORIZONTAL;
		gbc_characterField.gridx = 2;
		gbc_characterField.gridy = 1;
		settings_panel.add(characterField, gbc_characterField);
		characterField.setColumns(10);
		
		JButton defaultButton = new JButton("Default");
		defaultButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				characterField.setChar('█');
			}
		});
		GridBagConstraints gbc_defaultButton = new GridBagConstraints();
		gbc_defaultButton.anchor = GridBagConstraints.WEST;
		gbc_defaultButton.insets = new Insets(0, 0, 5, 5);
		gbc_defaultButton.gridx = 4;
		gbc_defaultButton.gridy = 1;
		settings_panel.add(defaultButton, gbc_defaultButton);
		
		JLabel lblNewLabel_5 = new JLabel("Optimize Generated:");
		GridBagConstraints gbc_lblNewLabel_5 = new GridBagConstraints();
		gbc_lblNewLabel_5.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_5.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_5.gridx = 1;
		gbc_lblNewLabel_5.gridy = 2;
		settings_panel.add(lblNewLabel_5, gbc_lblNewLabel_5);
		
		optimizeCheckbox = new JCheckBox("");
		optimizeCheckbox.setSelected(true);
		GridBagConstraints gbc_optimizeCheckbox = new GridBagConstraints();
		gbc_optimizeCheckbox.anchor = GridBagConstraints.WEST;
		gbc_optimizeCheckbox.insets = new Insets(0, 0, 5, 5);
		gbc_optimizeCheckbox.gridx = 2;
		gbc_optimizeCheckbox.gridy = 2;
		settings_panel.add(optimizeCheckbox, gbc_optimizeCheckbox);
		
		JLabel lblNewLabel_7 = new JLabel("Hologram Line Spacing:");
		GridBagConstraints gbc_lblNewLabel_7 = new GridBagConstraints();
		gbc_lblNewLabel_7.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_7.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_7.gridx = 1;
		gbc_lblNewLabel_7.gridy = 3;
		settings_panel.add(lblNewLabel_7, gbc_lblNewLabel_7);
		
		lineSpacingSpinner = new JSpinner();
		lineSpacingSpinner.setModel(new SpinnerNumberModel(0.225, null, null, 0.1));
		GridBagConstraints gbc_lineSpacingSpinner = new GridBagConstraints();
		gbc_lineSpacingSpinner.fill = GridBagConstraints.HORIZONTAL;
		gbc_lineSpacingSpinner.insets = new Insets(0, 0, 5, 5);
		gbc_lineSpacingSpinner.gridx = 2;
		gbc_lineSpacingSpinner.gridy = 3;
		settings_panel.add(lineSpacingSpinner, gbc_lineSpacingSpinner);
		
		JLabel lblNewLabel_8 = new JLabel("Blocks");
		GridBagConstraints gbc_lblNewLabel_8 = new GridBagConstraints();
		gbc_lblNewLabel_8.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_8.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_8.gridx = 3;
		gbc_lblNewLabel_8.gridy = 3;
		settings_panel.add(lblNewLabel_8, gbc_lblNewLabel_8);
		
		JButton defaultButton2 = new JButton("Default");
		defaultButton2.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				lineSpacingSpinner.setValue(0.225);
			}
		});
		GridBagConstraints gbc_defaultButton2 = new GridBagConstraints();
		gbc_defaultButton2.anchor = GridBagConstraints.WEST;
		gbc_defaultButton2.insets = new Insets(0, 0, 5, 5);
		gbc_defaultButton2.gridx = 4;
		gbc_defaultButton2.gridy = 3;
		settings_panel.add(defaultButton2, gbc_defaultButton2);

		{
			JLabel lblNewLabel_13 = new JLabel("Theme:");
			GridBagConstraints gbc_lblNewLabel_13 = new GridBagConstraints();
			gbc_lblNewLabel_13.anchor = GridBagConstraints.EAST;
			gbc_lblNewLabel_13.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_13.gridx = 1;
			gbc_lblNewLabel_13.gridy = 4;
			settings_panel.add(lblNewLabel_13, gbc_lblNewLabel_13);
		}

		{
			themeComboBox = new JComboBox(new DefaultComboBoxModel<>(Theme.values()));
			themeComboBox.setSelectedIndex(Theme.valueOf(Main.properties.getProperty("theme", Theme.ONE_DARK.name())).ordinal());
			themeComboBox.addActionListener(e -> {
				themeChanged = true;
			});
			GridBagConstraints gbc_lblNewLabel_13 = new GridBagConstraints();
			gbc_lblNewLabel_13.anchor = GridBagConstraints.WEST;
			gbc_lblNewLabel_13.insets = new Insets(0, 0, 5, 5);
			gbc_lblNewLabel_13.gridx = 2;
			gbc_lblNewLabel_13.gridy = 4;
			settings_panel.add(themeComboBox, gbc_lblNewLabel_13);
		}
		
		
		
		Component verticalStrut_5 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_5 = new GridBagConstraints();
		gbc_verticalStrut_5.insets = new Insets(0, 0, 5, 5);
		gbc_verticalStrut_5.gridx = 0;
		gbc_verticalStrut_5.gridy = 6;
		settings_panel.add(verticalStrut_5, gbc_verticalStrut_5);
		
		JButton btnNewButton_2 = new JButton("Save Settings");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveSettings();
				JOptionPane.showMessageDialog(Main.frame, "Settings saved!", "Info", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_2.anchor = GridBagConstraints.SOUTHWEST;
		gbc_btnNewButton_2.gridx = 1;
		gbc_btnNewButton_2.gridy = 6;
		settings_panel.add(btnNewButton_2, gbc_btnNewButton_2);
		
		JButton btnNewButton_3 = new JButton("Open Program Folder");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().open(new File(Main.appDir));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});
		GridBagConstraints gbc_btnNewButton_3 = new GridBagConstraints();
		gbc_btnNewButton_3.anchor = GridBagConstraints.SOUTHEAST;
		gbc_btnNewButton_3.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_3.gridx = 16;
		gbc_btnNewButton_3.gridy = 6;
		settings_panel.add(btnNewButton_3, gbc_btnNewButton_3);
		
		Component verticalStrut_9 = Box.createVerticalStrut(20);
		GridBagConstraints gbc_verticalStrut_9 = new GridBagConstraints();
		gbc_verticalStrut_9.insets = new Insets(0, 0, 5, 0);
		gbc_verticalStrut_9.gridx = 17;
		gbc_verticalStrut_9.gridy = 6;
		settings_panel.add(verticalStrut_9, gbc_verticalStrut_9);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 0, 5);
		gbc_horizontalStrut_1.gridx = 1;
		gbc_horizontalStrut_1.gridy = 7;
		settings_panel.add(horizontalStrut_1, gbc_horizontalStrut_1);
		
		JLabel lblNewLabel_3 = new JLabel("Created by Lex305");
		GridBagConstraints gbc_lblNewLabel_3 = new GridBagConstraints();
		gbc_lblNewLabel_3.anchor = GridBagConstraints.WEST;
		gbc_lblNewLabel_3.insets = new Insets(0, 0, 0, 5);
		gbc_lblNewLabel_3.gridx = 0;
		gbc_lblNewLabel_3.gridy = 3;
		contentPane.add(lblNewLabel_3, gbc_lblNewLabel_3);
		
		JLabel lblNewLabel_2 = new JLabel("Github: Lex305/ImageToMC");
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblNewLabel_2 = new GridBagConstraints();
		gbc_lblNewLabel_2.anchor = GridBagConstraints.EAST;
		gbc_lblNewLabel_2.gridx = 1;
		gbc_lblNewLabel_2.gridy = 3;
		contentPane.add(lblNewLabel_2, gbc_lblNewLabel_2);
		
		intoDatapackGroup = new Component[] {lblNewLabel_11, intoDatapackDestinationField, btnNewButton_5};
		onlyFilesGroup = new Component[] {lblNewLabel_10, workspaceNameField, lblNewLabel_12, onlyFilesDestinationField, btnNewButton_6};
	}
	
	private Component[] intoDatapackGroup;
	private Component[] onlyFilesGroup;
	
	private void setGenerateGroupEnabled(boolean enabled) {
		for(Component c : generateGroup) {
			c.setEnabled(enabled);
		}
	}
	
	private String out1Text = "";
	private String out2Text = "";
	private String out3Text = "";
	private JSpinner lineSpacingSpinner;
	private JTextField gifFileField;
	private JButton optionsButton1;
	private JButton optionsButton2;
	private final ButtonGroup exportGroup = new ButtonGroup();
	private JTextField intoDatapackDestinationField;
	private JTextField workspaceNameField;
	private JTextField onlyFilesDestinationField;
	private JRadioButton onlyFilesButton;
	private JRadioButton intoDatapackButton;
	private JCheckBox controlableFramesCheckbox;
	private JCheckBox gifRescaleCheckbox;
	
	private void updateGenerated() {
		if(jsonText.length() < 32767) {
			output.setEnabled(true);
			output_1.setEnabled(true);
			output_2.setEnabled(true);
			output.setText("/tellraw @a " + jsonText);
			output_1.setText("/give @p minecraft:written_book{title:\"\",author:\"\",pages:[\'" + jsonText.replace("\\n", "\\\\n") + "\']} 1");
			output_2.setText(jsonText);
			out1Text = "/tellraw @a " + jsonText;
			out2Text = "/give @p minecraft:written_book{title:\"\",author:\"\",pages:[\'" + jsonText.replace("\\n", "\\\\n") + "\']} 1";
			out3Text = jsonText;
		} else {
			output.setEnabled(false);
			output_1.setEnabled(false);
			output_2.setEnabled(false);
			output.setText("Too long...\nPlease use the copy button");
			output_1.setText("Too long...\nPlease use the copy button");
			output_2.setText("Too long...\nPlease use the copy button");
			out1Text = "/tellraw @a " + jsonText;
			out2Text = "/give @p minecraft:written_book{title:\"\",author:\"\",pages:[\'" + jsonText.replace("\\n", "\\\\n") + "\']} 1";
			out3Text = jsonText;
		}
	}
	
	private void setFilterEnabled(boolean b) {
		((PlainDocument) output.getDocument()).setDocumentFilter(b ? new DisallowFilter() : null);
		((PlainDocument) output_1.getDocument()).setDocumentFilter(b ? new DisallowFilter() : null);
		((PlainDocument) output_2.getDocument()).setDocumentFilter(b ? new DisallowFilter() : null);
	}
	
	public static void setContentsEnabled(Container container, boolean enable) {
		try {
			container.setEnabled(enable);
			for(Component c : container.getComponents()) {
				if(c instanceof Container)
					setContentsEnabled((Container) c, enable);
				else
					c.setEnabled(enable);
			}
		} catch (NullPointerException e) {}
	}
	public static void setGroupEnabled(Component[] group, boolean enable) {
		try {
			for(Component c : group)
				c.setEnabled(enable);
		} catch (NullPointerException e) {}
	}
	
	private void loadSettings() {
		try {
			characterField.setChar(Main.properties.getProperty("character").charAt(0));
			optimizeCheckbox.setSelected(Boolean.parseBoolean(Main.properties.getProperty("optimize")));
			lineSpacingSpinner.setValue(Double.parseDouble(Main.properties.getProperty("hologramLineSpacing")));
		} catch (NullPointerException e) {
		}
	}
	
	private void saveSettings() {
		Main.properties.setProperty("character", characterField.getText());
		Main.properties.setProperty("optimize", String.valueOf(optimizeCheckbox.isSelected()));
		Main.properties.setProperty("hologramLineSpacing", String.valueOf(lineSpacingSpinner.getValue()));
		Main.properties.setProperty("theme", ((Theme) themeComboBox.getSelectedItem()).name());
		if(themeChanged)
			((Theme) themeComboBox.getSelectedItem()).setTheme();
		Main.saveProperties();
	}
	
	public class DisallowFilter extends DocumentFilter {
		@Override
		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
			throws BadLocationException {
		}
		@Override
		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
			throws BadLocationException {
		}
		@Override
		public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
		}
	}
}
