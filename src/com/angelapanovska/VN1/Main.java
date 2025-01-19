package com.angelapanovska.VN1;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {

    String inputFolderPath = "C://Users//38976//Desktop//penguins//frames/";//folder where the extracted frames are save
	 
    String outputFolderPath = "C://Users//38976//Desktop//penguins//NewFrames/"; // folder path where the new analyzed frames will be saved

    File outputFolder = new File (outputFolderPath);
    if (!outputFolder.exists()){
        outputFolder.mkdir(); // checking output folder exists
    }
    System.out.println("Starting the video object tracking process...");

    // First step : Process frames to get the boxes
    FrameProcessor frameProcessor = new FrameProcessor(inputFolderPath);
    List<List<Map<String, Integer>>> boundingBoxes = frameProcessor.processFrames();

    //Second step: Draw bounding boxes on frames
    BoundingBoxDrawer drawer = new BoundingBoxDrawer(inputFolderPath, outputFolderPath);
    drawer.drawBoundingBoxes(boundingBoxes);
    
    System.out.println("Object tracking and box drawing completed.");
    }
}
