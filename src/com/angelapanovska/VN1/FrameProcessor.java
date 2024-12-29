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

            for (int i = 1; i < frameFiles.length; i++) {
                BufferedImage currentFrame = ImageIO.read(frameFiles[i]);

                // Process the frame for red object tracking
                BufferedImage processedFrame = trackRedObject(currentFrame, prevFrame);

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

    private BufferedImage trackRedObject(BufferedImage frame, BufferedImage prevFrame) {
        int width = frame.getWidth();
        int height = frame.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();

        // lets copy the original framee to the result
        g2d.drawImage(frame, 0, 0, null);

        int intensityThreshold = 450;
        int minWidth = 50, minHeight = 50;
        //int minObjectSize = 50; // Minimum size of the detected object

        int minX = width, minY = height, maxX = 0, maxY = 0;
        boolean objectFound = false;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int prevPixel = prevFrame.getRGB(x, y);
                int currentPixel = frame.getRGB(x, y);

                // calculate the absolute difference between the puxekls
                int prevRed = (prevPixel >> 16) & 0xff;
                int prevGreen = (prevPixel >> 8) & 0xff;
                int prevBlue = prevPixel & 0xff;

                int currRed = (currentPixel >> 16) & 0xff;
                int currGreen = (currentPixel >> 8) & 0xff;
                int currBlue = currentPixel & 0xff;

                // Calculate the combined intensity difference
                int intensityDifference = Math.abs(currRed - prevRed) +
                        Math.abs(currGreen - prevGreen) +
                        Math.abs(currBlue - prevBlue);
                // lets check if the pixel matches the object-like colors (black or dark with
                // contrast))))))
                if ((intensityDifference > intensityThreshold)) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);

                    objectFound = true;
                
                    // debug testing
                    result.setRGB(x, y, new Color(255, 0, 0).getRGB());
                } else {
                    result.setRGB(x, y, frame.getRGB(x, y));
                }
            }
        }
        
        // Apply overlay and draw bounding box only if an object is detected
        if (objectFound && (maxX - minX > minWidth) && (maxY - minY > minHeight)) {
            // Add semi-transparent yellow overlay for the detected region
            g2d.setColor(new Color(255, 255, 0, 128));
            g2d.fillRect(minX, minY, maxX - minX, maxY - minY);

            // Draw a bounding box around the detected object
            g2d.setColor(Color.GREEN);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(minX, minY, maxX - minX, maxY - minY);

            System.out.println("Bounding box coordinates: " + minX + ", " + minY + ", " + maxX + ", " + maxY);
        } else {
            System.out.println("No object detected.");
        }

        g2d.dispose();
        return result;
    }
}
