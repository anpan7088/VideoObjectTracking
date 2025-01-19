# VideoObjectTracking

This project implements a basic object-tracking system to detect and visualize movement in video frames by analyzing pixel intensity differences. The application highlights areas of motion in red and draws bounding boxes around detected objects.

## Features
- Extracts frames from a video.
- Compares consecutive frames to detect motion based on pixel intensity changes.
- Draws bounding boxes around moving objects.

## Requirements
- Java Development Kit (JDK)
- Any Java IDE IntelliJ

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

