package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class FrameProcessor {
    private String inputFolderPath;
    private int nextId = 1;

    public FrameProcessor(String framesFolderPath) {
        this.inputFolderPath = framesFolderPath;
    }

    public List<List<BoxWithID>> processFrames() {
        List<List<BoxWithID>> allBoxes = new ArrayList<>();

        try {
            File inputFolder = new File(inputFolderPath);
            File[] frameFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".png"));

            if (frameFiles == null || frameFiles.length < 2) {
                System.out.println("Not enough frames to process.");
                return allBoxes;
            }

            Arrays.sort(frameFiles);
            BufferedImage prevFrame = ImageIO.read(frameFiles[0]);

            for (int i = 1; i < frameFiles.length; i++) {
                BufferedImage currentFrame = ImageIO.read(frameFiles[i]);
                List<BoxWithID> frameBoxes = detectMovingObjects(currentFrame, prevFrame);
                allBoxes.add(frameBoxes);
                prevFrame = currentFrame;
            }

            System.out.println("Processing completed!");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allBoxes;
    }

    private List<BoxWithID> detectMovingObjects(BufferedImage frame, BufferedImage prevFrame) {
        int width = frame.getWidth();
        int height = frame.getHeight();
        boolean[][] diff = new boolean[width][height];
        int threshold = 10;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb1 = prevFrame.getRGB(x, y);
                int rgb2 = frame.getRGB(x, y);

                int r1 = (rgb1 >> 16) & 0xff;
                int g1 = (rgb1 >> 8) & 0xff;
                int b1 = rgb1 & 0xff;

                int r2 = (rgb2 >> 16) & 0xff;
                int g2 = (rgb2 >> 8) & 0xff;
                int b2 = rgb2 & 0xff;

                int diffValue = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                if (diffValue > threshold) {
                    diff[x][y] = true;
                }
            }
        }

        return findBoundingBoxes(diff, width, height);
    }

    private List<BoxWithID> findBoundingBoxes(boolean[][] diff, int width, int height) {
        List<BoxWithID> boxes = new ArrayList<>();
        boolean[][] visited = new boolean[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (diff[x][y] && !visited[x][y]) {
                    int[] bounds = floodFill(diff, visited, x, y, width, height);
                    int minX = bounds[0], minY = bounds[1], maxX = bounds[2], maxY = bounds[3];

                    if ((maxX - minX) > 10 && (maxY - minY) > 10) {
                        boxes.add(new BoxWithID(nextId++, minX, minY, maxX, maxY));
                    }
                }
            }
        }

        return boxes;
    }

    private int[] floodFill(boolean[][] diff, boolean[][] visited, int x, int y, int width, int height) {
        int minX = x, minY = y, maxX = x, maxY = y;
        Queue<int[]> queue = new LinkedList<>();
        queue.add(new int[]{x, y});
        visited[x][y] = true;

        int[][] directions = {{0,1},{1,0},{0,-1},{-1,0}};
        while (!queue.isEmpty()) {
            int[] curr = queue.poll();
            int cx = curr[0];
            int cy = curr[1];

            for (int[] d : directions) {
                int nx = cx + d[0];
                int ny = cy + d[1];
                if (nx >= 0 && nx < width && ny >= 0 && ny < height &&
                    !visited[nx][ny] && diff[nx][ny]) {
                    queue.add(new int[]{nx, ny});
                    visited[nx][ny] = true;
                    minX = Math.min(minX, nx);
                    minY = Math.min(minY, ny);
                    maxX = Math.max(maxX, nx);
                    maxY = Math.max(maxY, ny);
                }
            }
        }

        return new int[]{minX, minY, maxX, maxY};
    }
}
