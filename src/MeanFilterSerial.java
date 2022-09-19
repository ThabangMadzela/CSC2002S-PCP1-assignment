import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.*;
import java.util.stream.IntStream;

public class MeanFilterSerial {

    static long startTime = 0;
    private int[] reds; // store the red bits of the pixels
    private int[] greens;//  "   "   blue bits " "  "
    private int[] blues; // "  "    green " " " "
    private int windowWidth;
    private int filtHeiht;
    private int filtWidth;
    private BufferedImage image;
    private BufferedImage imageFilt;

     /**
     * constructor for the MeanFilterSerial class
     * 
     * @param windowWidth - filter square window size
     * @param image - the image used to get the pixels to be filtered while imageFilt is the image where the filtered oixels are set
     * 
     */
    public MeanFilterSerial (int windowWidth, BufferedImage image, BufferedImage imageFilt){

        this.windowWidth = windowWidth;
        this.image = image;
        this.imageFilt = imageFilt;
        this.reds = new int[windowWidth*windowWidth];
        this.greens = new int[windowWidth*windowWidth];
        this.blues = new int[windowWidth*windowWidth];
        this.filtWidth = (image.getWidth() - windowWidth);
        this.filtHeiht = (image.getHeight() - windowWidth);

        
    }

    /**
     * calculates the mean 
     * @param an array 0f integers
     * 
     */
    public int mean(int[] args) {
      
      return ((int)Math.round(IntStream.of(args).average().getAsDouble()));
    }

    /**
     * void method to filter or smooth images
     * 
     */
    public void meanFilter() {

        List<Color> colors;
        int red;
        int green;
        int blue;

        for (int yCord = 0; yCord < this.filtHeiht; yCord++) {//iterate through the height

            for(int xCord = 0; xCord < this.filtWidth; xCord++) {// iterate through the width
                colors = new ArrayList<Color>();

                // iteraation for the sliding square window
                for(int row = 0; row< this.windowWidth; row++) {
                    for(int col = 0; col<this.windowWidth; col++){
                        colors.add(new Color(this.image.getRGB(xCord+col ,yCord + row)));// store pixels as color objects
                    }
                }
  
  
                for (int z = 0; z< (this.windowWidth*this.windowWidth); z++){
                    this.reds[z] = colors.get(z).getRed();
                    this.greens[z] = colors.get(z).getGreen();
                    this.blues[z] = colors.get(z).getBlue();
  
                }

                int mid = this.windowWidth/2;// to locate the center of the sliding window
  
  
                red = this.mean(this.reds);
                green = this.mean(this.greens);
                blue = this.mean(this.blues);
  
                this.imageFilt.setRGB(xCord+mid, yCord+mid,new Color(red,green,blue).getRGB());
            }
        }

    }

    /**
     * gets the filtered image
     * 
     */
    public BufferedImage getImage() {

        return this.imageFilt;
    }

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
	 *  sets the required arguments for this class's comnstructor then run the required methods
	 *   calls tick() when the filter method is called and tock() once the filter method finishes to record the running time of the program
     * 
	 */
    public static void main(String args[])throws Throwable{

        int numOfArgs = args.length;

        if(numOfArgs == 3) {
            int temp = Integer.parseInt(args[2]);
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
        File outputImageFile = null;

        BufferedImage image = null;// Image used to get neighbouring pixels
        BufferedImage imageFilt = null;// The filtered image or image being filtered (set the mean or median pixel

        try{

            buffImageFile = new File("./images/"+args[0]);
            dupBuffImageFile = new File("./images/"+args[0]);
            outputImageFile = new File("./filtered/"+args[1]);

            image = ImageIO.read(buffImageFile);
            imageFilt = ImageIO.read(dupBuffImageFile);

        }catch(IOException e){
            System.out.println("Files couldn't be openned. Terminating....");
            System.exit(0);
        }
        

        MeanFilterSerial mean = new MeanFilterSerial(Integer.parseInt(args[2]),image,imageFilt);
        
        MeanFilterSerial.tick();
        mean.meanFilter();
        float time = MeanFilterSerial.tock();
        System.out.println("Total MeanFilterSerial run time: "+ time +" secs");
        
        ImageIO.write(mean.getImage(),"png",outputImageFile);

    }
}

