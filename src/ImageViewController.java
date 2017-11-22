public class ImageViewController {

    private ImageView src, dest;

    // Constructor
    public ImageViewController(ImageView src, ImageView dest) {
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
}
