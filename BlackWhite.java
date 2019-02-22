import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class BlackWhite {
	public static BufferedImage loadImg() { //loads in image from file
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("apple.png"));
		}
		catch(IOException e) {
		}
		
		return img;
	}
	
	public static boolean checkPixel(int p[][], int x, int y, int height) { 
		//Return is a pixel is connected or not
		boolean connected = false;
		if(x == 0 && y == 0) { //if at the top left corner it is default not connected
			return connected;
		}else if (x == 0 && y != 0) {
			if(p[x][y-1] != 0) {
				connected = true;
			}
		}else if(x != 0 && y == 0) {
			if(p[x-1][y] != 0 || p[x-1][y+1] != 0) {
				connected = true;
			}
		}else if(y == height - 1){ 
			if(p[x-1][y-1] != 0 || p[x-1][y] != 0 || p[x][y-1] != 0) {
				connected = true;
			}
		}else {
			if(p[x-1][y-1] != 0 || p[x-1][y] != 0 || p[x][y-1] != 0 || p[x-1][y+1] != 0) {
				connected = true;
			}
		}
		return connected;
	}
	
	public static int checkGroup(int p[][], int x, int y, int height) { //assumes pixel is connected; check which group a pixel should belong to
		int group = 0;
		if(x == 0 && y == 0 && p[x][y] != 0) { //if at the top left corner it is default not connected
			group = 1;
		}else if (x == 0) {
			if(p[x][y-1] != 0) {
				group = p[x][y-1];
			}
		}else if(y == 0) {
			if(p[x-1][y] != 0) {
				group = p[x-1][y];
			}else if(p[x-1][y+1] != 0) {
				group = p[x-1][y+1];
			}
		}else if(y == height - 1) { //if hugging right side of image don't search upper right
			if(p[x-1][y-1] != 0){
				group = p[x-1][y-1];
			}else if(p[x-1][y] != 0) {
				group = p[x-1][y];
			}else if (p[x][y-1] != 0) {
				group = p[x][y-1];
			}
		}else { //search all other directions
			if(p[x-1][y-1] != 0){
				group = p[x-1][y-1];
			}else if(p[x-1][y] != 0) {
				group = p[x-1][y];
			}else if(p[x-1][y+1] != 0) {
				group = p[x-1][y+1];
			}else if (p[x][y-1] != 0) {
				group = p[x][y-1];
			}
		}
		return group;
	}
	
	public static int countGroup(int[][] p, int width, int height, int group) { //Counts number pixels in each group according to the group array
		int counter = 0;
		for(int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if(p[i][j] == group) {
					counter++;
				}
			}
		}
		return counter;
	}
	
	public static void replaceAllPixels(int[][] p, int width, int height, int replaceThis, int replacement) { //replaces all of one entry with another
		for(int i = 0; i < height; i++) {
			for(int j = 0; j < width; j++){
				if(p[i][j] == replaceThis) {
					p[i][j] = replacement;
				}
			}
		}
	}
	
	public static int[][] getComponents(int[][] pixels, int height, int width) { //component separating code
		int[][] pixelGroups = new int[height][width];
		
		//Set initial to 0;
		for(int i = 0; i < height; i++) {
			for(int j = 0 ; j < width; j++) {
				pixelGroups[i][j] = 0;
			}
		}
		
		int groupCounter = 1; //count black
		for(int i = 0; i < height; i++){
			for(int j = 0; j < width; j++) {
				if (pixels[i][j] == 0) { //if black
					if(checkPixel(pixelGroups, i, j, width)) { //if connected
						pixelGroups[i][j] = checkGroup(pixelGroups, i , j, width);
					}else {
						//System.out.println(i + " " + j);
						pixelGroups[i][j] = groupCounter;
						groupCounter++;
					}
				}
			}
		}
				
		//Clean up the conflicting groups		
		for(int i = height-1; i > 0; i--) {
			for(int j = width-1; j > 1; j--) {
					if(((pixelGroups[i][j-1] > pixelGroups[i][j]) || pixelGroups[i][j-1] < pixelGroups[i][j]) && pixelGroups[i][j-1] != 0 && pixelGroups[i][j] != 0) { //if left number > current number
						//System.out.println("replaced " + pixelGroups[i][j-1] + " with " + pixelGroups[i][j]);
						replaceAllPixels(pixelGroups, width, height, pixelGroups[i][j-1], pixelGroups[i][j]); //replace that number to the left and anything connected to it
					}
			}
		}
		
		
		int[] groups = new int[groupCounter];
		for(int i = 0; i < groupCounter; i++) {
			groups[i] = countGroup(pixelGroups, width, height, i); //Print how many pixels are in each group
		}
		
		//replace group numbers so that they are consecutive
		int lastEmpty = -1;
		int index = 0;
		
		while(index < groupCounter) {
			if(groups[index] == 0 && lastEmpty == -1) {
				//System.out.println("Last Empty on " + index);
				lastEmpty = index;
			}
			else if(lastEmpty != -1 && groups[index] != 0) {
				//System.out.println("Replacement of " + index + " with " + lastEmpty);
				replaceAllPixels(pixelGroups, width, height, index, lastEmpty);
				groups[lastEmpty] = groups[index];
				groups[index] = 0;
				index = lastEmpty;
				//System.out.println("Index is on " + index);
				lastEmpty = -1;
			}
			//System.out.println("Increment");
			index++;
		}
		
		//print out the groupings
		for(int i = 1; i < groupCounter; i++) {
			if(groups[i] != 0) {
				System.out.println("Group " + i + " has " + groups[i] + " pixels");
			}
		}
		
		
		//prints out grouping array
//		for(int i = 0; i < height; i++) {
//			for(int j = 0; j < width; j++) {
//				System.out.print(pixelGroups[i][j] + " ");
//			}
//			System.out.println("");
//		}
		
		return pixelGroups;
	}
	
	
	public static void main(String args[]) {
		BufferedImage img = loadImg();
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		int[][] pixels = new int[height][width];
						
		int temp;
		//put pixel value 0-255 into array
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				temp = (img.getRGB(j,i) >> 16) & 0xff; //only use 1 of rgb because in grayscale rgb is the same value
				if(temp > 127) {
					pixels[i][j] = 255; //255 is white
				}else {
					pixels[i][j] = 0; //0 is black
				}
			}
		}
				
		for(int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {		
				temp = (255<<24) | (pixels[i][j]<<16) | (pixels[i][j]<<8) | pixels[i][j]; //make complete pixel value with alpha 255
			    img.setRGB(j, i, temp); //insert pixel back into new image
			}
		}

		//Print pixel groupings
//		for(int i = 0; i < height; i++) {
//			for(int j = 0; j < width; j++) {
//				System.out.print(pixels[i][j] + " ");
//			}
//			System.out.println("");
//		}
		
	    //write image
	    try{
	    	File f = new File("BWOutput.png");
	    	ImageIO.write(img, "png", f);
	    }catch(IOException e){
	    	System.out.println(e);
	    }
	    
	    getComponents(pixels, height, width);
	    
	}
}
