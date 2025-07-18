package com.angelapanovska.VN1;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;

public class BoundingBoxDrawer {
    private String inputFolderpath;
    private String outputFolderPath;

    private int nextObjectId = 1;
    private Map<Integer, Color> idToColor = new HashMap<>();
    private Map<Integer, BoxWithID> lastTrackedBoxes = new HashMap<>();

    private final double SMOOTHING_ALPHA = 0.6; // how "sticky" boxes are (0.5 = responsive, 0.9 = very stable)
    private final int FLICKER_THRESHOLD = 5;    // pixels of allowed box size change before update

    public BoundingBoxDrawer(String inputFolderPath, String outputFolderPath) {
        this.inputFolderpath = inputFolderPath;
        this.outputFolderPath = outputFolderPath;
    }

    public void drawBoundingBoxes(List<List<Map<String, Integer>>> allBoundingBoxes) {
        try {
            File inputFolder = new File(inputFolderpath);
            File[] frameFiles = inputFolder.listFiles((dir, name) -> name.endsWith(".png"));

            if (frameFiles == null || frameFiles.length == 0) {
                System.out.println("No frames found.");
                return;
            }

            Arrays.sort(frameFiles);
            List<BoxWithID> previousBoxes = new ArrayList<>();
            int distanceThreshold = 50;

            for (int i = 0; i < frameFiles.length; i++) {
                BufferedImage frame = ImageIO.read(frameFiles[i]);
                List<Map<String, Integer>> frameBoxes = i < allBoundingBoxes.size() ? allBoundingBoxes.get(i) : null;

                if (frameBoxes != null) {
                    List<BoxWithID> currentBoxes = new ArrayList<>();

                    for (Map<String, Integer> box : frameBoxes) {
                        int minX = box.get("minX");
                        int minY = box.get("minY");
                        int maxX = box.get("maxX");
                        int maxY = box.get("maxY");
                        Point center = new Point((minX + maxX) / 2, (minY + maxY) / 2);

                        // Match to previous box (based on distance)
                        int assignedId = -1;
                        double minDist = Double.MAX_VALUE;

                        for (BoxWithID prevBox : previousBoxes) {
                            double dist = center.distance(prevBox.getCenter());
                            if (dist < distanceThreshold && dist < minDist) {
                                minDist = dist;
                                assignedId = prevBox.id;
                            }
                        }

                        // Assign new ID if no match
                        if (assignedId == -1) {
                            assignedId = nextObjectId++;
                        }

                        // Smoothing and flicker suppression
                        BoxWithID smoothedBox;
                        if (lastTrackedBoxes.containsKey(assignedId)) {
                            BoxWithID prevBox = lastTrackedBoxes.get(assignedId);

                            int smoothMinX = (int)(SMOOTHING_ALPHA * prevBox.minX + (1 - SMOOTHING_ALPHA) * minX);
                            int smoothMinY = (int)(SMOOTHING_ALPHA * prevBox.minY + (1 - SMOOTHING_ALPHA) * minY);
                            int smoothMaxX = (int)(SMOOTHING_ALPHA * prevBox.maxX + (1 - SMOOTHING_ALPHA) * maxX);
                            int smoothMaxY = (int)(SMOOTHING_ALPHA * prevBox.maxY + (1 - SMOOTHING_ALPHA) * maxY);

                            double widthChange = Math.abs((maxX - minX) - (prevBox.maxX - prevBox.minX));
                            double heightChange = Math.abs((maxY - minY) - (prevBox.maxY - prevBox.minY));

                            if (widthChange < FLICKER_THRESHOLD && heightChange < FLICKER_THRESHOLD) {
                                smoothedBox = prevBox; // ignore small flicker
                            } else {
                                smoothedBox = new BoxWithID(assignedId, smoothMinX, smoothMinY, smoothMaxX, smoothMaxY);
                            }
                        } else {
                            smoothedBox = new BoxWithID(assignedId, minX, minY, maxX, maxY);
                        }

                        lastTrackedBoxes.put(assignedId, smoothedBox);
                        currentBoxes.add(smoothedBox);

                        Color color = idToColor.computeIfAbsent(assignedId, k -> getColorForIndex(k));
                        frame = addBoundingBox(frame, smoothedBox, color);
                    }

                    previousBoxes = currentBoxes;
                }

                String outputFileName = "frame_" + (i + 1) + ".png";
                File outputFile = new File(outputFolderPath + "/" + outputFileName);
                ImageIO.write(frame, "png", outputFile);
                System.out.println("Saved frame: " + outputFile.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BufferedImage addBoundingBox(BufferedImage frame, BoxWithID box, Color boxColor) {
        int width = frame.getWidth();
        int height = frame.getHeight();

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();

        g2d.drawImage(frame, 0, 0, null);

        int minX = box.minX;
        int minY = box.minY;
        int maxX = box.maxX;
        int maxY = box.maxY;

        // Draw transparent overlay
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.setColor(boxColor);
        g2d.fillRect(minX, minY, maxX - minX, maxY - minY);

        // Draw bounding box outline
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(minX, minY, maxX - minX, maxY - minY);

        // Draw object ID
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.setColor(Color.WHITE);
        g2d.drawString("ID " + box.id, minX + 5, minY + 20);

        g2d.dispose();
        return result;
    }

    private Color getColorForIndex(int i) {
        Color[] colors = {
                Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE,
                Color.MAGENTA, Color.CYAN, Color.PINK, Color.YELLOW,
                new Color(128, 0, 128), new Color(0, 128, 128)
        };
        return colors[i % colors.length];
    }
}
