/**	
 *	a parallel mean filter program for smoothing RGB colourimages
 *
 *@author Thabang Thubane
 *@version 13 August 2022
 */

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;

public class MeanFilterParallel {
    static long startTime = 0;

    /**
	 *a void method to set startTime to the current time
	 *
	 */
    private static void tick(){
		startTime = System.currentTimeMillis();
	}
    
     /**
	 *a method to return run time (time passed) after calling tick()
	 *
	 */
    private static float tock(){
		return (System.currentTimeMillis() - startTime) / 1000.0f; 
	}

    /**
	 *a main method to run the image filtering process
     * gets command line parameters(inOrder):\n<inputImageName> <outputImageName> <windowWidth>
	 *  sets the required argument for the SubImage comnstructor then invokes a gforkJoin pool
	 *   calls tick() when the pool is invoke and tock() once the pool finishes to record the running time of the program
     * 
	 */
    public static void main(String args[])throws Throwable{

        int numOfArgs = args.length;

        if(numOfArgs == 3) {
            int temp = Integer.parseInt(args[2]);// window size
            if((temp < 3) | (temp % 2 == 0)) {
                System.out.println("width of the square filter window must be an odd positive integer >=3");
                System.exit(0);
            }

        }else{
            System.out.println("Requires the following command-line parameters(inOrder):\n<inputImageName> <outputImageName> <windowWidth>");
            System.exit(0);
        }

        File buffImageFile;
        File dupBuffImageFile;
        File outputImageFile = null;// Output image

        BufferedImage image = null; // Image used to get neighbouring pixels
        BufferedImage imageFilt = null; // The filtered image or image being filtered (set the mean or median pixel)

        try{
            //read images from "images" folder
            buffImageFile = new File("./images/"+args[0]);
            dupBuffImageFile = new File("./images/"+args[0]);
            outputImageFile = new File("./filtered/"+args[1]);

            image = ImageIO.read(buffImageFile);
            imageFilt = ImageIO.read(dupBuffImageFile);

        }catch(IOException e){
            System.out.println("Files couldn't be openned. Terminating....");
            System.exit(0);
        }
        int filtHeight = (image.getHeight() - Integer.parseInt(args[2]));// where the sliding window should stop
        
        MeanFilterParallel.tick();
        ForkJoinPool pool = new ForkJoinPool();
        MeanSubImage task = new MeanSubImage(filtHeight,Integer.parseInt(args[2]),0,image.getWidth(),image,imageFilt);
        pool.invoke(task);
        
        float time = MeanFilterParallel.tock();
        System.out.println("Total MeanFilterParallel run time: "+ time +" secs");
        ImageIO.write(task.getImage(),"png",outputImageFile);
        
    }
    
}

/**
 * inner class to perfom the divide and conquer algorithm
 * extends the RecursiveAction class to be able to use Fork Join parallelization
 * 
 */
class MeanSubImage extends RecursiveAction{

    private int filtHeight;
    private int winSize;
    private int startWidth;
    private int endWidth;
    private BufferedImage inImg;// image to iterate through and get pixels
    private BufferedImage outImg;// image set the filtered pixel

    private int[] reds; // store the red bits of the pixels
    private int[] greens;//  "   "   blue bits " "  "
    private int[] blues; // "  "    green " " " "

    /**
     * constructor for the MeanSubImage class
     * splits a given inImg according to the given start and endWidth var
     * @param filtHeight -where the sliding window should stop
     * @param winSIze - filter square window size
     * @param startWidth - the start position for the part of the image
     * @param endWidth - the end position for the part of the image
     * @param inImg - the image used to get the pixels to be filtered while outImg is the image where the filtered oixels are set
     * 
     */
    public MeanSubImage(int filtHeight, int winSize, int startWidth, int endWidth, BufferedImage inImg, BufferedImage outImg){
        this.winSize = winSize;

        /**
         * section to check where this part fits in the original image 
         * and compensate for the edges acordingly
         * 
         */
        if(endWidth> inImg.getWidth()){
            this.endWidth = inImg.getWidth();
        }
        else{
            this.endWidth = endWidth;
        }
        if(startWidth < 0) {
            this.startWidth = 0;
        }
        else{
            this.startWidth = startWidth;
        }
        
        this.inImg = inImg;
        this.outImg = outImg;
        this.filtHeight = filtHeight;
        this.reds = new int[winSize*winSize];
        this.greens = new int[winSize*winSize];
        this.blues = new int[winSize*winSize];
    }

    /**
     * gets the filtered image
     * 
     */
    public BufferedImage getImage() {

        return this.outImg;
    }
   
    /**
     * calculates the mean 
     * @param an array 0f integers
     * 
     */
    public int mean(int[] args) {
      
        return ((int)Math.round(IntStream.of(args).average().getAsDouble()));
    }

    @Override
    protected void compute(){

        if((endWidth - startWidth) <= 300) {

            List<Color> colors;

            //the mean values for each color array
            int red;
            int green;
            int blue;

            /**
            * section to check where this part fits in the original image 
            * and filter the edges acordingly
            * 
            */
            int temp = winSize/2;
            if((endWidth) < temp) {
                return;
            }
            if((inImg.getWidth() - startWidth) < temp){
                return;
            }
            int filtWidth = endWidth - winSize;
            for (int yCord = 0; yCord < this.filtHeight; yCord++) {//iterate through the height
                for(int xCord = startWidth; xCord < filtWidth; xCord++) {// iterate through the width
                    colors = new ArrayList<Color>();// store pixels as color objects
    
                    // iteraation for the sliding square window
                    for(int row = 0; row< this.winSize; row++) {
                        for(int col = 0; col<this.winSize; col++){
                            colors.add(new Color(this.inImg.getRGB(xCord+col ,yCord + row)));
                        }
                    }
      
      
                    for (int z = 0; z< (this.winSize*this.winSize); z++){
                        this.reds[z] = colors.get(z).getRed();
                        this.greens[z] = colors.get(z).getGreen();
                        this.blues[z] = colors.get(z).getBlue();
      
                    }
    
                    int mid = this.winSize/2;// to locate the center of the sliding window
      
                    red = this.mean(this.reds);
                    green = this.mean(this.greens);
                    blue = this.mean(this.blues);
      
                    this.outImg.setRGB(xCord+mid, yCord+mid,new Color(red,green,blue).getRGB());
                }
            }
            
        }
        else{
            
            int mid = startWidth + (endWidth - startWidth) / 2;// cut/split the width
            int t = winSize/2;// var to compensate for the edges
            
            MeanSubImage left = new MeanSubImage(filtHeight, winSize, startWidth-t, mid+(2*t), inImg, outImg);
            MeanSubImage right = new MeanSubImage(filtHeight, winSize, mid-t, endWidth+(2*t), inImg, outImg);
            ForkJoinTask.invokeAll(left,right);
        }



    }
}


