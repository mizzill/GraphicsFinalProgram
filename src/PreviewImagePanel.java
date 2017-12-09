import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.io.*;

public class PreviewImagePanel extends JPanel {

    // The ImageViewController instance to draw information from
    private ImageViewController ivc;

    // A local array to store animated control points
    private Point[] controlPoints;

    // The buffered image on which to draw the morph
    private BufferedImage morphImage;

    // Constructor
    public PreviewImagePanel(ImageViewController ivc) {

        // Get a reference to the image view controller
        this.ivc = ivc;

        // Copy the source image's control points into a local array for modification
        controlPoints = Arrays.copyOf(ivc.src.controlPoints, ivc.src.controlPoints.length);

        // Resize the panel
        //setBackground(Color.black);
        int width = Math.max(ivc.src.getImage().getWidth(), ivc.dest.getImage().getWidth());
        int height = Math.max(ivc.src.getImage().getHeight(), ivc.dest.getImage().getHeight());
        setPreferredSize(new Dimension(width, height));

        // Set up the buffered image
        morphImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

    }

    // Updates the control points
    public void update(double percentCompleted) {

        // Create the triangle arrays
        Triangle[] srcTriangles = new Triangle[4];
        Triangle[] destTriangles = new Triangle[4];

        // Get copies of the images
        BufferedImage srcImage = ivc.src.getImage();
        BufferedImage destImage = ivc.dest.getImage();

        // Interpolate the new position of each control point
        for (int i = 0; i < controlPoints.length; ++i) {

            // Move the control point
            double _x = ivc.src.controlPoints[i].x + ((ivc.dest.controlPoints[i].x - ivc.src.controlPoints[i].x) * percentCompleted);
            double _y = ivc.src.controlPoints[i].y + ((ivc.dest.controlPoints[i].y - ivc.src.controlPoints[i].y) * percentCompleted);
            controlPoints[i] = new Point((int)_x, (int)_y);

            // Create the top triangle
            srcTriangles[0] = new Triangle(
                    ivc.src.controlPoints[i].x,
                    ivc.src.controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x,
                    ivc.src.gridCellCoords[i].y,
                    ivc.src.gridCellCoords[i].x + ivc.src.gridCellWidth,
                    ivc.src.gridCellCoords[i].y
            );

            destTriangles[0] = new Triangle(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.dest.gridCellCoords[i].x,
                    ivc.dest.gridCellCoords[i].y,
                    ivc.dest.gridCellCoords[i].x + ivc.dest.gridCellWidth,
                    ivc.dest.gridCellCoords[i].y
            );

            // Left triangle
            srcTriangles[1] = new Triangle(
                    ivc.src.controlPoints[i].x,
                    ivc.src.controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x,
                    ivc.src.gridCellCoords[i].y,
                    ivc.src.gridCellCoords[i].x,
                    ivc.src.gridCellCoords[i].y + ivc.src.gridCellHeight
            );

            destTriangles[1] = new Triangle(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.dest.gridCellCoords[i].x,
                    ivc.dest.gridCellCoords[i].y,
                    ivc.dest.gridCellCoords[i].x,
                    ivc.dest.gridCellCoords[i].y + ivc.dest.gridCellHeight
            );

            // Bottom triangle
            srcTriangles[2] = new Triangle(
                    ivc.src.controlPoints[i].x,
                    ivc.src.controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x,
                    ivc.src.gridCellCoords[i].y + ivc.src.gridCellHeight,
                    ivc.src.gridCellCoords[i].x + ivc.src.gridCellWidth,
                    ivc.src.gridCellCoords[i].y + ivc.src.gridCellHeight
            );

            destTriangles[2] = new Triangle(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.dest.gridCellCoords[i].x,
                    ivc.dest.gridCellCoords[i].y + ivc.dest.gridCellHeight,
                    ivc.dest.gridCellCoords[i].x + ivc.dest.gridCellWidth,
                    ivc.dest.gridCellCoords[i].y + ivc.dest.gridCellHeight
            );

            // Right triangle
            srcTriangles[3] = new Triangle(
                    ivc.src.controlPoints[i].x,
                    ivc.src.controlPoints[i].y,
                    ivc.src.gridCellCoords[i].x + ivc.src.gridCellWidth,
                    ivc.src.gridCellCoords[i].y,
                    ivc.src.gridCellCoords[i].x + ivc.src.gridCellWidth,
                    ivc.src.gridCellCoords[i].y + ivc.src.gridCellHeight
            );

            destTriangles[3] = new Triangle(
                    controlPoints[i].x,
                    controlPoints[i].y,
                    ivc.dest.gridCellCoords[i].x + ivc.dest.gridCellWidth,
                    ivc.dest.gridCellCoords[i].y,
                    ivc.dest.gridCellCoords[i].x + ivc.dest.gridCellWidth,
                    ivc.dest.gridCellCoords[i].y + ivc.dest.gridCellHeight
            );


            MorphTools.warpTriangle(srcImage, destImage, morphImage, srcTriangles[0], destTriangles[0], (float)percentCompleted, null, null);
            MorphTools.warpTriangle(srcImage, destImage, morphImage, srcTriangles[1], destTriangles[1], (float)percentCompleted, null, null);
            MorphTools.warpTriangle(srcImage, destImage, morphImage, srcTriangles[2], destTriangles[2], (float)percentCompleted, null, null);
            MorphTools.warpTriangle(srcImage, destImage, morphImage, srcTriangles[3], destTriangles[3], (float)percentCompleted, null, null);

        }
        try {
            int percent = (int)(percentCompleted *10);
            File outputfile = new File("Frames/tween" + percent + ".jpg");
            ImageIO.write(morphImage, "jpg", outputfile);
        }
        catch (IOException e){
            System.out.println("Unable to write file");
        }
        repaint();

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D)g;
        g2d.drawImage(morphImage, 0, 0, this);
    }

    // Resets the control points to their positions from the source image
    public void reset() {
        controlPoints = Arrays.copyOf(ivc.src.controlPoints, ivc.src.controlPoints.length);
        morphImage.getGraphics().clearRect(0, 0, morphImage.getWidth(), morphImage.getHeight());

    }

}
