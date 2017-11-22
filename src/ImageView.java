import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.*;

public class ImageView extends JComponent {

    // Instance variable to hold the buffered image
    private BufferedImage bim = null;

    // The size of each grid cell
    public int gridCellWidth;
    public int gridCellHeight;

    // The offset of the center point within a grid cell
    private int offsetX;
    private int offsetY;

    // An array to store all control points
    public Point[] controlPoints;

    // An array to store the top-left coordinates of each grid cell
    public Point[] gridCellCoords;

    // The selected control point index
    private int selected = -1;

    //A View Controller that handles communication between the two panels
    private ImageViewController ivc;

    // This constructor stores a buffered image passed in as a parameter, also an ImageView Controller that handles
    // communication between ImageViews
    public ImageView(BufferedImage img, ImageViewController ivc) {
        this.ivc = ivc;
        // Create the grid cell coordinate array
        gridCellCoords = new Point[ivc.gridRows * ivc.gridCols];

        // Set up the control point array
        controlPoints = new Point[ivc.gridRows * ivc.gridCols];
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
        super.paintComponent(g);

        // Get the graphics context
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);

        // Draw the image onto the view
        g2d.drawImage(bim, 0, 0, this);

        // Draw grid lines
        for (int i = 0; i <= ivc.gridRows; ++i) {
            g2d.drawLine(0, i * gridCellHeight, ivc.gridCols * gridCellWidth, i * gridCellHeight);
        }

        for (int i = 0; i <= ivc.gridCols; ++i) {
            g2d.drawLine(i * gridCellWidth, 0, i * gridCellWidth, ivc.gridRows * gridCellHeight);
        }

        // Draw control points and their corresponding lines
        for (int i = 0; i < controlPoints.length; ++i) {

            // Draw the lines
            g2d.setColor(Color.WHITE);
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x,
                    gridCellCoords[i].y
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x + gridCellWidth,
                    gridCellCoords[i].y + gridCellHeight
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x,
                    gridCellCoords[i].y + gridCellHeight
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x + gridCellWidth,
                    gridCellCoords[i].y
            );

            // Draw the control point
            if (i == selected) { g2d.setColor(Color.RED); }
            g2d.fillOval(
                    controlPoints[i].x - ivc.pointRadius,
                    controlPoints[i].y - ivc.pointRadius,
                    ivc.pointRadius * 2,
                    ivc.pointRadius * 2
            );
        }

    }

    // Creates the grid and control point arrays
    public void setupControlGrid() {

        // Determine the appropriate distance between control points
        gridCellHeight = bim.getHeight() / ivc.gridRows;
        gridCellWidth = bim.getWidth() / ivc.gridCols;

        // Calculate the offset
        offsetX = (gridCellWidth / 2);
        offsetY = (gridCellHeight / 2);


        // Put points into the arrays
        for (int i = 0; i < controlPoints.length; ++i) {
            int px = (i % ivc.gridCols) * gridCellWidth;
            int py = (i / ivc.gridCols) * gridCellHeight;
            gridCellCoords[i] = new Point(px, py);
            controlPoints[i] = new Point(px + offsetX, py + offsetY);
        }

        // Update the image view
        repaint();

    }

    // Sets up the control points for interaction
    private void setupMouseListeners() {

        // Listen for clicking/releasing
        addMouseListener(new MouseAdapter() {

            // Selecting a point
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                for (int i = 0; i < controlPoints.length; ++i) {
                    if (Point.distance(e.getX(), e.getY(), controlPoints[i].x, controlPoints[i].y) <= ivc.pointRadius * 2) {
                        selected = i;
                        ivc.updateSelected(selected);
                        break;
                    }
                }
            }

            // Unselecting a point
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                selected = -1;
                ivc.updateSelected(selected);
                repaint();
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
                    if (controlPoints[selected].x < gridCellCoords[selected].x) {
                        controlPoints[selected].x = gridCellCoords[selected].x;
                    }
                    else if (controlPoints[selected].x > gridCellCoords[selected].x + gridCellWidth) {
                        controlPoints[selected].x = gridCellCoords[selected].x + gridCellWidth;
                    }

                    if (controlPoints[selected].y < gridCellCoords[selected].y) {
                        controlPoints[selected].y = gridCellCoords[selected].y;
                    }
                    else if (controlPoints[selected].y > gridCellCoords[selected].y + gridCellHeight) {
                        controlPoints[selected].y = gridCellCoords[selected].y + gridCellHeight;
                    }

                    // Paint the scene again
                    repaint();
                }
            }
        });

    }

    // Called from the Image View Controller to update the selected control point from the other grid
    public void updateSelectedExternally(int clickedPoint){
        this.selected = clickedPoint;
    }

    // Sets the ImageViewController associated with this instance
    public void setController(ImageViewController ivc){
        this.ivc = ivc;
    }
}