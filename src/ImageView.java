import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.*;

public class ImageView extends JComponent {

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

    // The selected control point index
    private int selected = -1;

    // This constructor stores a buffered image passed in as a parameter
    public ImageView(BufferedImage img) {

        setImage(img);
        setupMouseListeners();

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

        // Get the graphics context
        Graphics2D big = (Graphics2D) g;
        big.setColor(Color.white);

        // Draw the image onto the view
        big.drawImage(bim, 0, 0, this);

        // Draw grid lines
        for (int i = 0; i <= gridRows; ++i) {
            big.drawLine(0, i * gridCellHeight, gridCols * gridCellWidth, i * gridCellHeight);
        }

        for (int i = 0; i <= gridCols; ++i) {
            big.drawLine(i * gridCellWidth, 0, i * gridCellWidth, gridRows * gridCellHeight);
        }

        // Draw control points and their corresponding lines
        for (int i = 0; i < controlPoints.length; ++i) {
            // Draw the point
            big.fillOval(
                    controlPoints[i].x - pointRadius,
                    controlPoints[i].y - pointRadius,
                    pointRadius * 2,
                    pointRadius * 2
            );

            // Draw the lines
            big.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x,
                    gridCellCoords[i].y
            );
            big.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x + gridCellWidth,
                    gridCellCoords[i].y + gridCellHeight
            );
            big.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x,
                    gridCellCoords[i].y + gridCellHeight
            );
            big.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x + gridCellWidth,
                    gridCellCoords[i].y
            );
        }

    }

    // Creates the grid and control point arrays
    public void setupControlGrid() {

        // Determine the appropriate distance between control points
        gridCellHeight = bim.getHeight() / gridRows;
        gridCellWidth = bim.getWidth() / gridCols;

        // Calculate the offset
        offsetX = (gridCellWidth / 2);
        offsetY = (gridCellHeight / 2);

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

    // Sets up the control points for interaction
    private void setupMouseListeners() {

        addMouseListener(new MouseAdapter() {

            // Selecting a point
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                for (int i = 0; i < controlPoints.length; ++i) {
                    if (Point.distance(e.getX(), e.getY(), controlPoints[i].x, controlPoints[i].y) <= pointRadius * 2) {
                        selected = i;
                        break;
                    }
                }
            }

            // Unselecting a point
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                selected = -1;
            }

        });

        // Listens for mouse dragging
        addMouseMotionListener(new MouseMotionAdapter() {

            // Drag the selected point
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);

                // If a point is selected
                if (selected != -1) {

                    // Move the point to the cursor
                    controlPoints[selected] = e.getPoint();

                    // Make sure the point stays in bounds
                    if (controlPoints[selected].x < 0) {
                        controlPoints[selected].x = 0;
                    }
                    else if (controlPoints[selected].x > bim.getWidth()) {
                        controlPoints[selected].x = bim.getWidth();
                    }

                    if (controlPoints[selected].y < 0) {
                        controlPoints[selected].y = 0;
                    }
                    else if (controlPoints[selected].y > bim.getHeight()) {
                        controlPoints[selected].y = bim.getHeight();
                    }

                    // Paint the scene again
                    repaint();
                }
            }
        });

    }
}