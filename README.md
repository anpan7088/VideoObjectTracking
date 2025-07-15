# VideoObjectTracking

This project implements a basic object-tracking system to detect and visualize movement in video frames by analyzing pixel intensity differences. The application highlights areas of motion in red and draws bounding boxes around detected objects.

## Features
- Extracts frames from a video.
- Compares consecutive frames to detect motion based on pixel intensity changes.
- Visualizes detected motion with red highlights.
- Draws bounding boxes around areas with significant motion.

## Requirements
- Java Development Kit (JDK)
- Any Java IDE (e.g., IntelliJ, Eclipse, etc.)
- External Libraries (if applicable):
  - OpenCV (optional for advanced image processing)

## How It Works
1. **Frame Extraction:**
   The video is broken down into individual frames for processing.

2. **Pixel Comparison:**
   Each pixel in the current frame is compared to the corresponding pixel in the previous frame to calculate the intensity difference.

3. **Thresholding:**
   A threshold value is used to determine significant changes:
   - **Red Highlight Threshold:** Visualizes small pixel intensity differences.
   - **Detection Threshold:** Identifies areas of significant motion to draw bounding boxes.

4. **Bounding Boxes:**
   Draws rectangles around detected regions of motion.

## Usage
### 1. Clone the Repository
```bash
git clone https://github.com/anpan7088/VideoObjectTracking.git
cd VideoObjectTracking
```

### 2. Compile the Code
Use your IDE or the terminal to compile the Java files.

### 3. Run the Application
```bash
java VideoObjectTracking
```

### 4. Adjust Parameters
You can tweak the following parameters in the code to refine results:
- **`highlightThreshold`**: Controls sensitivity for red highlights.
- **`detectionThreshold`**: Adjusts sensitivity for bounding box detection.
- **Bounding Box Size Filters**: Minimum width and height for detected objects.

## Future Enhancements
- Adaptive thresholding based on average pixel intensity difference.
- Morphological filtering (erosion and dilation) to reduce noise and enhance detection.
- Real-time processing using webcam input.

## Example Output
The program outputs a video with detected motion highlighted in red and bounding boxes around moving objects.

## Contribution
Feel free to contribute by submitting issues or pull requests. Suggestions for improvement are always welcome!

## License
This project is licensed under the MIT License. See the LICENSE file for details.

# Sequential Branch - Video Object Tracking Project

## **Project Description**
This project is designed to process sequential video frames and detect moving objects by comparing consecutive frames. The moving objects are highlighted using red overlays to visualize the detected motion.

## **Features**
- Processes sequential images (e.g., `.png` or `.jpg` format) from an input folder.
- Detects movement by analyzing pixel intensity differences between consecutive frames.
- Highlights detected motion in red on the processed frames.
- Outputs processed frames to a specified folder.

## **How to Run**
### Prerequisites
- Ensure you have the following installed:
  - Java Development Kit (JDK) version 8 or higher.
  - An IDE such as IntelliJ IDEA or Eclipse (optional but recommended).

### Input Requirements
- A folder containing sequential video frames in `.png` or `.jpg` format.
- Ensure the frames are named sequentially (e.g., `frame1.png`, `frame2.png`, etc.).

### Steps to Run
1. **Setup**:
   - Clone the project repository and switch to the `sequential` branch.
   - Ensure the input folder path and output folder path are correctly set in the code.

2. **Run the Program**:
   - Compile and run the `Main` class.
   - Provide the following inputs when prompted:
     - Path to the folder containing the input frames.
     - Path to the folder where the output frames will be saved.

3. **Output**:
   - Processed frames will be saved in the specified output folder, with motion highlighted in red.

## **Code Structure**
- **`FrameProcessor`**: Handles frame-by-frame motion detection by calculating pixel differences.
  - Outputs bounding box coordinates and identifies moving objects.

- **`BoundingBoxDrawer`**: Visualizes detected motion by drawing bounding boxes on detected moving objects.

- **`Main`**: Entry point for running the sequential processing workflow.

## **Limitations**
- Bounding box is something that still needs to be worked on since it has to have different colors for different  movingobjects.
- Requires sequentially named frames for proper operation.

## **Issues**
- Bounding boxes may be misaligned or incorrectly sized due to bugs and not completed algorithm.
- Detection sensitivity is dependent on thresholds, which may need tuning for different input videos.




