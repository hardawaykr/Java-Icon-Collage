import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.JOptionPane;


public class PictureCollage {
	private static int blockSize = 1; // Static instance variable that determines the width of the block of pixels that will correspond
	 								  // to one sub image in the final picture.	
	public static void main(String args[]) {
		BufferedImage img = getImage(); // gets the image to be converted
		ArrayList<BufferedImage> imageList = processImages(); // gets the sub images to be implemented 
		int[][] intensity = getIntensity(img); // gets the average intensity of each block of pixels in img
		int[] intensitySubImages = getIntensitySubImages(imageList); // gets the average intensity of each sub image
		writeImage(img, imageList, intensity, intensitySubImages); // creates the image 
	}
	
	// Method to return the index of sub image with intensity value within 30 of the value of the pixel.
	public static int findIndex(int[] list, int target) {
		
		ArrayList<Integer> indexList = new ArrayList<Integer>();
		
		int x = 0;
		boolean check = true;
		
		while(check) {
			x++;
			for (int i = 0; i < list.length; i++) {
				if (list[i] >= target - x && list[i] <= target + x) {
					indexList.add(i);
					check = false;
				}
			}
				
			if (x >= 40 && check == true) {
				int ranIndex = (int)(Math.random() * (list.length));
				indexList.add(list[ranIndex]);
				break;
			}
				
		}
		return indexList.get(0); // returns random sub image if no images within 30 are found
	}
	
	// Method to return an ArrayList of images from a user specified library.
	public static ArrayList<BufferedImage> processImages() {
		ArrayList<BufferedImage> imageList = new ArrayList<BufferedImage>();
		String name = chooseSubImages(); 
		int x = 1;
		boolean check = true;
		
		while(check) {
			name = name.replaceFirst(Integer.toString(x - 1), Integer.toString(x)); // replaces previous name with a name incremented by one
			try {
				imageList.add(ImageIO.read(new File(name)));
				x++;
			} catch(IOException e) {
				check = false; // terminates loop if no image is found 
			}
		}
		return imageList;
	}
	
	// Prompts the user through JFileChooser to pick an image on the computer to convert.
	public static String choosePicture() {
		JFileChooser chooser = new JFileChooser(); // instantiates JFileChooser object
		
		FileNameExtensionFilter filter = new FileNameExtensionFilter("jpg, gif, png", "jpg", "gif", "png"); //limits user to only shown extensions
		
		chooser.setFileFilter(filter);
		chooser.setDialogTitle("Pick the image you want to convert."); // title of JFileChooser window
		int returnVal = chooser.showOpenDialog(chooser);
		
		String defaultImagePath = "/Users/Keith/Pictures/raven.png"; 
		
		if (returnVal == JFileChooser.APPROVE_OPTION) { // if acceptable file is chosen shows user what file they picked in JOptionPane window
			JOptionPane.showMessageDialog(null,"You opened this file: " + chooser.getSelectedFile().getPath());
			return chooser.getSelectedFile().getPath();
		}
		JOptionPane.showMessageDialog(null, "Opened default image: " + defaultImagePath);
		return defaultImagePath;
	}

	// Prompts the user through JFileChooser to pick a library of images to make the final image out of.
	public static String chooseSubImages() {
		JFileChooser chooser = new JFileChooser(); // instantiates JFileChooser object
		
		chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);// allows user to pick folder
		chooser.setDialogTitle("Pick the library of sub images."); // informs user of what to pick
		
		JOptionPane.showMessageDialog(null, "Pick the library of sub images. \n(Images must be named in sequential order starting at 1.gif. The one included will work.)");

