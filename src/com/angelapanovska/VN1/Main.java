// installing SDK adoptium so my projects compile...:)

package com.angelapanovska.VN1;

import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String videoInputPath = "penguins/video.mp4";
        String framesFolderPath = "penguins/frames";
        String outputFolderPath = "penguins/NewFrames";
        String outputVideoPath = "penguins/output_video.mp4";

        long totalStartTime = System.currentTimeMillis();

        try {
            // STEP 1: Extract frames from video using FFmpeg
            long startExtract = System.currentTimeMillis();
            extractFramesWithFFmpeg(videoInputPath, framesFolderPath);
            long endExtract = System.currentTimeMillis();
            System.out.println("Frame extraction time: " + (endExtract - startExtract) + " ms");

            // STEP 2: Process frames to detect moving objects
            long startProcessing = System.currentTimeMillis();
            FrameProcessor frameProcessor = new FrameProcessor(framesFolderPath);
            List<List<BoxWithID>> boundingBoxes = frameProcessor.processFrames();
            long endProcessing = System.currentTimeMillis();
            System.out.println("Frame analysis time: " + (endProcessing - startProcessing) + " ms");

            // Ensure output folder exists
            File outputFolder = new File(outputFolderPath);
            if (!outputFolder.exists()) {
                outputFolder.mkdir();
            }

            // STEP 3: Draw bounding boxes
            long startDrawing = System.currentTimeMillis();
            BoundingBoxDrawer drawer = new BoundingBoxDrawer(framesFolderPath, outputFolderPath);
            drawer.drawBoundingBoxes(boundingBoxes);
            long endDrawing = System.currentTimeMillis();
            System.out.println("Bounding box drawing time: " + (endDrawing - startDrawing) + " ms");

            // STEP 4: Combine annotated frames into video
            long startCombine = System.currentTimeMillis();
            reassembleFramesWithFFmpeg(outputFolderPath, outputVideoPath);
            long endCombine = System.currentTimeMillis();
            System.out.println("Video recombination time: " + (endCombine - startCombine) + " ms");

            long totalEndTime = System.currentTimeMillis();
            System.out.println("Total processing time: " + (totalEndTime - totalStartTime) + " ms");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void extractFramesWithFFmpeg(String videoPath, String outputFolderPath) throws Exception {
        File folder = new File(outputFolderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", videoPath, "-q:v", "2", outputFolderPath + "/frame_%d.png"
        );
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }

    private static void reassembleFramesWithFFmpeg(String inputFolderPath, String outputVideoPath) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-framerate", "10", "-i",
                inputFolderPath + "/frame_%d.png",
                "-c:v", "libx264", "-pix_fmt", "yuv420p", outputVideoPath
        );
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }
}
