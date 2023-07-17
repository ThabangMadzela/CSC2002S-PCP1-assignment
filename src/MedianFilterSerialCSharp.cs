using System;
using System.Collections.Generic;
using System.Drawing;
using System.IO;

namespace MedianFilter
{
    class MedianFilterSerial
    {
        private int[] reds;
        private int[] greens;
        private int[] blues;
        private int windowWidth;
        private int filtWidth;
        private int filtHeight;
        private Bitmap image;
        private Bitmap imageFilt;

        public MedianFilterSerial(int windowWidth, Bitmap image, Bitmap imageFilt)
        {
            this.windowWidth = windowWidth;
            this.image = image;
            this.imageFilt = imageFilt;
            this.reds = new int[windowWidth * windowWidth];
            this.greens = new int[windowWidth * windowWidth];
            this.blues = new int[windowWidth * windowWidth];
            this.filtWidth = image.Width - windowWidth;
            this.filtHeight = image.Height - windowWidth;
        }

        public void MedianFilter()
        {
            List<Color> colors = new List<Color>();

            for (int yCord = 0; yCord < filtHeight; yCord++)
            {
                for (int xCord = 0; xCord < filtWidth; xCord++)
                {
                    colors.Clear();

                    for (int row = 0; row < windowWidth; row++)
                    {
                        for (int col = 0; col < windowWidth; col++)
                        {
                            colors.Add(image.GetPixel(xCord + col, yCord + row));
                        }
                    }

                    for (int z = 0; z < windowWidth * windowWidth; z++)
                    {
                        reds[z] = colors[z].R;
                        greens[z] = colors[z].G;
                        blues[z] = colors[z].B;
                    }

                    Array.Sort(reds);
                    Array.Sort(greens);
                    Array.Sort(blues);

                    int mid = (windowWidth * windowWidth) / 2;
                    int mi = windowWidth / 2;

                    imageFilt.SetPixel(xCord + mi, yCord + mi, Color.FromArgb(reds[mid], greens[mid], blues[mid]));
                }
            }
        }

        public Bitmap GetImage()
        {
            return imageFilt;
        }

        static void Main(string[] args)
        {
            if (args.Length != 3)
            {
                Console.WriteLine("Requires the following command-line parameters (in order): <inputImageName> <outputImageName> <windowWidth>");
                return;
            }

            string inputImageName = args[0];
            string outputImageName = args[1];
            int windowWidth = int.Parse(args[2]);

            if (windowWidth < 3 || windowWidth % 2 == 0)
            {
                Console.WriteLine("Width of the square filter window must be an odd positive integer >=3");
                return;
            }

            string inputImagePath = "./images/" + inputImageName;
            string outputImagePath = "./filtered/" + outputImageName;

            if (!File.Exists(inputImagePath))
            {
                Console.WriteLine("Input image file does not exist");
                return;
            }

            Bitmap inputImage = new Bitmap(inputImagePath);
            Bitmap outputImage = new Bitmap(inputImage.Width, inputImage.Height);

            MedianFilterSerial medianFilter = new MedianFilterSerial(windowWidth, inputImage, outputImage);

            Console.WriteLine("Applying Median Filter...");
            medianFilter.MedianFilter();
            Console.WriteLine("Median Filter applied successfully");

            Console.WriteLine("Saving output image...");
            outputImage.Save(outputImagePath);
            Console.WriteLine("Output image saved successfully");

            Console.WriteLine("Process completed");
        }
    }
}
