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

- **`BoundingBoxDrawer`**: Visualizes detected motion by drawing red overlays or bounding boxes on frames (currently experimental).

- **`Main`**: Entry point for running the sequential processing workflow.

## **Limitations**
- Bounding box functionality is experimental and may not accurately represent detected objects.
- Requires sequentially named frames for proper operation.

## **Known Issues**
- Bounding boxes may be misaligned or incorrectly sized due to incomplete implementation.
- Detection sensitivity is dependent on thresholds, which may need tuning for different input videos.

## **Future Improvements**
- Debug and refine bounding box placement.
- Add support for customizable sensitivity thresholds.

## **Contact**
If you have any questions or issues, please contact Angela Panovska at [your email here].

