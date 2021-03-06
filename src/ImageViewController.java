public class ImageViewController {

    // The Image Views to control
    public ImageView src, dest;

    // The number of grid rows and columns in each image view
    public int gridResolution = 10;

    // The radius of each control point in an image view
    public final int pointRadius = 5;

    // Constructor
    public ImageViewController() { }

    // Sets the views to watch over
    public void setViews(ImageView src, ImageView dest) {
        this.src = src;
        this.dest = dest;
    }

    /* Updates the "selected" property on both the clicked point
       and the corresponding point on the other image */
    public void updateSelected(int clickedPoint) {
        src.updateSelectedExternally(clickedPoint);
        dest.updateSelectedExternally(clickedPoint);

        src.repaint();
        dest.repaint();
    }

    public void changeGridResolution(int resolution) {
        gridResolution = resolution;
        src.setupControlGrid();
        dest.setupControlGrid();
    }

    public void loadControlPoints(int[] srcX, int[] srcY, int[] destX, int[] destY) {
        src.updateControlPoints(srcX, srcY);
        dest.updateControlPoints(destX, destY);
    }
}
