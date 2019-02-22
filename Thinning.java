import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Thinning {
	public static BufferedImage loadImg() { //loads in image from file
		BufferedImage img = null;
		try {
			img = ImageIO.read(new File("three.png"));
		}
		catch(IOException e) {
		}
		
		return img;
	}
	public static void main(String args[]) {
		BufferedImage img = loadImg();
		
		int width = img.getWidth();
		int height = img.getHeight();
		
		int[][] pixels = new int[width][height];
		int[][] btow = new int[width][height]; //black to white array
		int temp;
		//put pixel value 0-255 into array
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				temp = (img.getRGB(i,j) >> 16) & 0xff; //only use 1 of rgb because in grayscale rgb is the same value
				if(temp > 127) {
					pixels[i][j] = 255; //255 is white
				}else {
					pixels[i][j] = 0; //0 is black
				}
				btow[i][j] = 0;
			}
		}
		
				int bp1 = 0;
				int ap1 = 0;
				int p246 = 0;
				int p468 = 0;
				int p248 = 0;
				int p268 = 0;
				int a = 0;
				int change = 0;
				
				while(bp1 != -1 || ap1 != -1 || p246 != -1 || p468 != -1){
					for (int i = 1; i < width-1; i++) {
						for (int j = 1; j < height-1; j++) { //step one, check if neighbour black or white
							if(pixels[i][j] == 0){//if pixel is black 
								if(pixels[i-1][j-1] == 0) bp1 += 1; //p9
								
								if(pixels[i-1][j] == 0) bp1 += 1; //p2
								else { 
									p246 += 1;
									p248 += 1;
									p268 += 1;
								}
								if(pixels[i-1][j+1] == 0) bp1 += 1; //p3
								
								if(pixels[i][j-1] == 0) bp1 += 1; //p8
								else {
									p468 += 1;
									p248 += 1;
									p268 += 1;
								}
								if(pixels[i][j+1] == 0) bp1 += 1; //p4
								else {
									p246 += 1;
									p468 += 1;
									p248 += 1;
								}
								if(pixels[i+1][j-1] == 0) bp1 += 1; //p7
								
								if(pixels[i+1][j] == 0) bp1 += 1; //p6
								else {
									p246 += 1;
									p468 += 1;
									p268 += 1;
								}
								
								if(pixels[i+1][j+1] == 0) bp1 += 1; //p5
								
								if(pixels[i-1][j] == 255 && pixels[i-1][j+1] == 0) a++;
								if(pixels[i-1][j+1] == 255 && pixels[i][j+1] == 0) a++;
								if(pixels[i][j+1] == 255 && pixels[i+1][j+1] == 0) a++;
								if(pixels[i+1][j+1] == 255 && pixels[i+1][j] == 0) a++;
								if(pixels[i+1][j] == 255 && pixels[i+1][j-1] == 0) a++;
								if(pixels[i+1][j-1] == 255 && pixels[i][j-1] == 0) a++;
								if(pixels[i][j-1] == 255 && pixels[i-1][j-1] == 0) a++;
								if(pixels[i-1][j-1] == 255 && pixels[i-1][j] == 0) a++;
								
								if(bp1 >= 2 && bp1 <=6 && p246 > 0 && p468 > 0 && a == 1){ //checks for step 1
									btow[i][j] = 1; //checks pixel to be changed to white
									change++;
								}
								
								if(bp1 >= 2 && bp1 <=6 && p248 > 0 && p268 > 0 && a == 1){ //checks for step 2
									btow[i][j] = 1; //checks pixel to be changed to white
									change++;
								}
								
								bp1 = 0;
								ap1 = 0;
								a = 0;
								p246 = 0;
								p468 = 0;
								p248 = 0;
								p268 = 0;
								
							}else{
//								newpixels[i][j] = pixels[i][j];
								bp1 = 0;
								ap1 = 0;
								a = 0;
								p246 = 0;
								p468 = 0;
								p248 = 0;
								p268 = 0;
							}
							
						}
						
					}
					for (int i = 0; i < width; i++) { //set black to white
						for (int j = 0; j < height; j++) {
							if(btow[i][j] == 1){
								pixels[i][j] = 255;
								btow[i][j] = 0;
							}
						}
					}
					if(change == 0){
						bp1 = -1;
						ap1 = -1;
						p246 = -1;
						p468 = -1;
					}
					change = 0;
				}

				
//				//add back original image to buffered array
//				for(int i = 0; i < width; i++) {
//					for(int j = 0;j < height; j++) {
//						pixels[i+1][j+1] = newpixels[i][j];
//					}
//				}
				
						
				for(int i = 0; i < width; i++) {
					for (int j = 0; j < height; j++) {		
						temp = (255<<24) | (pixels[i][j]<<16) | (pixels[i][j]<<8) | pixels[i][j]; //make complete pixel value with alpha 255
					    img.setRGB(i, j, temp); //insert pixel back into new image
					}
				}
				
			    //write image
			    try{
			    	File f = new File("ThinningOutput.png");
			    	ImageIO.write(img, "png", f);
			    }catch(IOException e){
			    	System.out.println(e);
			    }
	}
}
