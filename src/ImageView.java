import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.*;

public class ImageView extends JComponent {

    // Instance variable to hold the buffered image
    private BufferedImage bim = null;
    private BufferedImage original = null;

    // The size of each grid cell
    public int gridCellWidth;
    public int gridCellHeight;

    // An array to store all control points
    public Point[] controlPoints;

    // An array to store the top-left coordinates of each grid cell
    public Point[] gridCellCoords;

    // The selected control point index
    private int selected = -1;

    //A View Controller that handles communication between the two panels
    private ImageViewController ivc;

    // This constructor stores a buffered image passed in as a parameter and an ImageView Controller that handles
    // communication between ImageViews
    public ImageView(BufferedImage img, ImageViewController ivc) {
        this.ivc = ivc;
        setImage(img);
        setupMouseListeners();
    }

    // This mutator changes the stored image and updates control points
    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        original = copyImage(bim);

        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
        setupControlGrid();
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
        for (int i = 0; i <= ivc.gridResolution; ++i) {
            g2d.drawLine(0, i * gridCellHeight, ivc.gridResolution * gridCellWidth, i * gridCellHeight);
            g2d.drawLine(i * gridCellWidth, 0, i * gridCellWidth, ivc.gridResolution * gridCellHeight);
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
        gridCellHeight = bim.getHeight() / ivc.gridResolution;
        gridCellWidth = bim.getWidth() / ivc.gridResolution;

        // Calculate the offset
        int offsetX = (gridCellWidth / 2);
        int offsetY = (gridCellHeight / 2);

        // Create the grid cell coordinate array
        gridCellCoords = new Point[ivc.gridResolution * ivc.gridResolution];

        // Set up the control point array
        controlPoints = new Point[ivc.gridResolution * ivc.gridResolution];

        // Put points into the arrays
        for (int i = 0; i < controlPoints.length; ++i) {
            int px = (i % ivc.gridResolution) * gridCellWidth;
            int py = (i / ivc.gridResolution) * gridCellHeight;
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
                    if (controlPoints[selected].x < gridCellCoords[selected].x + 1) {
                        controlPoints[selected].x = gridCellCoords[selected].x + 1;
                    }
                    else if (controlPoints[selected].x > gridCellCoords[selected].x + gridCellWidth - 1) {
                        controlPoints[selected].x = gridCellCoords[selected].x + gridCellWidth - 1;
                    }

                    if (controlPoints[selected].y < gridCellCoords[selected].y + 1) {
                        controlPoints[selected].y = gridCellCoords[selected].y + 1;
                    }
                    else if (controlPoints[selected].y > gridCellCoords[selected].y + gridCellHeight - 1) {
                        controlPoints[selected].y = gridCellCoords[selected].y + gridCellHeight - 1;
                    }

                    // Paint the scene again
                    repaint();
                }
            }
        });

    }

    public void changeBrightness(float brightness){
        RescaleOp op = new RescaleOp(brightness, 0, null);
        op.filter(original, bim);
        repaint();
    }

    public BufferedImage getImage() {
        return copyImage(bim);
    }

    public static BufferedImage copyImage(BufferedImage source){
        BufferedImage b = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());
        Graphics g = b.getGraphics();
        g.drawImage(source, 0, 0, null);
        g.dispose();
        return b;
    }

    // Called from the Image View Controller to update the selected control point from the other grid
    public void updateSelectedExternally(int clickedPoint){
        this.selected = clickedPoint;
    }

    public Point[] getControlPoints() {
        return controlPoints;
    }
    public void updateControlPoints(int[] x, int[] y) {
        for(int i = 0; i < x.length; i ++) {
            controlPoints[i].x = x[i];
            controlPoints[i].y = y[i];
        }
        repaint();
    }
}