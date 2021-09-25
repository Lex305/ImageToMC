package de.lexoland.image_to_mc;

import java.awt.Desktop;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import de.lexoland.image_to_mc.core.Theme;
import de.lexoland.image_to_mc.frames.MainFrame;

public class Main {
	
	public static String appDir = System.getenv("APPDATA") + "/.lexoland/itm/";
	
	public static final int VERSION = 4;
	public static MainFrame frame;
	
	public static Image iconMain;
	public static Image iconRescale;
	static {
		try {
			iconMain = ImageIO.read(Main.class.getResource("/icon.png"));
			iconRescale = ImageIO.read(Main.class.getResource("/rescale.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		loadProperties();
		Theme.valueOf(properties.getProperty("theme", "DARK")).setTheme();

		openFrame();

		BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://lexoland.net/application/imageToMinecraft/version").openStream()));
		if(VERSION < Integer.parseInt(reader.readLine())) {
			int choice = JOptionPane.showConfirmDialog(frame, "A new update is available!" + System.lineSeparator() + "Do you want to download it?", "Update available", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
			if(choice == JOptionPane.YES_OPTION) {
				if(Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI("http://lexoland.net/application/imageToMinecraft/"));
					} catch (IOException | URISyntaxException e) {
						e.printStackTrace();
					}
				}
			}
		}
		reader.close();
		
	}

	public static void openFrame() {
		if(frame != null)
			frame.dispose();
		frame = new MainFrame();
		frame.setVisible(true);
	}
	
	public static String imageToJson(BufferedImage image) {
		System.out.println("Converting...");
		char c = properties.getProperty("character").charAt(0);
		WritableRaster raster = image.getRaster();
		int lastPx = image.getHeight() - 1;
		int height = image.getHeight();
		int width = image.getWidth();
		
		int[] pixel = new int[4];
		String[] arr = new String[height * width + height - 1];
		
		int i = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				pixel = raster.getPixel(x, y, new int[4]);
				arr[i] = "{\"text\":\"" + c + "\",\"color\":\"" + String.format("#%02x%02x%02x", pixel[0], pixel[1], pixel[2]) + "\"}";
				i++;
			}
			if (y != lastPx) {
				arr[i] = "{\"text\":\"\\n\"}";
			}
			i++;
		}

		if(Boolean.parseBoolean(properties.getProperty("optimize", "true"))) {
			System.out.println("Optimizing...");
			String o = arr[0];
			String[] newArr = new String[height * width + height - 1];
			char[] chars = new char[width];
			i = 0;
			int charIndex = 1;
			for(String s : arr) {
				try {
					if(s.equals(o)) {
						chars[charIndex] = c;
						charIndex++;
					} else {
						newArr[i] = o.replaceFirst(String.valueOf(c), new String(chars).trim());
						chars = new char[width];
						chars[0] = c;
						charIndex = 1;
						i++;
					}
				} catch (IndexOutOfBoundsException e) {
					newArr[i] = o.replaceFirst(String.valueOf(c), new String(chars).trim());
					chars = new char[width];
					chars[0] = c;
					charIndex = 1;
					i++;
				}
				o = s;
			}
			newArr[i] = o.replaceFirst(String.valueOf(c), new String(chars).trim());
			chars = new char[width];
			chars[0] = c;
			charIndex = 1;
			i++;
			arr = Arrays.copyOf(newArr, i);
		}
		String s = Arrays.toString(arr);
		return s.replace(" ", "");
	}
	public static List<String> jsonToHologram(String json) {
		List<String> commands = new ArrayList<String>();
		String[] array = json.replace(",{\"text\":\"\\n\"},", "split").split("split");
		double lineSpacing = Double.parseDouble(properties.getProperty("hologramLineSpacing", "0.225"));
		double height = lineSpacing * array.length;
		for(String s : array) {
			commands.add("summon minecraft:armor_stand ~ ~" + (double) Math.round(height * 1000f) / 1000f + " ~ {CustomNameVisible:1b,NoGravity:1b,Small:1b,Marker:1b,Invisible:1b,CustomName:'[" + s.replace(" ", "").replace("\\", "\\\\").replace("]", "").replace("[", "") + "]'}");
			height -= lineSpacing;
		}
		return commands;
	}
	public static void writeCommandsToFile(File f, List<String> commands) {
		try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8));
			for(String c : commands) {
				writer.write(c);
				writer.newLine();
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String colorToHex(int r, int g, int b) {
		return String.format("#%02x%02x%02x", r, g, b); 
	}
	
	public static BufferedImage stealSkin(String player) {
		try {
			return ImageIO.read(new URL("https://minecraft.tools/download-skin/" + player));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(frame, "Unknown player!", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
		return null;
	}
	
	public static BufferedImage cutFace(BufferedImage image) {
		return image.getSubimage(8, 8, 8, 8);
	}
	public static BufferedImage cutFaceOverlay(BufferedImage image) {
		return image.getSubimage(40, 8, 8, 8);
	}
	public static BufferedImage overlap(BufferedImage img1, BufferedImage img2) {
		BufferedImage img = new BufferedImage(
			img1.getWidth() > img2.getWidth() ? img1.getWidth() : img2.getWidth(),
			img1.getHeight() > img2.getHeight() ? img1.getHeight() : img2.getHeight(),
			BufferedImage.TYPE_INT_ARGB
		);
		Graphics g = img.getGraphics();
		g.drawImage(img1, 0, 0, null);
		g.drawImage(img2, 0, 0, null);
		return img;
	}
	public static String createOneCommand(List<String> commands) {
		String finalCommand = "/summon minecraft:falling_block ~ ~0.5 ~ {Motion:[.0,.5,.0],BlockState:{Name:\"minecraft:activator_rail\",Properties:{powered:\"true\"}},Time:1,Passengers:[{id:falling_block,BlockState:{Name:redstone_block},Time:1},";
		for(String s : commands) {
			finalCommand += "{id:\"minecraft:command_block_minecart\",Command:\"" + s.replace("\"", "\\\"") + "\"},";
		}
		finalCommand += "{id:\"minecraft:command_block_minecart\",Command:\"" + "kill @e[type=minecraft:command_block_minecart,distance=..2]" + "\"}";
		finalCommand += "]}";
		return finalCommand;
	}
	public static Properties propertiesDefaults = new Properties();
	static {
		propertiesDefaults.setProperty("character", "█");
		propertiesDefaults.setProperty("optimize", "false");
		propertiesDefaults.setProperty("hologramLineSpacing", "0.225");
		propertiesDefaults.setProperty("theme", Theme.ONE_DARK.name());
	}
	public static Properties properties = new Properties(propertiesDefaults);
	
	
	@SuppressWarnings("deprecation")
	public static void loadProperties() {
		File f = new File(appDir + "settings.properties");
		if(!f.exists()) {
			try {
				new File(appDir).mkdirs();
				f.createNewFile();
				propertiesDefaults.save(new FileOutputStream(f), "Image To Minecraft Settings");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			properties.load(new FileReader(f));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@SuppressWarnings("deprecation")
	public static void saveProperties() {
		try {
			properties.save(new FileOutputStream(new File(appDir + "settings.properties")), "Image To Minecraft Settings");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	

}
