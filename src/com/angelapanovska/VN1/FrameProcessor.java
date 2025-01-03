package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import java.util.List;

public class FrameProcessor {
    private String inputFolderPath;
    

    public FrameProcessor(String inputFolderPath) {
        this.inputFolderPath = inputFolderPath;
    }

    public List<Map<String, Integer>> processFrames() {
        List<Map<String, Integer>> boundingBoxes = new ArrayList<>(); // storing the coordinates of the detected objects
        try {
            // listing all frame files in the input folder
            File inputFolder = new File(inputFolderPath);
            File[] frameFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".png"));

            if (frameFiles == null || frameFiles.length < 2) {
                System.out.println("Not enough frames to process.");
                return boundingBoxes;
            }

            Arrays.sort(frameFiles); // Sorting files to ensure sequential processing

            BufferedImage prevFrame = ImageIO.read(frameFiles[0]); // read the first frame

            for (int i = 1; i < frameFiles.length; i++) {
                BufferedImage currentFrame = ImageIO.read(frameFiles[i]);

                //detecting moving  objects and their coordinates
                Map<String, Integer> objectCoordinates = detectMovingObjects(currentFrame, prevFrame);
                
                if (!objectCoordinates.isEmpty()) {
                    boundingBoxes.add(objectCoordinates);
                }
                prevFrame = currentFrame; // Update the previous frame for the next iteration
            }

            System.out.println("Processing completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return boundingBoxes;
    }

    private Map<String, Integer> detectMovingObjects (BufferedImage frame, BufferedImage prevFrame) {
        int width = frame.getWidth();
        int height = frame.getHeight();

       int intensityThreshold = 150;
       int minWidth = 50, minHeight = 50;

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
                    
                } 
                }
            }
        
            Map<String, Integer> objectCoordinates = new HashMap<>();
        // Apply overlay and draw bounding box only if an object is detected
        if (objectFound && (maxX - minX > minWidth) && (maxY - minY > minHeight)) {
            objectCoordinates.put("minX", minX);
            objectCoordinates.put("minY", minY);
            objectCoordinates.put("maxX", maxX);
            objectCoordinates.put("maxY", maxY);

            System.out.println("Bounding box coordinates: " + minX + ", " + minY + ", " + maxX + ", " + maxY);
        } else {
            System.out.println("No object detected.");
        }
        return objectCoordinates;
    }
}
