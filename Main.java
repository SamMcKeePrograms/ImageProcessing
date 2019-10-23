package com.company;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.*;

public class Main {
    //"C:\\Users\\Sam\\Pictures\\pic.jpg"

    static int SAMPLE_DIVIDER = 1;

    public static void main(String[] args) throws IOException {

        BufferedImage imageRead=null, resizedImg = null, normyImg = null;
        long finalTime = 0;

        //for(SAMPLE_DIVIDER = 100; SAMPLE_DIVIDER <= 10000; SAMPLE_DIVIDER+=100) {
            File image = new File("C:\\Users\\Sam\\Pictures\\test_red.jpg");
            long startTime = System.nanoTime();
            imageRead = ImageIO.read(image);

            resizedImg = resizeImage(imageRead, imageRead.getWidth(), imageRead.getHeight(), .25);
            normyImg = normalizeImgC(resizedImg);

            long endTime = System.nanoTime();
            finalTime = (endTime - startTime) / 1000000;
            try {
                File imageFile = new File("C:\\Users\\Sam\\Pictures\\normalizedImgC_"+SAMPLE_DIVIDER+".jpg");
                ImageIO.write(normyImg, "jpg", imageFile);
            } catch (IOException e) {

                System.out.println(e);
            }
            System.out.println("Final Time: " + finalTime);
        //}
        //finalTime -= 200;

        System.out.println("WIDTH:"+resizedImg.getWidth());
        System.out.println("HEIGHT:"+resizedImg.getHeight());
        System.out.println("AREA:"+resizedImg.getWidth()*resizedImg.getHeight());
    }
    public static Future<Integer> futureDispose(final Graphics2D g2d){
//        Graphics2D graphics2D = null;
        return Executors.newSingleThreadExecutor().submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                g2d.dispose();
                return 0;
            }
        });
    }
    public static BufferedImage resizeImage(Image image, int width, int height, double scale) throws IOException {
        width = (int) (width * scale);
        height = (int) (height * scale);
        //creates a buffered image with the variable image and defines a image type as RGB
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = bufferedImage.createGraphics();
        //this defines how the pixels will be combined together
        graphics2D.setComposite(AlphaComposite.Src);
        //draws the new images back into the image variable
        graphics2D.drawImage(image, 0, 0, width, height, null);
        futureDispose(graphics2D);
        return bufferedImage;
    }

    //writes the new resized image in the file path
    public static double[] returnRGB(int imageValues){
        double[] rgbValues = new double[3];
        int r = (imageValues>>16)&0xff;
        int g = (imageValues>>8)&0xff;
        int b = imageValues&0xff;
        rgbValues[0] = r;
        rgbValues[1] = g;
        rgbValues[2] = b;
        return rgbValues;
    }
    public static double[] getStDv(double[] RGBSums, BufferedImage img){
        double[] returnArray = new double[3];
        for (int y = 0; y < img.getHeight(); y+=SAMPLE_DIVIDER) {
            for (int x = 0; x < img.getWidth(); x+=SAMPLE_DIVIDER) {
                double[] rbgValuesReturned = returnRGB(img.getRGB(x, y));
                double redSquare = Math.pow((rbgValuesReturned[0] - RGBSums[0]), 2);
                double greenSquare = Math.pow((rbgValuesReturned[1] - RGBSums[1]), 2);
                double blueSquare = Math.pow((rbgValuesReturned[2] - RGBSums[2]), 2);
                returnArray[0] += redSquare;
                returnArray[1] += greenSquare;
                returnArray[2] += blueSquare;
            }
        }
        return returnArray;
    }
    /*
        returns the averages of the RGB values in a double array
        r averages = RGBSums[0]
        g averages = RGBSums[1]
        b averages = RGBSums[2]
    */
    public static double[] getAverage(BufferedImage image){
        double[] RGBSums = new double[3];
        for (int y = 0; y < image.getHeight(); y+=SAMPLE_DIVIDER) {
            for (int x = 0; x < image.getWidth(); x+=SAMPLE_DIVIDER) {
                double[] rbgValuesReturned = returnRGB(image.getRGB(x, y));
                RGBSums[0] += rbgValuesReturned[0];
                RGBSums[1] += rbgValuesReturned[1];
                RGBSums[2] += rbgValuesReturned[2];

            }
        }

        RGBSums[0]/= (image.getWidth()*image.getHeight())/(SAMPLE_DIVIDER*SAMPLE_DIVIDER);
        RGBSums[1]/= (image.getWidth()*image.getHeight())/(SAMPLE_DIVIDER*SAMPLE_DIVIDER);
        RGBSums[2]/= (image.getWidth()*image.getHeight())/(SAMPLE_DIVIDER*SAMPLE_DIVIDER);
        return RGBSums;

    }
    public static BufferedImage normalizeImgC(BufferedImage img) {
        /*BufferedImage img = null;
        try {
            img = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.out.println(e);
        }*/
        //assert img != null;
        double[][] red = new double[img.getWidth()][img.getHeight()];
        double[][] green = new double[img.getWidth()][img.getHeight()];
        double[][] blue = new double[img.getWidth()][img.getHeight()];
       /* double redSum = 0;
        double greenSum = 0;
        double blueSum = 0;*/
        int width = img.getWidth();
        int height = img.getHeight();
        //getting the mean of all the numbers
        /*for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] rbgValuesReturned = returnRGB(img.getRGB(x, y));
                //img.setRGB(x, y, p);
                redSum += rbgValuesReturned[0];
                greenSum += rbgValuesReturned[1];
                blueSum += rbgValuesReturned[2];
            }
        }*/
        double[] RGBSums = getAverage(img);
        /*double redAvg = RGBSums[0];//redSum/(width*height)
        double greenAvg = RGBSums[1];
        double blueAvg = RGBSums[2];*/
        /*double redSum2 = 0;RGBSums[0]
        double greenSum2 = 0;
        double blueSum2 = 0;*/
        //getting the standard deviation
        /*for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] rbgValuesReturned = returnRGB(img.getRGB(x, y));
                double redSquare = Math.pow((rbgValuesReturned[0] - redAvg), 2);
                double greenSquare = Math.pow((rbgValuesReturned[1] - greenAvg), 2);
                double blueSquare = Math.pow((rbgValuesReturned[2] - blueAvg), 2);
                redSum2 += redSquare;
                greenSum2 += greenSquare;
                blueSum2 += blueSquare;
            }
        }*/
        double[] stDev = getStDv(RGBSums, img);
        double redStDev = Math.sqrt(stDev[0] / (width * height)/(SAMPLE_DIVIDER*SAMPLE_DIVIDER));
        double greenStDev = Math.sqrt(stDev[1] / (width * height)/(SAMPLE_DIVIDER*SAMPLE_DIVIDER));
        double blueStDev = Math.sqrt(stDev[2] / (width * height)/(SAMPLE_DIVIDER*SAMPLE_DIVIDER));


        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double[] rbgValuesReturned = returnRGB(img.getRGB(x, y));
                double redNorm = (rbgValuesReturned[0] - RGBSums[0]) / redStDev;
                red[x][y] = redNorm;
                double greenNorm = (rbgValuesReturned[1] - RGBSums[1]) / greenStDev;
                green[x][y] = greenNorm;
                double blueNorm = (rbgValuesReturned[2] - RGBSums[2]) / blueStDev;
                blue[x][y] = blueNorm;
            }
        }
        double redMostE = Double.MIN_VALUE;
        double greenMostE = Double.MIN_VALUE;
        double blueMostE = Double.MIN_VALUE;
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {

                if (Math.abs(red[x][y]) > redMostE) {
                    redMostE = Math.abs(red[x][y]);
                }
                if (Math.abs(blue[x][y]) > blueMostE) {
                    blueMostE = Math.abs(blue[x][y]);
                }
                if (Math.abs(green[x][y]) > greenMostE) {
                    greenMostE = Math.abs(green[x][y]);
                }
            }
        }
        for (int y = 1; y < height; y++) {
            for (int x = 1; x < width; x++) {
                double redXDec = red[x][y] * (127 / redMostE) + 128;
                red[x][y] = redXDec;
                double blueXDec = blue[x][y] * (127 / blueMostE) + 128;
                blue[x][y] = blueXDec;
                double greenXDec = green[x][y] * (127 / greenMostE) + 128;
                green[x][y] = greenXDec;
            }
        }
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p;
                int r = (int) red[x][y];
                int g = (int) green[x][y];
                int b = (int) blue[x][y];


                p = (0xff << 24) | (r << 16) | (g << 8) | b;
                img.setRGB(x, y, p);


            }
        }

        return img;

    }
}

