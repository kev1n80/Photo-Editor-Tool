/*
 * Kevin Cam
 * Jared Chen
 * Assignment 5
 * 8/5/18
 */

import java.util.*;
import acm.graphics.*;

public class ImageShopAlgorithms implements ImageShopAlgorithmsInterface {

	//
	public GImage flipHorizontal(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		int[][] pixelsFlipped = new int[rows][columns];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int px = pixels[i][j];
				pixelsFlipped[i][columns-1-j] = px;
			}
		}
		source.setPixelArray(pixelsFlipped);
		return source;
	}
	
	//rotates image 90 degrees counterclockwise
	public GImage rotateLeft(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		int rowsRotated = columns;
		int columnsRotated = rows;
		int[][] pixelsRotated = new int[rowsRotated][columnsRotated];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int px = pixels[i][j];
				pixelsRotated[columns-1-j][i] = px;
			}
		}
		source.setPixelArray(pixelsRotated);
		return source;
	}

	//rotates image 90 degrees clockwise
	public GImage rotateRight(GImage source) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		int rowsRotated = columns;
		int columnsRotated = rows;
		int[][] pixelsRotated = new int[rowsRotated][columnsRotated];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int px = pixels[i][j];
				pixelsRotated[j][rows-1-i] = px;
			}
		}
		source.setPixelArray(pixelsRotated);
		return source;
	}

	public GImage greenScreen(GImage source) {
		int [][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int px = pixels[i][j];
				
				int red = GImage.getRed(px);
				int green = GImage.getGreen(px);
				int blue = GImage.getBlue(px);
				
				int bigger = Math.max(red, blue);
				
				//checks if pixel can be considered green.
				if (2 * bigger < green) {
					
					//makes pixel completely transparent.
					pixels[i][j] = GImage.createRGBPixel(1, 1, 1, 0);
				}
			}
		}
		source.setPixelArray(pixels);
		return source;
	}

	public GImage equalize(GImage source) {
		
		//histogram array of pixel luminosity.
		int [] hist1 = lumiHisto(source); 
		
		//histogram of array of total pixels with the same or lower luminosity.
		int [] hist2 = cumHisto(hist1);    
										  
		//new image created after equalize effect.
		GImage lumPixel = lumiPixel (source, hist2);
		return lumPixel;
	}
	
	private int[] lumiHisto (GImage source) {
		
		//array of image w/ pixels
		int [][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		
		//array which counts the amount of pixels that have a certain luminosity.
		int [] histo = new int [256];
		
		//goes through every pixel 
		for (int i = 0; i < rows; i ++) {
			for (int j = 0; j < columns; j++) {
				
				//get pixel r,g,b
				int px = pixels[i][j];
				int red = GImage.getRed(px);
				int green = GImage.getGreen(px);
				int blue = GImage.getBlue(px);
				
				//calculate luminosity
				int luminosity = computeLuminosity(red, green, blue);
				
				//adds one everytime 
				histo[luminosity]++;
			}
		}
		//returns the array histo with luminosity tally
		return histo;
	}
	
	private int[] cumHisto (int[] lumiHisto) {
		//new array with the cumulative values
		int columns = 256;
		int[] cumuHisto = new int[columns];
		
		//old array w/ info we need
		int[] lumHisto = lumiHisto;
		
		//goes through the whole array to change its value
		//starts from luminosity = 0 to luminosity = 255
		for (int j = 0; j < columns; j++) {
			
			//gets the # of pixels with j (luminsoty), 
			int numPixels = lumHisto[j];
			
			//if j-1 < 0 there is no number to get, because there is no index < 0
			if (j - 1 < 0) {
				
				//new array at index j (luminosity), there is nothing in previous index
				cumuHisto[j] = numPixels;
			}
			else {
				//add # of pixels with index j (luminosity), with # of pixels from previous index  
				cumuHisto[j] = numPixels + cumuHisto[j-1]; 
			}
		}
		return cumuHisto;
	}
	
	private GImage lumiPixel (GImage source, int[] cumHisto) {
		int [][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		int [][] lumPixels = new int[rows][columns];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int px = pixels[i][j];
				
				int red = GImage.getRed(px);
				int green = GImage.getGreen(px);
				int blue = GImage.getBlue(px);
				int luminosity = computeLuminosity(red, green, blue);
				
				int lumi = (int)(255 * ((double)(cumHisto[luminosity])/cumHisto[255]));
				
				lumPixels[i][j] = GImage.createRGBPixel(lumi, lumi, lumi);
			}
		}
		source.setPixelArray(lumPixels);
		return source;
	}

	//changes the colors of an image to create a "negative" effect.
	public GImage negative(GImage source) {
		int [][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int px = pixels[i][j];
				
				int red = GImage.getRed(px);
				int green = GImage.getGreen(px);
				int blue = GImage.getBlue(px);
				
				//makes new "negative/inverted" colors.
				int ired = 255 - red;
				int igreen = 255 - green;
				int iblue = 255 - blue;
				
				pixels[i][j] = GImage.createRGBPixel(ired, igreen, iblue);
			}
		}
		source.setPixelArray(pixels);
		return source;
	}

	//moves the image dx/dy. 
	public GImage translate(GImage source, int dx, int dy) {
		int[][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		int finalx;
		int finaly;
		int[][] pixelsTranslated = new int[rows][columns];
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int px = pixels[i][j];
				
				//depending on certain cases, uses different algorithms.
				if (dy >= 0) {
					finaly = (i + dy) % rows;
				} else {
					finaly = (i + rows + dy) % rows; 
				}
				if (dx >= 0) {
					finalx = (j + dx) % columns;
				} else {
					finalx = (j + columns + dx) % columns; 
				}
				
				//moves pixel to new location.
				pixelsTranslated[finaly][finalx] = px;
				}		
			}
		source.setPixelArray(pixelsTranslated);
		return source;
		}
		
	//blurs image.
	public GImage blur(GImage source) {
		int [][] pixels = source.getPixelArray();
		int rows = pixels.length;
		int columns = pixels[0].length;
		
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int sumRed = 0;
				int sumGreen = 0;
				int sumBlue = 0;
				int n = 0;
				
				//makes an array of different values, depending on the 'special case'
				int[] situations = determine(i, j, rows, columns);
				
				//gives the pixel a new value, with the blur effect.
				pixels[i][j] = blur(pixels, i, j, situations, sumRed, sumGreen, sumBlue, n);
			}
		}
		source.setPixelArray(pixels);
		return source;
	}

		//Averages the pixel colors with the pixel and the pixels around it. 
		private int blur (int[][] pixels, int i, int j, int[] determine, int sumRed,
				int sumGreen, int sumBlue, int n) {
			
			//depending on the array, gives the variables for different situations.
			int minRow = determine[0];
			int maxRow = determine[1];
			int minColumn = determine[2];
			int maxColumn = determine[3];
			
			//gets the sum red, green, and blue to make the new blurred pixel.
			for (int k = i - minRow; k <= i + maxRow; k++ ) {
				for (int l = j - minColumn; l <= j + maxColumn; l++) {
					int pixel = pixels[k][l];
					int surroundingRed = GImage.getRed(pixel);
					int surroundingGreen = GImage.getGreen(pixel);
					int surroundingBlue = GImage.getBlue(pixel);
					sumRed += surroundingRed;
					sumGreen += surroundingGreen;
					sumBlue += surroundingBlue;
					n = n +1;
				}
			}
			int pixel = pixels[i][j] = GImage.createRGBPixel(sumRed/n, sumGreen/n, sumBlue/n);
			return pixel;
		}
		
		//gives different values, depending on the 'special case'
		private int[] determine(int i, int j, int rows, int columns) {
			int[] position = new int[4];
			for (int k = 0; k < 4; k++) {
				position[k] = 1;
			}
			//all top have the case where i == 0
			if (i == 0) {
					//top left
				if ( j == 0 ) {
					position [0] = 0;
					position [2] = 0;
					//top right
				} else if (j == columns - 1) {
					position[0] = 0;
					position[3] = 0;
					//top
				} else if (j != 0 && j != columns - 1) {
					position[0] = 0;
				}
			}
			//all bottom have the case where i == rows - 1
			if (i == rows - 1) {
					//bottom left
				if ( j == 0 ) {
					position [1] = 0;
					position [2] = 0;
					//bottom right
				} else if (j == columns - 1) {
					position[1] = 0;
					position[3] = 0;
					//bottom
				} else if (j != 0 && j != columns - 1) {
					position[1] = 0;
				}
			}
			//left and right have the case where i != 0
			if (i != 0 && i != rows - 1) {
					// left
				if (j == 0 ) {
					position [2] = 0;
					// right
				} else if (j == columns - 1) {
					position[3] = 0;
				}
			}
			return position;
		}

		
}
