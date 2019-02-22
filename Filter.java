import java.awt.image.*;
import javax.imageio.*;
import java.io.*;

public class Filter {
	public static BufferedImage loadImg() { //loads in image from file
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("test.png"));
		}
		catch(IOException e) {
		}
		
		return img;
	}
	
	public static int sum(int[][] filter) {
		int sum = 0;
		for(int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				sum += filter[i][j];
			}
		}
		return sum;
	}
	
	public static void main(String args[]) {
		final int[][] filter = {{1,1,1},
								{1,1,1},
								{1,1,1}}; //this gives blur filter
		
		BufferedImage img = loadImg();
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		int[][] pixels = new int[width][height];
		int[][] newpixels = new int[width+2][height+2]; //larger array for operations
		
		//make 1 pixel buffer
		for(int i = 0; i < width+2; i++) {
			for (int j = 0; j < height+2; j++) {
				newpixels[i][j] = 0;
			}
		}
				
		int temp;
		//put pixel value 0-255 into array
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				temp = (img.getRGB(i,j) >> 16) & 0xff; //only use 1 of rgb because in grayscale rgb is the same value
				pixels[i][j] = temp;
			}
		}
		
		//add back original image to buffered array
		for(int i = 0; i < width; i++) {
			for(int j = 0;j < height; j++) {
				newpixels[i+1][j+1] = pixels[i][j];
			}
		}
		
		//Make new Image using convolutions
		for(int i = 0; i < width; i++) {
			for(int j = 0; j < height; j++) {
				pixels[i][j] = (newpixels[i][j] * filter[0][0] + newpixels[i+1][j] * filter[0][1] + newpixels[i+2][j] * filter[0][2]
						+ newpixels[i][j+1] * filter[1][0] + newpixels[i+1][j+1] * filter[1][1] + newpixels[i+2][j+1] * filter[1][2]
						+ newpixels[i][j+2] * filter[2][0] + newpixels[i+1][j+2] * filter[2][1] + newpixels[i+2][j+2] * filter[2][2]) / sum(filter); //divide by sum of filter to get blur
			}
		}
				
		for(int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {		
				temp = (255<<24) | (pixels[i][j]<<16) | (pixels[i][j]<<8) | pixels[i][j]; //make complete pixel value with alpha 255
			    img.setRGB(i, j, temp); //insert pixel back into new image
			}
		}
		
	    //write image
	    try{
	    	File f = new File("FilterOutput.png");
	    	ImageIO.write(img, "png", f);
	    }catch(IOException e){
	    	System.out.println(e);
	    }
	}
}
