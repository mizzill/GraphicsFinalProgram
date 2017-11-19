import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class MyImageObj extends JLabel {

    // Instance variable to hold the buffered image
    private BufferedImage bim = null;

    // The number of grid rows and columns
    private int gridRows = 10;
    private int gridCols = 10;

    // The size of each grid cell
    private int gridCellWidth = -1;
    private int gridCellHeight = -1;

    // The offset of the center point within a grid cell
    private int offsetX;
    private int offsetY;

    // The radius of each control point
    private int pointRadius = 5;

    // An array to store all control points
    private Point[] controlPoints;

    // An array to store the top-left coordinates of each grid cell
    private Point[] gridCellCoords;

    // This constructor stores a buffered image passed in as a parameter
    public MyImageObj(BufferedImage img) {
        bim = img;

        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));

        setupControlGrid();

        this.repaint();
    }

    // This mutator changes the stored image and updates control points
    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
        setupControlGrid();
        this.repaint();
    }

    // Accessor to get a handle to the BufferedImage object stored here
    public BufferedImage getImage() {
        return bim;
    }


    // Show current image by a scheduled call to paint()
    public void showImage() {
        if (bim == null) return;
        this.repaint();
    }

    // Get a graphics context and show the image with the control point grid
    public void paintComponent(Graphics g) {

        // Get the graphics context of the buffered image
        Graphics2D bimGfx = bim.createGraphics();
        bimGfx.setColor(Color.white);

        // Draw grid lines
        for (int i = 0; i <= gridRows; ++i) {
            bimGfx.drawLine(0, i * gridCellHeight, gridCols * gridCellWidth, i * gridCellHeight);
        }

        for (int i = 0; i <= gridCols; ++i) {
            bimGfx.drawLine(i * gridCellWidth, 0, i * gridCellWidth, gridRows * gridCellHeight);
        }

        // Draw control points and their corresponding lines
        for (int i = 0; i < controlPoints.length; ++i) {
            bimGfx.fillOval(controlPoints[i].x, controlPoints[i].y, pointRadius * 2, pointRadius * 2);
            bimGfx.drawLine(
                    controlPoints[i].x + pointRadius,
                    controlPoints[i].y + pointRadius,
                    gridCellCoords[i].x,
                    gridCellCoords[i].y
            );
            bimGfx.drawLine(
                    controlPoints[i].x + pointRadius,
                    controlPoints[i].y + pointRadius,
                    gridCellCoords[i].x + gridCellWidth,
                    gridCellCoords[i].y + gridCellHeight
            );
            bimGfx.drawLine(
                    controlPoints[i].x + pointRadius,
                    controlPoints[i].y + pointRadius,
                    gridCellCoords[i].x,
                    gridCellCoords[i].y + gridCellHeight
            );
            bimGfx.drawLine(
                    controlPoints[i].x + pointRadius,
                    controlPoints[i].y + pointRadius,
                    gridCellCoords[i].x + gridCellWidth,
                    gridCellCoords[i].y
            );
        }

        // Draw the image onto the view
        Graphics2D big = (Graphics2D) g;
        big.drawImage(bim, 0, 0, this);

    }

    // Creates the grid and control point arrays
    private void setupControlGrid() {

        // Determine the appropriate distance between control points
        gridCellHeight = bim.getHeight() / gridRows;
        gridCellWidth = bim.getWidth() / gridCols;

        // Calculate the offset
        offsetX = (gridCellWidth / 2) - pointRadius;
        offsetY = (gridCellHeight / 2) - pointRadius;

        // Create the grid cell coordinate array
        gridCellCoords = new Point[gridRows * gridCols];

        // Set up the control point array
        controlPoints = new Point[gridRows * gridCols];

        // Put points into the arrays
        for (int i = 0; i < controlPoints.length; ++i) {
            int px = (i % gridCols) * gridCellWidth;
            int py = (i / gridCols) * gridCellHeight;
            gridCellCoords[i] = new Point(px, py);
            controlPoints[i] = new Point(px + offsetX, py + offsetY);
        }

        // Update the image view
        repaint();

    }
}