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

    public List<List<Map<String, Integer>>> processFrames() {
        List<List<Map<String, Integer>>> allBoundingBoxes = new ArrayList<>(); // storing the coordinates of the
                                                                               // detected objects

        try {
            // listing all frame files in the input folder
            File inputFolder = new File(inputFolderPath);
            File[] frameFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".png"));

            if (frameFiles == null || frameFiles.length < 2) {
                System.out.println("Not enough frames to process.");
                return allBoundingBoxes;
            }

            Arrays.sort(frameFiles); // Sorting files to ensure sequential processing

            BufferedImage prevFrame = ImageIO.read(frameFiles[0]); // read the first frame

            for (int i = 1; i < frameFiles.length; i++) {
                BufferedImage currentFrame = ImageIO.read(frameFiles[i]);

                // detecting moving objects and their coordinates
                List<Map<String, Integer>> frameBoundingBoxes = detectMovingObjects(currentFrame, prevFrame);

                // if (!objectCoordinates.isEmpty()) {
                allBoundingBoxes.add(frameBoundingBoxes);
                // }
                prevFrame = currentFrame; // Update the previous frame for the next iteration
            }

            System.out.println("Processing completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allBoundingBoxes;
    }

    private List<Map<String, Integer>> detectMovingObjects(BufferedImage frame, BufferedImage prevFrame) {
        int width = frame.getWidth();
        int height = frame.getHeight();

        boolean[][] differenceMatrix = new boolean[width][height];
        int intensityThreshold = 80;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int prevPixel = prevFrame.getRGB(x, y);
                int currentPixel = frame.getRGB(x, y);

                // calculate the absolute difference between the pixels
                int prevRed = (prevPixel >> 16) & 0xff;
                int prevGreen = (prevPixel >> 8) & 0xff;
                int prevBlue = prevPixel & 0xff;

                int currRed = (currentPixel >> 16) & 0xff;
                int currGreen = (currentPixel >> 8) & 0xff;
                int currBlue = currentPixel & 0xff;

                int intensityDifference = Math.abs(currRed - prevRed) +
                        Math.abs(currGreen - prevGreen) +
                        Math.abs(currBlue - prevBlue);

                if ((intensityDifference > intensityThreshold)) {
                    differenceMatrix[x][y] = true;
                }
            }
        }
        // detect connected components
        return findBoundingBoxes(differenceMatrix, height, width);

    }
    private List<Map<String, Integer>> findBoundingBoxes(boolean[][] differenceMatrix, int height, int width) {
        List<Map<String, Integer>> boundingBoxes = new ArrayList<>();
        boolean[][] visited = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (differenceMatrix[x][y] && !visited[x][y]) {

                    int[] bounds = floodFill(differenceMatrix, visited, x, y, width, height);

                    int minX = bounds[0], minY = bounds[1], maxX = bounds[2], maxY = bounds[3];
                    if ((maxX - minX) > 10 && (maxY - minY) > 10) {
                        Map<String, Integer> box = new HashMap<>();
                        box.put("minX", minX);
                        box.put("minY", minY);
                        box.put("maxX", maxX);
                        box.put("maxY", maxY);
                        boundingBoxes.add(box);
                    }
                }
            }
        }
        return boundingBoxes;
    }
    private int[] floodFill(boolean[][] differenceMatrix, boolean[][] visited, int startX, int startY, int width, int height) {
        int minX = startX, minY = startY, maxX = startX, maxY = startY;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{startX, startY});
        visited[startX][startY] = true;

        int [][] directionss = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};
        while (!queue.isEmpty()) {
            int[] current = queue.poll();
            int x = current[0];
            int y = current[1];

            for (int[] direction : directionss) {
                int newX = x + direction[0];
                int newY = y + direction[1];

                if(newX >= 0 && newX < width && newY >= 0 && newY < height && !visited[newX][newY] && differenceMatrix[newX][newY]) {
                    queue.add(new int[]{newX, newY});
                    visited[newX][newY] = true;
                    minX = Math.min(minX, newX);
                    minY = Math.min(minY, newY);
                    maxX = Math.max(maxX, newX);
                    maxY = Math.max(maxY, newY);
                }
            }
        }
        return new int[]{minX, minY, maxX, maxY};
    }
}
