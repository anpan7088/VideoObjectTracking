package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class FrameProcessor {
    private String inputFolderPath;
    private String outputFolderPath;

    public FrameProcessor (String inputFolderPath, String outputFolderPath){
        this.inputFolderPath = inputFolderPath;
        this.outputFolderPath = outputFolderPath;
    }

    public void processFrames(){
        try {
            // listing all frame files in the input folder
            File inputFolder = new File(inputFolderPath);
            File [] frameFiles = inputFolder.listFiles((dir,name) -> name.endsWith(".png"));

            if (frameFiles == null || frameFiles.length < 2) {
                System.out.println("Not enough frames to process.");
                return;
            }

            Arrays.sort(frameFiles); // Sorting files to ensure sequential processing

            BufferedImage prevFrame = ImageIO.read(frameFiles[0]); // read the first frame

            for (int i=1; i< frameFiles.length; i++){
                BufferedImage currentFrame = ImageIO.read(frameFiles[i]);

                // processing the frames and detecting differencies razliki
                BufferedImage processedFrame = detectChanges(prevFrame, currentFrame);

                // save the frames
                String outputFileName = outputFolderPath + "/frame_" + i + ".png";
                ImageIO.write(processedFrame, "png", new File(outputFileName));

                prevFrame = currentFrame; // update previous frame
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

        private  BufferedImage detectChanges (BufferedImage prevFrame, BufferedImage currentFrame){
        int width = prevFrame.getWidth();
        int height = prevFrame.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int threshold = 30; // threshold for pixel difference

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int prevPixel = prevFrame.getRGB(x, y);
                    int currPixel = currentFrame.getRGB(x, y);

                    //absolute difference between RGB values
                    int diff = Math.abs((prevPixel & 0xFF)- (currPixel & 0xFF));

                    if (diff > threshold) {
                        // lets highlight the change (red color overlay)
                        result.setRGB(x, y, new Color(255,0, 0, 128).getRGB());
                    } else {
                        // keep the original pixel
                        result.setRGB(x, y, currentFrame.getRGB(x, y));
                    }

                }
            }
            return result;
        }
    }
