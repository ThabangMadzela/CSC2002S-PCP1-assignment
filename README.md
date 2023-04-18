# 2D Median Filter for Image Smoothing
## Parallel Programming with the Java Fork/Join framework:
    The aim of this project is to implement two parallel filters for smoothing RGB 
    colour images using the Java Fork-Join library, and to benchmark the parallel 
    program to determine under which conditions parallelization is worth the
    extra effort involved.

## Project Details

### Description

#### Two filters for smoothing RGB colour images:
- A mean filter that sets each pixel in the image to the average of the surrounding pixels.
- A median filter that sets each pixel to the median of the surrounding pixels.

Both methods use a sliding square window of a specified width **'w'** 
(where **'w'** is an odd number >=3) that defines the neighbouring 
pixels that are used to calculate the mean or the median. 
For RGB colour images, the filter calculation considers the 
three colour components of each pixel (red, green, and blue) 
separately – and each pixel is set to the mean/median of the red, 
green, and blue values of the surrounding pixels.

### Input and Output
The programs must take the following command-line parameters (in order):
**'\<inputImageName\>' '\<outputImageName\>' '\<windowWidth\>'**

where
* **'\<inputImageName\>'** is the name of the input image, including the file extension.
* **'\<outputImageName\>'** is the name of the input image, including the file extension.
* **'\<windowWidth\>'** is the width of the square filter window. This must be an odd, positive integer >=3.

Invalid input is handled gracefully, exiting without crashing. The java.awt.image.BufferedImage class makes it easy to deal with a range of image file types.

### Program Files

* MeanFilterSerial.java
* MeanFilterParallel.java
* MedianFilterSerial.java
* MedianFilterParallel.java

In this project, we have successfully implemented two parallel filters 
for smoothing RGB colour images using the Java Fork-Join library, and 
benchmarked the programs experimentally to determine the conditions under which parallelization is worth the extra effort involved. The speedup graphs generated from the experiments have been included. The four Java program src files have been included with the exact names as required.
