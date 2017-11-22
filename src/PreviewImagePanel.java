import javax.swing.*;
import java.awt.*;

public class PreviewImagePanel extends JPanel {

    // Information taken from the source image view
    private int rows;
    private int cols;
    private int cellHeight;
    private int cellWidth;
    private Point[] srcControlPoints;
    private Point[] destControlPoints;
    private Point[] gridCellCoords;
    private int pointRadius;

    private Point[] controlPoints;

    // Constructor
    public PreviewImagePanel(int rows, int cols, int cellHeight, int cellWidth, Point[] srcControlPoints, Point[] destControlPoints, Point[] gridCellCoords, int pointRadius) {

        this.rows = rows;
        this.cols = cols;
        this.cellHeight = cellHeight;
        this.cellWidth = cellWidth;
        this.srcControlPoints = srcControlPoints;
        this.destControlPoints = destControlPoints;
        this.gridCellCoords = gridCellCoords;
        this.pointRadius = pointRadius;

        // Start with source points
        controlPoints = srcControlPoints;

        // Resize the panel
        setBackground(Color.black);
        setPreferredSize(new Dimension(cols * cellWidth, rows * cellHeight));
    }

    // Updates the control points
    public void update(double percentCompleted) {
        for (int i = 0; i < controlPoints.length; ++i) {
            double _x = srcControlPoints[i].x + ((destControlPoints[i].x - srcControlPoints[i].x) * percentCompleted);
            double _y = srcControlPoints[i].y + ((destControlPoints[i].y - srcControlPoints[i].y) * percentCompleted);
            controlPoints[i] = new Point((int)_x, (int)_y);
        }
        repaint();
    }

    // Resets the image view
    public void reset() {
        update(0);
        controlPoints = srcControlPoints;
    }

    // Get a graphics context and show the image with the control point grid
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Get the graphics context
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);

        // Draw grid lines
        for (int i = 0; i <= rows; ++i) {
            g2d.drawLine(0, i * cellHeight, cols * cellWidth, i * cellHeight);
        }

        for (int i = 0; i <= cols; ++i) {
            g2d.drawLine(i * cellWidth, 0, i * cellWidth, rows * cellHeight);
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
                    gridCellCoords[i].x + cellWidth,
                    gridCellCoords[i].y + cellHeight
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x,
                    gridCellCoords[i].y + cellHeight
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    gridCellCoords[i].x + cellWidth,
                    gridCellCoords[i].y
            );

            // Draw the control point
            g2d.fillOval(
                    controlPoints[i].x - pointRadius,
                    controlPoints[i].y - pointRadius,
                    pointRadius * 2,
                    pointRadius * 2
            );
        }

    }
}
