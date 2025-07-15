/*89211031 - Angela Panovska
 * Sequential part: 
 * 1. Extracted frames from the penguin video(could be any video) with the given app FFmpeg.
 * 2. Sucessfully, extracting the new frames in new folder where its going to be used in the future with this command: 
 * ffmpeg -i penguins.mp4 -vf fps=1 -q:v 2 frames/frame_%04d.png to make the final video.
 * 3. Wrote an alghoritm to track the objects with any movements, all pixels are colored in yellow color and saved in new folder.
 * 4. The algorithm for bounding boxes is imperfect and it is detecting moving objects and drawing rectangles, but then the frames are probablt bigger different size
 * colors etc... And for multiple frames it doesnt work for me for some reason. It works for 2 frames for now.
 * 
 * Github: https://github.com/anpan7088/VideoObjectTracking/tree/Main
 */
package com.angelapanovska.VN1;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {

    public static void main(String[] args) {
    
    String framesFolderPath = "C:/Users/38976/Desktop/penguins/frames";//folder where the extracted frames are save
	 
    String outputFolderPath = "C:/Users/38976/Desktop/penguins/NewFrames"; // folder path where the new analyzed frames will be saved

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
