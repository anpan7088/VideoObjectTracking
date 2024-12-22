package com.angelapanovska.VN1;

import java.io.File;

public class Main {

    public static void main(String[] args) {

	String framesFolderPath = "\"C:\\Users\\38976\\Desktop\\penguins\\frames\""; //folder where the extracted frames are save

    String outputFolderPath = "\"C:\\Users\\38976\\Desktop\\penguins\\NewFrames\""; // folder path where the new analyzed frames will be saved

    File outputFolder = new File (outputFolderPath);
    if (!outputFolder.exists()){
        outputFolder.mkdir(); // checking output folder exists
    }
    System.out.println("Starting the video object tracking process...");

    FrameProcessor frameProcessor = new FrameProcessor(framesFolderPath,  outputFolderPath);
    frameProcessor.processFrames ();

    System.out.println("Sequential processing completed!");
    }
}
