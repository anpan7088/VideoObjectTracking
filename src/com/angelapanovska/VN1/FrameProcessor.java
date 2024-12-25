package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class FrameProcessor {
    private String inputFolderPath;
    private String outputFolderPath;

    public FrameProcessor(String inputFolderPath, String outputFolderPath) {
        this.inputFolderPath = inputFolderPath;
        this.outputFolderPath = outputFolderPath;
    }


        public void processFrames() {
            try {
            // listing all frame files in the input folder
            File inputFolder = new File(inputFolderPath);
            File[] frameFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".png"));

            if (frameFiles == null || frameFiles.length < 2) {
                System.out.println("Not enough frames to process.");
                return;
            }

            Arrays.sort(frameFiles); // Sorting files to ensure sequential processing

            BufferedImage prevFrame = ImageIO.read(frameFiles[0]); // read the first frame

            for (int i = 0; i < frameFiles.length; i++) {
                BufferedImage currentFrame = ImageIO.read(frameFiles[i]);

                //Process the frame for red object tracking
                BufferedImage processedFrame = trackRedObject(currentFrame);

                // Save the result to the output folder
                String outputFilePath = outputFolderPath + "/frame_" + i + ".png";
                File outputFile = new File(outputFilePath);
                ImageIO.write(processedFrame, "png", outputFile);

                prevFrame = currentFrame; // Update the previous frame for the next iteration
            }

            System.out.println("Processing completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage trackRedObject(BufferedImage frame) {
        int width = frame.getWidth();
        int height = frame.getHeight();
    
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();

        // lets copy the original framee to the result
        g2d.drawImage(frame, 0, 0, null); 

        int brightnessThreshold = 50;

        int minX = width, minY = height, maxX = 0, maxY = 0;
        boolean objectFound = false;

        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                int pixel = frame.getRGB(x, y);

                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;
                
                

                // lets check if the pixel is red
                if(red > brightnessThreshold && green < brightnessThreshold && blue < brightnessThreshold) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                    // lets draw a rectangle around the red object
                
                    objectFound = true;
                }
        
            }
        }

        if (objectFound) {
            // lets draw a rectangle around the red object
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(minX, minY, maxX - minX, maxY - minY);

            System.out.println("Bounding box coordinates: " + minX + ", " + minY + ", " + maxX + ", " + maxY);
        }

        g2d.dispose();
        return result;
    }
}
