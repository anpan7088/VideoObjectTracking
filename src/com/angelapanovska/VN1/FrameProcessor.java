package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class FrameProcessor {
    private String framesFolderPath;
    private Map<Integer, BoxWithID> previousTracked = new HashMap<>();
    private int nextId = 1;

    public FrameProcessor(String framesFolderPath) {
        this.framesFolderPath = framesFolderPath;
    }

    public List<List<BoxWithID>> processFrames() {
        List<List<BoxWithID>> result = new ArrayList<>();
        try {
            File folder = new File(framesFolderPath);
            File[] frames = folder.listFiles((dir, name) -> name.endsWith(".png"));
            if (frames == null || frames.length < 2) {
                System.out.println("Not enough frames found.");
                return result;
            }

            Arrays.sort(frames);
            BufferedImage prev = ImageIO.read(frames[0]);

            for (int i = 1; i < frames.length; i++) {
                BufferedImage curr = ImageIO.read(frames[i]);
                boolean[][] diff = calculateDifference(curr, prev);
                List<BoxWithID> boxes = extractBoxes(diff);
                assignIDs(boxes);
                result.add(boxes);
                prev = curr;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private boolean[][] calculateDifference(BufferedImage curr, BufferedImage prev) {
        int w = curr.getWidth();
        int h = curr.getHeight();
        boolean[][] diff = new boolean[w][h];
        int threshold = 50;

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                int rgb1 = curr.getRGB(x, y);
                int rgb2 = prev.getRGB(x, y);

                int r1 = (rgb1 >> 16) & 0xff, g1 = (rgb1 >> 8) & 0xff, b1 = rgb1 & 0xff;
                int r2 = (rgb2 >> 16) & 0xff, g2 = (rgb2 >> 8) & 0xff, b2 = rgb2 & 0xff;

                int diffVal = Math.abs(r1 - r2) + Math.abs(g1 - g2) + Math.abs(b1 - b2);
                if (diffVal > threshold) {
                    diff[x][y] = true;
                }
            }
        }
        return diff;
    }

    private List<BoxWithID> extractBoxes(boolean[][] diff) {
        List<BoxWithID> boxes = new ArrayList<>();
        int w = diff.length;
        int h = diff[0].length;
        boolean[][] visited = new boolean[w][h];

        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                if (diff[x][y] && !visited[x][y]) {
                    int[] bounds = floodFill(diff, visited, x, y, w, h);
                    int minX = bounds[0], minY = bounds[1], maxX = bounds[2], maxY = bounds[3];
                    if ((maxX - minX) > 10 && (maxY - minY) > 10) {
                        boxes.add(new BoxWithID(-1, minX, minY, maxX, maxY));
                    }
                }
            }
        }
        return boxes;
    }

    private int[] floodFill(boolean[][] diff, boolean[][] visited, int sx, int sy, int w, int h) {
        int minX = sx, maxX = sx, minY = sy, maxY = sy;
        Queue<Point> q = new LinkedList<>();
        q.add(new Point(sx, sy));
        visited[sx][sy] = true;

        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
        while (!q.isEmpty()) {
            Point p = q.poll();
            for (int[] d : dirs) {
                int nx = p.x + d[0], ny = p.y + d[1];
                if (nx >= 0 && nx < w && ny >= 0 && ny < h && diff[nx][ny] && !visited[nx][ny]) {
                    visited[nx][ny] = true;
                    q.add(new Point(nx, ny));
                    minX = Math.min(minX, nx);
                    maxX = Math.max(maxX, nx);
                    minY = Math.min(minY, ny);
                    maxY = Math.max(maxY, ny);
                }
            }
        }
        return new int[]{minX, minY, maxX, maxY};
    }

    private void assignIDs(List<BoxWithID> current) {
        List<BoxWithID> updated = new ArrayList<>();
        for (BoxWithID box : current) {
            Point c = box.getCenter();
            int assignedId = -1;
            double minDist = Double.MAX_VALUE;

            for (BoxWithID prev : previousTracked.values()) {
                double d = c.distance(prev.getCenter());
                if (d < 50 && d < minDist) {
                    minDist = d;
                    assignedId = prev.id;
                }
            }

            if (assignedId == -1) {
                assignedId = nextId++;
            }

            box.id = assignedId;
            updated.add(box);
        }

        previousTracked.clear();
        for (BoxWithID b : updated) {
            previousTracked.put(b.id, b);
        }
    }
}
