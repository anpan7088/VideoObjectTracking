package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class FrameProcessor {
    private String inputFolderPath;
    private String outputFolderPath;
    private java.util.List<Color> objectColors;

    public FrameProcessor(String inputFolderPath, String outputFolderPath) {
        this.inputFolderPath = inputFolderPath;
        this.outputFolderPath = outputFolderPath;

        // defining the colors of the objects we want to track
        this.objectColors = new ArrayList<>(Arrays.asList(
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA,
                Color.YELLOW, new Color(128, 0, 128), new Color(0, 128, 128), new Color(128, 128, 0),
                new Color(128, 0, 0)));
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

    private BufferedImage detectChanges(BufferedImage prevFrame, BufferedImage currentFrame) {
        int width = prevFrame.getWidth();
        int height = prevFrame.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        int[][] labels = new int[width][height]; // matrix for the region labels
        int labelCounter = 1; // Initialize labelCounter

        // list of predefined colors for objects
        Color[] colors = {
                Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.CYAN, Color.MAGENTA, Color.CYAN,
                new Color(255, 165, 0), // Orange
                new Color(128, 0, 128) // Purple
        };

        int threshold = 30; // threshold for pixel difference
        //int regionCounter = 0; // Initialize regionCounter

        Map<Integer, Color> labelToColor = new HashMap<>(); // Initialize labelToColor map

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int prevPixel = prevFrame.getRGB(x, y);
                int currPixel = currentFrame.getRGB(x, y);

                // absolute difference between RGB values
                int diff = Math.abs((prevPixel & 0xFF) - (currPixel & 0xFF));

                if (diff > threshold) {
                    if (labels[x][y] == 0) {
                        floodFill(labels, x, y, labelCounter, width, height);
                        labelToColor.put(labelCounter, colors[labelCounter % colors.length]);
                        labelCounter++;
                    }
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int label = labels[x][y];
                if (label > 0) {
                    Color color = labelToColor.get(label);
                    result.setRGB(x, y, color.getRGB());
                } else {
                    result.setRGB(x, y, currentFrame.getRGB(x, y)); // keep non-moving pixels
                }
            }
        }
        return result;
    }

    // method flood fill alggorithm for connected component labeling
    private void floodFill(int[][] labels, int x, int y, int label, int width, int height) {
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[] { x, y });

        while (!queue.isEmpty()) {
            int[] pixel = queue.poll();
            int px = pixel[0];
            int py = pixel[1];

            if (px < 0 || px >= width || py < 0 || py >= height || labels[px][py] != 0) {
                continue; // out of bounds or already labeled
            }
            labels[px][py] = label;

            // lets add neighbours to the queue
            queue.add(new int[] { px + 1, py });
            queue.add(new int[] { px - 1, py });
            queue.add(new int[] { px, py + 1 });
            queue.add(new int[] { px, py - 1 });
        }
    }

}
