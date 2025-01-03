package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BoundingBoxDrawer {
    private String inputFolderpath;
    private String outputFolderPath;

    public BoundingBoxDrawer (String inputFolderPath, String outputFolderPath){
        this.inputFolderpath=inputFolderPath;
        this.outputFolderPath=outputFolderPath;
    }


    public void drawBoundingBoxes(List<Map<String, Integer>> boundingBoxes) {
        try {
            // listing all frame files in the input folder
            File inputFolder = new java.io.File(inputFolderpath);
            File[] frameFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".png"));
            
            if (frameFiles == null || frameFiles.length == 0) {
                System.out.println("No frames found in the input folder.");
                return;
            }

            Arrays.sort(frameFiles);

            
            if (boundingBoxes.size() != frameFiles.length){
                System.out.println("Mistmatch between number of frames and boxes");
                while (boundingBoxes.size() < frameFiles.length) {
                    boundingBoxes.add(null); // Fill with null
                }
            }

            File outputFolder = new File (outputFolderPath);
            if (!outputFolder.exists()){
                outputFolder.mkdirs();
            }
            

            for (int i = 0; i < frameFiles.length; i++) {
                BufferedImage frame = ImageIO.read(frameFiles[i]);

                // Retrieve the detected object coordinates for the current frame
                Map<String, Integer> coordinates = boundingBoxes.get(i);
                
                if (coordinates != null) {
                    // Draw bounding boxes on the frame
                    BufferedImage processedFrame = addBoundingBox(frame, coordinates);

                   // Save the processed frame to the output folder
                String outputFileName = "frame_" + (i + 1) + ".png";
                File outputFile = new File(outputFolderPath + "/" + outputFileName);
                ImageIO.write(processedFrame, "png", outputFile);
                
                System.out.println("Processed and saved: " + outputFile.getAbsolutePath());
                }
            }  

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage addBoundingBox(BufferedImage frame, Map<String, Integer> coordinates) {
        // Implementation of addBoundingBox method
        int width = frame.getWidth();
        int height = frame.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();

        // copyuing the original frame to the result
        g2d.drawImage (frame, 0,0, null);

        //coordinate extraction
        int minX= coordinates.get("minX");
        int minY = coordinates.get ("minY");
        int maxX = coordinates.get ("maxX");
        int maxY = coordinates.get ("maxY");

        //drawing rectangle bounding box
        g2d.setColor(Color.GREEN);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(minX, minY, maxX - minX, maxY - minY);

        g2d.dispose();
        return result;
    }
}
