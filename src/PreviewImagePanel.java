import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class PreviewImagePanel extends JPanel {

    // The ImageViewController instance to draw information from
    private ImageViewController ivc;

    // The height and width of a grid cell
    private int cellHeight;
    private int cellWidth;

    // A local array to store animated control points
    private Point[] controlPoints;

    // Constructor
    public PreviewImagePanel(ImageViewController ivc) {

        this.ivc = ivc;
        cellHeight = ivc.src.gridCellHeight;
        cellWidth = ivc.src.gridCellWidth;

        // Copy the source image's control points into a local array for modification
        controlPoints = Arrays.copyOf(ivc.src.controlPoints, ivc.src.controlPoints.length);

        // Resize the panel
        setBackground(Color.black);
        setPreferredSize(new Dimension(ivc.gridCols * cellWidth, ivc.gridRows * cellHeight));
    }

    // Updates the control points
    public void update(double percentCompleted) {

        // Interpolate the new position of each control point
        for (int i = 0; i < controlPoints.length; ++i) {
            double _x = ivc.src.controlPoints[i].x + ((ivc.dest.controlPoints[i].x - ivc.src.controlPoints[i].x) * percentCompleted);
            double _y = ivc.src.controlPoints[i].y + ((ivc.dest.controlPoints[i].y - ivc.src.controlPoints[i].y) * percentCompleted);
            controlPoints[i] = new Point((int)_x, (int)_y);
        }

        repaint();
    }

    // Resets the control points to their positions from the source image
    public void reset() {
        cellHeight = ivc.src.gridCellHeight;
        cellWidth = ivc.src.gridCellWidth;
        controlPoints = Arrays.copyOf(ivc.src.controlPoints, ivc.src.controlPoints.length);
    }

    // Get a graphics context and show the image with the control point grid
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Get the graphics context
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(Color.white);

        // Draw grid lines
        for (int i = 0; i <= ivc.gridRows; ++i) {
            g2d.drawLine(0, i * cellHeight, ivc.gridCols * cellWidth, i * cellHeight);
        }

        for (int i = 0; i <= ivc.gridCols; ++i) {
            g2d.drawLine(i * cellWidth, 0, i * cellWidth, ivc.gridRows * cellHeight);
        }

        // Draw control points and their corresponding lines
        for (int i = 0; i < controlPoints.length; ++i) {

            // Draw the lines
            g2d.setColor(Color.WHITE);
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x,
                    ivc.src.gridCellCoords[i].y
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x + cellWidth,
                    ivc.src.gridCellCoords[i].y + cellHeight
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x,
                    ivc.src.gridCellCoords[i].y + cellHeight
            );
            g2d.drawLine(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x + cellWidth,
                    ivc.src.gridCellCoords[i].y
            );

            // Draw the control point
            g2d.fillOval(
                    controlPoints[i].x - ivc.pointRadius,
                    controlPoints[i].y - ivc.pointRadius,
                    ivc.pointRadius * 2,
                    ivc.pointRadius * 2
            );
        }

    }
}
