import com.sun.corba.se.impl.orbutil.graph.Graph;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;

public class MyImageObj extends JLabel {

    // Instance variable to hold the buffered image
    private BufferedImage bim = null;

    // The number of grid rows and columns
    private int gridWidth = 10;
    private int gridHeight = 10;

    // The distance between each grid cell
    private int dx = -1;
    private int dy = -1;

    // The diameter of each control point
    private int diameter = 10;

    // An array to store all control points
    private Point[] controlPoints;

    // This constructor stores a buffered image passed in as a parameter
    public MyImageObj(BufferedImage img) {
        bim = img;

        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));

        setupControlPoints();

        this.repaint();
    }

    // This mutator changes the stored image and updates control points
    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
        setupControlPoints();
        this.repaint();
    }

    // Accessor to get a handle to the bufferedimage object stored here
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

        // Draw grid lines
        for (int i = 0; i < gridWidth; ++i) {
            bimGfx.drawLine(i * dx, 0, i * dx, getHeight());
        }

        for (int i = 0; i < gridHeight; ++i) {
            bimGfx.drawLine(0, i * dy, getWidth(), i * dy);
        }

        // Draw control points
        for (int i = 0; i < controlPoints.length; ++i) {
            bimGfx.drawOval(controlPoints[i].x, controlPoints[i].y, diameter, diameter);
        }

        // Draw the image onto the view
        Graphics2D big = (Graphics2D) g;
        big.drawImage(bim, 0, 0, this);

    }

    // Creates a new control point array and populates it
    private void setupControlPoints() {

        // Determine the appropriate distance between control points
        dx = bim.getWidth() / gridWidth;
        dy = bim.getHeight() / gridHeight;

        // Set up the control points array
        controlPoints = new Point[gridWidth * gridHeight];
        for (int i = 0; i < controlPoints.length; ++i) {
            int px = (i % gridWidth) * dx;
            int py = (i / gridWidth) * dy;
            int offsetX = (dx / 2) - (diameter / 2);
            int offsetY = (dy / 2) - (diameter / 2);
            controlPoints[i] = new Point(px + offsetX, py + offsetY);
        }

        // Update the image view
        repaint();

    }
}