		int returnVal = chooser.showDialog(chooser, "select");
		String folderPath = "/Users/Keith/Pictures/Icons/1.gif"; // default path of folder if nothing is selected
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			JOptionPane.showMessageDialog(null, "You opened this library: " + chooser.getSelectedFile().getPath()); // informs user which library was opened
			folderPath = chooser.getSelectedFile().getPath() + "/1.gif";
		}
		
		else {
			JOptionPane.showMessageDialog(null, "Opened default library: " + folderPath);
		}
		
		return folderPath;
	}
	
	// Returns an array with the intensity of every pixel in the primary image.
	public static int[][] getIntensity(BufferedImage img) {
		int[][] intensity = new int [img.getHeight() / blockSize][img.getWidth() / blockSize]; // Instantiates array for image intensities
		int k = 0;
		
		// algorithm that finds average pixel intensities for all pixels within the specified block
		for (int j = 0; j < img.getHeight() - (img.getHeight() % blockSize); j++) {
			int f = 0;
			for (int i = 0; i < img.getWidth()  - (img.getWidth() % blockSize); i++) {
				if (i % blockSize == 0 && i != 0) 
					f++;
				
				int rgbColor = img.getRGB(i, j);
				Color c = new Color(rgbColor);
				int r = c.getRed();
				int g = c.getGreen();
				int b = c.getBlue();
				
				intensity[k][f] += Math.round(0.21 * r + 0.71 * g + 0.07 * b);
				
			}
		
			if (j % blockSize == 0 && j != 0) 
				k++;
		}
		
		for (int i = 0; i < intensity.length; i++) {
			for (int j = 0; j < intensity[0].length; j++) {
				intensity[i][j] = intensity[i][j] / (blockSize * blockSize);
			}
		}
		
		return intensity;
	}
	
	// Returns an array with the average intensity of each sub image.
	public static int[] getIntensitySubImages(ArrayList<BufferedImage> imageList) {
		int[] intensitySubImages = new int[imageList.size()];
		int numPixels = imageList.get(0).getHeight() * imageList.get(0).getWidth();
		
		// finds average intensity of each image in the array imageList
		for (int t = 0; t < imageList.size(); t++) {
			BufferedImage currentImage = imageList.get(t);
			for (int j = 0; j < currentImage.getHeight(); j++) {
				for (int i = 0; i < currentImage.getWidth(); i++) {
					int rgbColor = currentImage.getRGB(i, j);
					Color c = new Color(rgbColor);
					int r = c.getRed();
					int g = c.getGreen();
					int b = c.getBlue();
					
					intensitySubImages[t] += Math.round(0.21 * r + 0.71 * g + 0.07 * b);
				}
				
			}
			intensitySubImages[t] = intensitySubImages[t] / numPixels;
		}
		return intensitySubImages;
	}
	
	// Prompts the user to choose an image and stores that image as a BufferedImage.
	public static BufferedImage getImage() {
		BufferedImage img = null;
		try {
			String fileName = choosePicture(); // accesses method to browse for image and returns the file name
			img = ImageIO.read(new File(fileName)); // makes bufferedImage object for chosen image
			
			try { 
				blockSize = Integer.parseInt(JOptionPane.showInputDialog(null,"Enter width of block of pixels that will correspond to each sub image: " 
									+ "\n(Usually around 10 for 1920 X 1080, less is more accurate but can overflow heap.)"));
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(null,"Default block size " + blockSize); 
			}
	
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,"File not found.");	
		}
		return img;
	}

	// Compiles the image to be output at collage.png with the appropriate sub image at each location.
	public static void writeImage(BufferedImage img, ArrayList<BufferedImage> imageList, int[][] intensity, int[] intensitySubImages) {
		int w = (img.getWidth() / blockSize) * imageList.get(0).getWidth(); // width of final image is number of subunits * width of sub unit
		int h = (img.getHeight() / blockSize) * imageList.get(0).getHeight(); // height of final image is number of subunits * height of sub unit
		
		BufferedImage off_Image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB); // creates final image of correct size and type
		Graphics g = off_Image.createGraphics(); // graphics object to add images to final image
		int r = 0;
		int c = 0;
		
		// draws each image at the correct position in the final image
		for (int i = 0; i < intensity.length; i++) {
			c = 0;
			for (int j = 0; j < intensity[0].length; j++) {
				g.drawImage(imageList.get(findIndex(intensitySubImages, intensity[i][j] )),c, r + 1, null);// adds subunits at correct position in the final image
				c += imageList.get(0).getWidth(); // increments width along final image so next image is placed next to and not on top of previous image
				
			}
			r += imageList.get(0).getHeight(); // increments height by the correct number of pixels to place next image in the next row
			
		}
		
		try {
			File outputfile = new File("collage.png");
			ImageIO.write(off_Image, "png", outputfile);
		} catch(IOException e) {
			System.out.print("error");
		}
		g.dispose(); 
	}
}