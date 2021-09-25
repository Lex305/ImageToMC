package de.lexoland.image_to_mc.core;

import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CancellationException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.swing.JOptionPane;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.lexoland.image_to_mc.Main;


public class GifConverter {
	
	public static void saveGifAsFunctions(File gif, File saveDirectory, Rescale rescale, boolean controlableFrames, String workspaceName) {
		
		OPTIMIZE = Boolean.getBoolean(Main.properties.getProperty("optimize", "true"));
		CHARACTER = Main.properties.getProperty("character", "█").charAt(0);
		
		String datapackFunctionWorkspace = null;
		if(workspaceName == null) {
			datapackFunctionWorkspace = getMcFunctionPathParent(saveDirectory);
			if(datapackFunctionWorkspace == null)
				return;
		} else {
			datapackFunctionWorkspace = workspaceName + ":";
		}
		boolean doResize = rescale == null ? false : true;
		int width = 0;
		int height = 0;
		int masterWidth = 0;
		int masterHeight = 0;
		int armorStandCount = 0;
		if(doResize) {
			width = rescale.getWidth();
			height = rescale.getHeight();
		}
		File gifDir = new File(saveDirectory.getAbsolutePath() + "\\" + gif.getName());
		gifDir.mkdirs();
		File framesDir = new File(saveDirectory.getAbsolutePath() + "\\" + gif.getName() + "\\frames");
		framesDir.mkdirs();
		try {
			double lineSpacing = 0.225;
			Date start = new Date();
		    String[] imageatt = new String[]{
		            "imageLeftPosition",
		            "imageTopPosition",
		            "imageWidth",
		            "imageHeight"
		    };
		    ImageReader reader = (ImageReader) ImageIO.getImageReadersByFormatName("gif").next();
		    ImageInputStream ciis = ImageIO.createImageInputStream(gif);
		    reader.setInput(ciis, false);

		    int noi = reader.getNumImages(true);
		    int noi2 = reader.getNumImages(true) - 1;
		    BufferedImage master = null;
		    
		    //read delay
		    
		    IIOMetadata imageMetaData =  reader.getImageMetadata(0);
	        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

	        IIOMetadataNode root = (IIOMetadataNode)imageMetaData.getAsTree(metaFormatName);

	        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");

	        System.out.println(graphicsControlExtensionNode.getAttribute("delayTime"));
		    
		    //convert to function
	        Main.frame.convertDialog.setMaximum(noi - 1);
		    for (int i = 0; i < noi; i++) { 
		        try {
		        	BufferedImage image = reader.read(i);
		        	BufferedImage imageResized = null;
			        IIOMetadata metadata = reader.getImageMetadata(i);

			        Node tree = metadata.getAsTree("javax_imageio_gif_image_1.0");
			        NodeList children = tree.getChildNodes();

			        int y1 = 0;
			        int y2 = 0;
			        
			        boolean[] freeLines = null;
			        if(master != null)
			        	freeLines = new boolean[master.getHeight()];
			        
			        float rescaleValWidth = 0;
			        float rescaleValHeight = 0;
			        
			        for (int j = 0; j < children.getLength(); j++) {
			            Node nodeItem = children.item(j);
			            if(nodeItem.getNodeName().equals("ImageDescriptor")){
			                Map<String, Integer> imageAttr = new HashMap<String, Integer>();

			                for (int k = 0; k < imageatt.length; k++) {
			                    NamedNodeMap attr = nodeItem.getAttributes();
			                    Node attnode = attr.getNamedItem(imageatt[k]);
			                    imageAttr.put(imageatt[k], Integer.valueOf(attnode.getNodeValue()));
			                }
			                
			                if(i == 0) {
			                	masterWidth = imageAttr.get("imageWidth");
			                	masterHeight = imageAttr.get("imageHeight");
			                }
			                
			                rescaleValWidth = (float) width / (float) masterWidth;
			                rescaleValHeight = (float) height / (float) masterHeight;
			                int imageHeight = 0;
			                
			                if(i==0){
			                	if(doResize)
			                		master = new BufferedImage(
			                			masterWidth,
			                			masterHeight,
			                			BufferedImage.TYPE_INT_ARGB
			                		);
			                	else
			                		master = new BufferedImage(masterWidth, masterHeight, BufferedImage.TYPE_INT_ARGB);
			                	if(doResize)
			                		freeLines = new boolean[(int) Math.ceil(rescaleValHeight * image.getHeight())];
			                	else
			                		freeLines = new boolean[image.getHeight()];
			                }
			                
			                if(doResize) {
			                	imageHeight = (int) Math.ceil(rescaleValHeight * image.getHeight());
			                	BufferedImage newImg = new BufferedImage(
			                		(int) Math.ceil(rescaleValWidth * image.getWidth()),
			                		(int) Math.ceil(rescaleValHeight * image.getHeight()),
			                		BufferedImage.TYPE_INT_ARGB
			                	);
			                	Graphics g = newImg.getGraphics();
			                	g.drawImage(image, 0, 0,
			                		(int) Math.ceil(rescaleValWidth * image.getWidth()),
			                		(int) Math.ceil(rescaleValHeight * image.getHeight()),
			                		null
			                	);
			                	imageResized = newImg;
			                } else
			                	imageHeight = image.getHeight();
			                
							if(!controlableFrames) {
								for (int iy = 0; iy < imageResized.getHeight(); iy++) {
									boolean a = true;
									for (int ix = 0; ix < imageResized.getWidth(); ix++) {
										int alpha = (imageResized.getRGB(ix, iy) >> 24) & 0xFF;
										if (alpha != 0) {
											a = false;
										}
									}
									freeLines[iy] = a;
								}
							}
							
			                
							
							
			                int x = imageAttr.get("imageLeftPosition");
			                int y = imageAttr.get("imageTopPosition");
			                
			                if(doResize) {
			                	y1 = (int) Math.floor(rescaleValHeight * y);
								y2 = imageHeight;
								master.getGraphics().drawImage(image,
									x,
									y,
									null
								);
			                } else {
			                	y1 = y;
								y2 = imageHeight;
								master.getGraphics().drawImage(image, x, y, null);
			                }
			                
			            }
			        }
			        BufferedImage newImg = new BufferedImage(
                		(int) Math.ceil(rescaleValWidth * masterWidth),
                		(int) Math.ceil(rescaleValHeight * masterHeight),
                		BufferedImage.TYPE_INT_ARGB
                	);
                	Graphics g = newImg.getGraphics();
                	g.drawImage(master, 0, 0,
                		(int) Math.ceil(rescaleValWidth * masterWidth),
                		(int) Math.ceil(rescaleValHeight * masterHeight),
                		null
                	);
                	BufferedImage optimizedImage = null;
			        if(controlableFrames) {
			        	optimizedImage = newImg;
			        } else {
			        	optimizedImage = newImg.getSubimage(0, y1, newImg.getWidth(), y2);
			        }
			        armorStandCount = newImg.getHeight();
			        
			        String[] jsonFrame = gifFrameToJson(optimizedImage, freeLines);
			        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(framesDir.getAbsolutePath() + "/" + i + ".mcfunction")), StandardCharsets.UTF_8));
			        for(int ii = 0; ii < jsonFrame.length; ii++) {
			        	String s = jsonFrame[ii];
			        	if(s != null) {
			        		writer.write("execute as @e[type=minecraft:armor_stand,tag=" + gif.getName() + "-" + (ii + (controlableFrames ? 0 : y1)) + "] run data modify entity @s CustomName set value '" + s + "'");
			        		writer.newLine();
			        	}
			        }
			        writer.close();
			        Main.frame.convertDialog.setValue(i);
			        System.out.println(i + "/" + noi2 + " done!");
				} catch (IndexOutOfBoundsException e) {
					System.err.println(i + "/" + noi2 + " couldn't read frame!");
				} catch (IllegalArgumentException e) {
					System.err.println("Fatal error!");
					Toolkit.getDefaultToolkit().beep();
					JOptionPane.showMessageDialog(Main.frame, "Failed to convert Gif" + System.lineSeparator() + "Try using a SD Gif instead!", "Fatal Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
		    }
		    System.out.println("Creating api functions...");
		    File createFunction = new File(gifDir.getAbsolutePath() + "/create.mcfunction");
		    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(createFunction), StandardCharsets.UTF_8))) {
		    	writer.write("# ");
		    	writer.newLine();
		    	writer.write("# This will create a new hologram");
		    	writer.newLine();
		    	writer.write("# ");
		    	writer.newLine();
		    	double h = lineSpacing * armorStandCount;
				for(int y = 0; y < armorStandCount; y++) {
					writer.write("summon minecraft:armor_stand ~ ~" + (double) Math.round(h * 1000f) / 1000f + " ~ {Tags:[\"" + gif.getName() + "-" + y + "\"],CustomNameVisible:1b,NoGravity:1b,Small:1b,Marker:1b,Invisible:1b}");
					writer.newLine();
					h -= lineSpacing;
				}
				writer.write("scoreboard objectives add " + gif.getName() + " dummy");
				writer.newLine();
				writer.write("scoreboard players set frame " + gif.getName() + " 0");
				writer.newLine();
				writer.write("function " + datapackFunctionWorkspace + gif.getName() + "/frames/0");
			    writer.close();
		    }
		    File nextFrameFunction = new File(gifDir.getAbsolutePath() + "/next_frame.mcfunction");
		    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(nextFrameFunction), StandardCharsets.UTF_8))) {
		    	writer.write("# ");
		    	writer.newLine();
		    	writer.write("# This will jump to the next frame and will update the hologram");
		    	writer.newLine();
		    	writer.write("# ");
		    	writer.newLine();
		    	writer.write("scoreboard players add frame " + gif.getName() + " 1");
		    	writer.newLine();
		    	writer.write("execute if score frame " + gif.getName() + " matches " + noi + " run scoreboard players set frame " + gif.getName() + " 0");
		    	writer.newLine();
		    	if(controlableFrames) {
		    		writer.write("function " + datapackFunctionWorkspace + gif.getName() + "/update_frame");
		    		writer.newLine();
		    	} else {
		    		for(int i = 0; i < noi; i++) {
			    		writer.write("execute if score frame " + gif.getName() + " matches " + i + " run function " + datapackFunctionWorkspace + gif.getName() + "/frames/" + i);
			    		writer.newLine();
			    	}
		    	}
		    	writer.close();
		    }
		    if(controlableFrames) {
		    	File loadFrameFunction = new File(gifDir.getAbsolutePath() + "/update_frame.mcfunction");
			    try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(loadFrameFunction), StandardCharsets.UTF_8))) {
			    	writer.write("# ");
			    	writer.newLine();
			    	writer.write("# This updates the hologram");
			    	writer.newLine();
			    	writer.write("# ");
			    	writer.newLine();
			    	for(int i = 0; i < noi; i++) {
			    		writer.write("execute if score frame " + gif.getName() + " matches " + i + " run function " + datapackFunctionWorkspace + gif.getName() + "/frames/" + i);
			    		writer.newLine();
			    	}
			    	writer.close();
			    }
		    }
		    System.out.println("Done! " + (new Date().getTime() - start.getTime()) + "ms");
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	public static boolean OPTIMIZE = false;
	public static char CHARACTER = '█';
	
	public static String[] gifFrameToJson(BufferedImage image, boolean[] freeLines) {
		char c = CHARACTER;
		WritableRaster raster = image.getRaster();
		int height = image.getHeight();
		int width = image.getWidth();
		
		int[] pixel = new int[4];
		String[][] arr = new String[image.getHeight()][width];
		
		for (int y = 0; y < height; y++) {
			int i = 0;
			if(freeLines[y]) {
				i++;
				continue;
			}
			for (int x = 0; x < width; x++) {
				pixel = raster.getPixel(x, y, new int[4]);
				arr[y][i] = "{\"text\":\"" + c + "\",\"color\":\"" + String.format("#%02x%02x%02x", pixel[0], pixel[1], pixel[2]) + "\"}";
				i++;
			}
			if(OPTIMIZE)
				arr[y] = optimize(arr[y], c, width, height);
		}
		String[] j = new String[arr.length];
		for(int y = 0; y < arr.length; y++) {
			if(arr[y][0] == null)
				j[y] = null;
			else
				j[y] = Arrays.toString(arr[y]).replace(" ", "");
		}
		return j;
	}
	
	public static String[] optimize(String[] arr, char c, int width, int height) {
		String o = arr[0];
		String[] newArr = new String[height * width + height - 1];
		char[] chars = new char[width];
		int i = 0;
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
		return Arrays.copyOf(newArr, i);
	}
	
	public static String getMcFunctionPathParent(File f) {
		String[] dirs = f.getAbsolutePath().replace(File.separator, "/").split("/");
		boolean dataDirFound = false;
		boolean functionDirFound = false;
		String workspaceName = null;
		for (String s : dirs) {
			if (s.equals("data") && !dataDirFound) {
				dataDirFound = true;
				continue;
			}
			if (dataDirFound && workspaceName == null) {
				workspaceName = s;
				continue;
			}
			if (s.equals("functions") && workspaceName != null && !functionDirFound) {
				functionDirFound = true;
				workspaceName += ":";
				continue;
			}
			if (functionDirFound) {
				workspaceName += s + "/";
				continue;
			}
		}
		if (!functionDirFound) {
			Toolkit.getDefaultToolkit().beep();
			JOptionPane.showMessageDialog(Main.frame, "Your selected directory is not a part of a datapack!", "Error",
				JOptionPane.ERROR_MESSAGE);
		}
		return workspaceName;
		
		
	}
	
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)== 0) {
            return((IIOMetadataNode) rootNode.item(i));
            }
       }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return(node);
  }
	
	
	
}
